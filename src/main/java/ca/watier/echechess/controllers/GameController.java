/*
 *    Copyright 2014 - 2017 Yannick Watier
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

package ca.watier.echechess.controllers;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.responses.BooleanResponse;
import ca.watier.echechess.common.responses.StringResponse;
import ca.watier.echechess.engine.exceptions.FenParserException;
import ca.watier.echechess.models.PawnPromotionPiecesModel;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.services.GameService;
import ca.watier.echechess.services.UserService;
import ca.watier.echechess.utils.AuthenticationUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("api/v1/game")
@PreAuthorize("#oauth2.hasScope('read')")
public class GameController {
    public static final String UI_UUID_PLAYER = "The UI-UUID of the player";
    private static final String UUID_GAME = "The UUID of the game";
    private static final String SIDE_PLAYER = "The side of the player";
    private static final String UPGRADED_PIECE = "The upgraded piece";
    private static final String TO_POSITION = "The to position";
    private static final String FROM_POSITION = "The from position";
    private static final String PLAY_AGAINST_THE_AI = "Create a new game to play against the AI";
    private static final String WITH_OR_WITHOUT_OBSERVERS = "Create a new game with or without observers";
    private static final String PATTERN_CUSTOM_GAME = "Pattern used to create a custom game";
    private static final ResponseEntity<Object> NO_CONTENT_RESPONSE_ENTITY = ResponseEntity.noContent().build();
    private static final ResponseEntity<Object> BAD_REQUEST_RESPONSE_ENTITY = ResponseEntity.badRequest().build();

    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }


    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "There's an issue with the game creation"),
            @ApiResponse(code = 200, message = "The game is created")
    })
    @ApiOperation("Create a new game for the current player")
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewGame(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                        @ApiParam(value = PLAY_AGAINST_THE_AI, required = true) boolean againstComputer,
                                        @ApiParam(value = WITH_OR_WITHOUT_OBSERVERS, required = true) boolean observers,
                                        @ApiParam(value = PATTERN_CUSTOM_GAME) String specialGamePieces) {

        try {
            UUID newGameUuid = gameService.createNewGame(specialGamePieces, side, againstComputer, observers, AuthenticationUtils.getUserDetail());

            addGameToPlayerSession(newGameUuid);
            return ResponseEntity.ok(new StringResponse(newGameUuid.toString()));
        } catch (FenParserException ignored) {
            return BAD_REQUEST_RESPONSE_ENTITY;
        }
    }

    @ApiOperation("Move the selected piece")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity movePieceOfPlayer(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                            @ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                            @ApiParam(value = UUID_GAME, required = true) String uuid) {

        gameService.movePiece(from, to, uuid, AuthenticationUtils.getUserDetail());
        return NO_CONTENT_RESPONSE_ENTITY;
    }

    @ApiOperation("Get a list of position that the piece can moves")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @GetMapping(path = "/moves", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMovesOfAPiece(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                           @ApiParam(value = UUID_GAME, required = true) String uuid) {

        gameService.getAllAvailableMoves(from, uuid, AuthenticationUtils.getUserDetail());
        return NO_CONTENT_RESPONSE_ENTITY;
    }

    @ApiOperation("Used when there's a pawn promotion")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/piece/pawn/promotion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity pawnPromotion(@ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                        @ApiParam(value = UUID_GAME, required = true) String uuid,
                                        @ApiParam(value = UPGRADED_PIECE, required = true) PawnPromotionPiecesModel piece) {
        return ResponseEntity.ok(gameService.upgradePiece(to, uuid, piece, AuthenticationUtils.getUserDetail()));
    }

    @ApiOperation("Gets the pieces location")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @GetMapping(path = "/pieces", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPieceLocations(@ApiParam(value = UUID_GAME, required = true) String uuid) {
        return ResponseEntity.ok(gameService.getPieceLocations(uuid, AuthenticationUtils.getUserDetail()));
    }

    @ApiOperation("Join a game for the current player")
    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity joinGame(@ApiParam(value = UUID_GAME, required = true) String uuid,
                                   @ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                   @ApiParam(value = UI_UUID_PLAYER, required = true) String uiUuid) {


        BooleanResponse response = gameService.joinGame(uuid, side, uiUuid, AuthenticationUtils.getUserDetail());

        if (response.isResponse()) {
            addGameToPlayerSession(UUID.fromString(uuid));
        }

        return ResponseEntity.ok(response);
    }

    @ApiOperation("Change the side of the current player")
    @PreAuthorize("isPlayerInGame(#uuid)")
    @PostMapping(path = "/side")
    public ResponseEntity setSideOfPlayer(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                          @ApiParam(value = UUID_GAME, required = true) String uuid) {
        return ResponseEntity.ok(gameService.setSideOfPlayer(side, uuid, AuthenticationUtils.getUserDetail()));
    }

    private void addGameToPlayerSession(UUID newGameUuid) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.addCreatedGame(newGameUuid);

        userService.addGameToUser(principal.getUsername(), newGameUuid);
    }
}
