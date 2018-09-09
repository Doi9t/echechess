/*
 *    Copyright 2014 - 2018 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.echechess.services;

import ca.watier.echechess.api.model.GenericPiecesModel;
import ca.watier.echechess.common.enums.*;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.responses.BooleanResponse;
import ca.watier.echechess.common.responses.DualValueResponse;
import ca.watier.echechess.common.sessions.Player;
import ca.watier.echechess.common.utils.Constants;
import ca.watier.echechess.engine.constraints.DefaultGameConstraint;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.engine.game.SimpleCustomPositionGameHandler;
import ca.watier.echechess.engine.interfaces.GameConstraint;
import ca.watier.echechess.redis.interfaces.GameRepository;
import ca.watier.echechess.redis.model.GenericGameHandlerWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

import static ca.watier.echechess.common.enums.ChessEventMessage.*;
import static ca.watier.echechess.common.enums.ChessEventMessage.PLAYER_TURN;
import static ca.watier.echechess.common.enums.Side.getOtherPlayerSide;
import static ca.watier.echechess.common.utils.Constants.*;

/**
 * Created by yannick on 4/17/2017.
 */

@Service
public class GameService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private static final BooleanResponse NO = BooleanResponse.NO;
    private final GameConstraint gameConstraint;
    private final WebSocketService webSocketService;
    private final GameRepository<GenericGameHandler> gameRepository;
    private final RedisTemplate<String, GenericGameHandlerWrapper> redisTemplate;
    private final ChannelTopic moveAppToNodeTopic;
    private final ChannelTopic availableMoveAppToNodeTopic;

    @Autowired
    public GameService(GameConstraint gameConstraint,
                       WebSocketService webSocketService,
                       GameRepository<GenericGameHandler> gameRepository,
                       RedisTemplate<String, GenericGameHandlerWrapper> redisTemplate,
                       ChannelTopic moveAppToNodeTopic,
                       ChannelTopic availableMoveAppToNodeTopic) {

        this.gameConstraint = gameConstraint;
        this.webSocketService = webSocketService;
        this.gameRepository = gameRepository;
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        this.moveAppToNodeTopic = moveAppToNodeTopic;
        this.availableMoveAppToNodeTopic = availableMoveAppToNodeTopic;
    }

    /**
     * Create a new game, and associate it to the player
     *
     * @param player
     * @param specialGamePieces - If null, create a {@link GenericGameHandler}
     * @param side
     * @param againstComputer
     * @param observers
     */
    public UUID createNewGame(Player player, String specialGamePieces, Side side, boolean againstComputer, boolean observers) {
        if (player == null || side == null) {
            throw new IllegalArgumentException();
        }

        GameType gameType = GameType.CLASSIC;
        GenericGameHandler genericGameHandler;

        if (StringUtils.isNotBlank(specialGamePieces)) {
            gameType = GameType.SPECIAL;

            SimpleCustomPositionGameHandler customPieceWithStandardRulesHandler = new SimpleCustomPositionGameHandler((DefaultGameConstraint) gameConstraint);
            customPieceWithStandardRulesHandler.setPieces(specialGamePieces);
            genericGameHandler = customPieceWithStandardRulesHandler;
        } else {
            genericGameHandler = new GenericGameHandler(gameConstraint);
        }

        UUID uui = UUID.randomUUID();
        genericGameHandler.setGameType(gameType);
        String uuidAsString = uui.toString();
        genericGameHandler.setUuid(uuidAsString);
        player.addCreatedGame(uui);

        genericGameHandler.setPlayerToSide(player, side);
        genericGameHandler.setAllowOtherToJoin(!againstComputer);
        genericGameHandler.setAllowObservers(observers);

        gameRepository.add(new GenericGameHandlerWrapper<>(uuidAsString, genericGameHandler));

        return uui;
    }

    public Map<UUID, GenericGameHandler> getAllGames() {
        Map<UUID, GenericGameHandler> values = new HashMap<>();

        for (GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper : gameRepository.getAll()) {
            values.put(UUID.fromString(genericGameHandlerWrapper.getId()), genericGameHandlerWrapper.getGenericGameHandler());
        }

        return values;
    }

    /**
     * Moves the piece to the specified location
     *
     * @param from
     * @param to
     * @param uuid
     * @param player
     * @return
     */
    public void movePiece(CasePosition from, CasePosition to, String uuid, Player player) {
        if (from == null || to == null || uuid == null || player == null) {
            throw new IllegalArgumentException();
        }

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Side playerSide = getPlayerSide(uuid, player);

        if (!gameFromUuid.hasPlayer(player) || gameFromUuid.isGamePaused() || gameFromUuid.isGameDraw()) {
            return;
        } else if (gameFromUuid.isGameDone()) {
            webSocketService.fireSideEvent(uuid, playerSide, GAME_WON_EVENT_MOVE, GAME_ENDED);
            return;
        } else if (KingStatus.STALEMATE.equals(gameFromUuid.getEvaluatedKingStatusBySide(playerSide))) {
            webSocketService.fireSideEvent(uuid, playerSide, GAME_WON_EVENT_MOVE, PLAYER_KING_STALEMATE);
            return;
        }

        //UUID|FROM|TO|ID_PLAYER_SIDE
        String payload = uuid + '|' + from + '|' + to + '|' + playerSide.getValue();
        redisTemplate.convertAndSend(moveAppToNodeTopic.getTopic(), payload);
    }

    /**
     * Get the game associated to the uuid
     *
     * @param uuid
     * @return
     */
    public GenericGameHandler getGameFromUuid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException();
        }

        GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper = gameRepository.get(uuid);

        return genericGameHandlerWrapper.getGenericGameHandler();
    }

    /**
     * Get the side of the player for the associated game
     *
     * @param uuid
     * @return
     */
    public Side getPlayerSide(String uuid, Player player) {
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return getGameFromUuid(uuid).getPlayerSide(player);
    }

    /**
     * Gets all possible moves for the selected piece
     *
     * @param from
     * @param uuid
     * @param player
     * @return
     */
    public void getAllAvailableMoves(CasePosition from, String uuid, Player player) {
        if (from == null || player == null || uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException();
        }

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Side playerSide = gameFromUuid.getPlayerSide(player);

        boolean isOnTheSameSide =
                Optional.ofNullable(gameFromUuid.getPiece(from))
                        .map(p -> p.getSide().equals(playerSide))
                        .orElse(false);

        if (!gameFromUuid.hasPlayer(player) || !isOnTheSameSide) {
            return;
        }

        String payload = uuid + '|' + from.name() + '|' + playerSide.getValue();
        redisTemplate.convertAndSend(availableMoveAppToNodeTopic.getTopic(), payload);
    }

    public BooleanResponse joinGame(String uuid, Side side, String uiUuid, Player player) {
        if (uiUuid == null || uiUuid.isEmpty() || player == null || uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException();
        }

        boolean joined = false;
        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);

        if (gameFromUuid == null) {
            return NO;
        }

        boolean allowObservers = gameFromUuid.isAllowObservers();
        boolean allowOtherToJoin = gameFromUuid.isAllowOtherToJoin();

        if ((!allowOtherToJoin && !allowObservers) ||
                (allowOtherToJoin && !allowObservers && Side.OBSERVER.equals(side)) ||
                (!allowOtherToJoin && (Side.BLACK.equals(side) || Side.WHITE.equals(side)))) {
            webSocketService.fireUiEvent(uiUuid, TRY_JOIN_GAME, NOT_AUTHORIZED_TO_JOIN);
            return NO;
        }

        UUID gameUuid = UUID.fromString(uuid);
        if (!player.getCreatedGameList().contains(gameUuid) && !player.getJoinedGameList().contains(gameUuid)) {
            joined = gameFromUuid.setPlayerToSide(player, side);
        }

        if (joined) {
            webSocketService.fireGameEvent(uuid, PLAYER_JOINED, String.format(NEW_PLAYER_JOINED_SIDE, side));
            webSocketService.fireUiEvent(uiUuid, PLAYER_JOINED, String.format(JOINING_GAME, uuid));
            player.addJoinedGame(gameUuid);

            gameRepository.add(new GenericGameHandlerWrapper<>(uuid, gameFromUuid));
        }

        return BooleanResponse.getResponse(joined);
    }

    public List<DualValueResponse> getPieceLocations(String uuid, Player player) {
        if (player == null || uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException();
        }

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);

        List<DualValueResponse> values = new ArrayList<>();

        if (gameFromUuid == null || !gameFromUuid.hasPlayer(player)) {
            return values;
        }

        for (Map.Entry<CasePosition, Pieces> casePositionPiecesEntry : gameFromUuid.getPiecesLocation().entrySet()) {
            values.add(new DualValueResponse(casePositionPiecesEntry.getKey(), casePositionPiecesEntry.getValue(), ""));
        }

        return values;
    }

    public boolean setSideOfPlayer(Player player, Side side, String uuid) {

        GenericGameHandler game = getGameFromUuid(uuid);
        boolean isGameExist = game != null;
        boolean response = false;

        if (isGameExist) {
            response = game.setPlayerToSide(player, side);
        }

        return isGameExist && response;
    }

    /**
     * Used when we need to upgrade a piece in the board (example: pawn promotion)
     *
     * @param uuid
     * @param piece
     * @param player
     * @return
     */
    public boolean upgradePiece(CasePosition to, String uuid, GenericPiecesModel piece, Player player) {
        if (player == null || uuid == null || uuid.isEmpty() || piece == null || to == null) {
            throw new IllegalArgumentException();
        }

        GenericGameHandler gameFromUuid = getGameFromUuid(uuid);
        Side playerSide = gameFromUuid.getPlayerSide(player);

        sendPawnPromotionMessage(uuid, playerSide, to);

        boolean isChanged = false;

        try {
            isChanged = gameFromUuid.upgradePiece(to, GenericPiecesModel.from(piece, playerSide), playerSide);

            if (isChanged) {
                webSocketService.fireGameEvent(uuid, SCORE_UPDATE, gameFromUuid.getGameScore()); //Refresh the points
                webSocketService.fireGameEvent(uuid, REFRESH_BOARD); //Refresh the boards
                webSocketService.fireSideEvent(uuid, getOtherPlayerSide(playerSide), PLAYER_TURN, Constants.PLAYER_TURN);
            }

        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.toString(), ex);
        }

        return isChanged;
    }

    private void sendPawnPromotionMessage(String uuid, Side playerSide, CasePosition to) {
        webSocketService.fireSideEvent(uuid, playerSide, PAWN_PROMOTION, to.name());
        webSocketService.fireGameEvent(uuid, PAWN_PROMOTION, String.format(GAME_PAUSED_PAWN_PROMOTION, playerSide));
    }
}
