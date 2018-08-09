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

import ca.watier.echechess.api.model.GenericPiecesModel;
import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.common.enums.Side;
import ca.watier.echechess.common.utils.SessionUtils;
import ca.watier.echechess.services.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("api/v1/game")
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

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }


    @ApiOperation("Create a new game for the current player")
    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewGame(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                        @ApiParam(value = PLAY_AGAINST_THE_AI, required = true) boolean againstComputer,
                                        @ApiParam(value = WITH_OR_WITHOUT_OBSERVERS, required = true) boolean observers,
                                        @ApiParam(value = PATTERN_CUSTOM_GAME) String specialGamePieces,
                                        HttpSession session) {
        UUID newGameUuid = gameService.createNewGame(SessionUtils.getPlayer(session), specialGamePieces, side, againstComputer, observers);
        return ResponseEntity.ok(newGameUuid.toString());
    }

    @ApiOperation("Move the selected piece")
    @RequestMapping(path = "/move", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity movePieceOfPlayer(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                            @ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                            @ApiParam(value = UUID_GAME, required = true) String uuid,
                                            HttpSession session) {

        gameService.movePiece(from, to, uuid, SessionUtils.getPlayer(session));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("Get a list of position that the piece can moves")
    @RequestMapping(path = "/moves", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMovesOfAPiece(@ApiParam(value = FROM_POSITION, required = true) CasePosition from,
                                           @ApiParam(value = UUID_GAME, required = true) String uuid,
                                           HttpSession session) {
        return ResponseEntity.ok(gameService.getAllAvailableMoves(from, uuid, SessionUtils.getPlayer(session)));
    }

    @ApiOperation("Used when there's a pawn promotion")
    @RequestMapping(path = "/piece/pawn/promotion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity pawnPromotion(@ApiParam(value = TO_POSITION, required = true) CasePosition to,
                                        @ApiParam(value = UUID_GAME, required = true) String uuid,
                                        @ApiParam(value = UPGRADED_PIECE, required = true) GenericPiecesModel piece,
                                        HttpSession session) {
        return ResponseEntity.ok(gameService.upgradePiece(to, uuid, piece, SessionUtils.getPlayer(session)));
    }

    @ApiOperation("Gets the pieces location")
    @RequestMapping(path = "/pieces", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPieceLocations(@ApiParam(value = UUID_GAME, required = true) String uuid,
                                            HttpSession session) {
        return ResponseEntity.ok(gameService.getPieceLocations(uuid, SessionUtils.getPlayer(session)));
    }

    @ApiOperation("Join a game for the current player")
    @RequestMapping(path = "/join", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity joinGame(@ApiParam(value = UUID_GAME, required = true) String uuid,
                                   @ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                   @ApiParam(value = UI_UUID_PLAYER, required = true) String uiUuid,
                                   HttpSession session) {
        return ResponseEntity.ok(gameService.joinGame(uuid, side, uiUuid, SessionUtils.getPlayer(session)));
    }

    @ApiOperation("Change the side of the current player")
    @RequestMapping(path = "/side", method = RequestMethod.POST)
    public ResponseEntity setSideOfPlayer(@ApiParam(value = SIDE_PLAYER, required = true) Side side,
                                          @ApiParam(value = UUID_GAME, required = true) String uuid,
                                          HttpSession session) {
        return ResponseEntity.ok(gameService.setSideOfPlayer(SessionUtils.getPlayer(session), side, uuid));
    }
}
