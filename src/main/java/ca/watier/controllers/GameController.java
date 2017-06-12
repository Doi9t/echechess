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

package ca.watier.controllers;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Side;
import ca.watier.game.GenericGameHandler;
import ca.watier.responses.BooleanResponse;
import ca.watier.responses.DualValueResponse;
import ca.watier.services.GameService;
import ca.watier.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by yannick on 4/22/2017.
 */

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Create a new game
     *
     * @param side
     * @param againstComputer
     * @param observers
     * @param session
     * @return
     */
    @RequestMapping(path = "/create/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericGameHandler createNewGame(Side side, boolean againstComputer, boolean observers, String specialGamePieces, HttpSession session) {
        return gameService.createNewGame(SessionUtils.getPlayer(session), specialGamePieces, side, againstComputer, observers);
    }


    /**
     * Checks if the move is valid
     *
     * @param from
     * @param to
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/move/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse movePieceOfPlayer(CasePosition from, CasePosition to, String uuid, HttpSession session) {
        return gameService.movePiece(from, to, uuid, SessionUtils.getPlayer(session));
    }

    /**
     * Return a list of position that the piece can moves
     *
     * @param from
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/moves/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMovesOfAPiece(CasePosition from, String uuid, HttpSession session) {
        return gameService.getAllAvailableMoves(from, uuid, SessionUtils.getPlayer(session));
    }

    /**
     * Gets the pieces location
     *
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/pieces/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DualValueResponse> getPieceLocations(String uuid, HttpSession session) {
        return gameService.getPieceLocations(uuid, SessionUtils.getPlayer(session));
    }

    /**
     * Join a game
     *
     * @param uuid
     * @param side
     * @param session
     * @return
     */
    @RequestMapping(path = "/join/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse joinGame(String uuid, Side side, HttpSession session) {
        return gameService.joinGame(uuid, side, SessionUtils.getPlayer(session));
    }

    /**
     * Change side of a player
     *
     * @param side
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/side/1", method = RequestMethod.POST)
    public BooleanResponse setSideOfPlayer(Side side, String uuid, HttpSession session) {
        return gameService.setSideOfPlayer(SessionUtils.getPlayer(session), side, uuid);
    }
}
