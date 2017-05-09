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

package ca.watier.pieces;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.game.StandardGameHandler;
import ca.watier.services.ConstraintService;
import ca.watier.testUtils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ca.watier.enums.CasePosition.*;
import static ca.watier.enums.Pieces.*;
import static ca.watier.enums.SpecialGameRules.CAN_SET_PIECES;
import static ca.watier.enums.SpecialGameRules.NO_PLAYER_TURN;
import static junit.framework.TestCase.fail;

/**
 * Created by yannick on 5/8/2017.
 */
public class PawnMovesTest {

    private static final Side WHITE = Side.WHITE;
    private static final Side BLACK = Side.BLACK;
    private static final ConstraintService constraintService = new ConstraintService();

    @Test
    public void moveTest() {
        Map<CasePosition, Pieces> pieces = new HashMap<>();
        pieces.put(E1, W_KING);
        pieces.put(E8, B_KING);

        //Cannot move (front)
        pieces.put(H2, W_PAWN);
        pieces.put(H3, W_ROOK); //Is blocking the H2 pawn
        pieces.put(H7, B_PAWN);
        pieces.put(H6, B_ROOK); //Is blocking the H7 pawn

        //can move
        pieces.put(A2, W_PAWN);
        pieces.put(B2, W_PAWN);
        pieces.put(F2, W_PAWN);
        pieces.put(A7, B_PAWN);
        pieces.put(B7, B_PAWN);
        pieces.put(F7, B_PAWN);

        StandardGameHandler gameHandler = new StandardGameHandler(constraintService);
        gameHandler.addSpecialRule(CAN_SET_PIECES, NO_PLAYER_TURN);
        gameHandler.setPieceLocation(pieces);

        try {
            Utils.addBothPlayerToGameAndSetUUID(gameHandler);

            //Cannot move (blocked in front)
            Assert.assertFalse(gameHandler.movePiece(H2, H4, WHITE)); // 2 cases
            Assert.assertFalse(gameHandler.movePiece(H2, H3, WHITE));
            Assert.assertFalse(gameHandler.movePiece(H7, H5, BLACK)); // 2 cases
            Assert.assertFalse(gameHandler.movePiece(H7, H6, BLACK));

            //Can move
            Assert.assertTrue(gameHandler.movePiece(A2, A4, WHITE)); // 2 cases
            Assert.assertTrue(gameHandler.movePiece(B2, B3, WHITE));
            Assert.assertTrue(gameHandler.movePiece(A7, A5, BLACK)); // 2 cases
            Assert.assertTrue(gameHandler.movePiece(B7, B6, BLACK));

            //Cannot move by 2 position (not on the starting position)
            Assert.assertFalse(gameHandler.movePiece(B3, B5, WHITE));
            Assert.assertFalse(gameHandler.movePiece(B6, B4, WHITE));

            //Can move by one position
            Assert.assertTrue(gameHandler.movePiece(B3, B4, WHITE));
            Assert.assertTrue(gameHandler.movePiece(B6, B5, BLACK));

            //cannot move diagonally (without attack)
            Assert.assertFalse(gameHandler.movePiece(F2, E3, WHITE));
            Assert.assertFalse(gameHandler.movePiece(F2, G3, WHITE));
            Assert.assertFalse(gameHandler.movePiece(F2, D4, WHITE)); // 2 cases
            Assert.assertFalse(gameHandler.movePiece(F2, H4, WHITE)); // 2 cases
            Assert.assertFalse(gameHandler.movePiece(F7, E6, BLACK));
            Assert.assertFalse(gameHandler.movePiece(F7, G6, BLACK));
            Assert.assertFalse(gameHandler.movePiece(F7, D5, WHITE)); // 2 cases
            Assert.assertFalse(gameHandler.movePiece(F7, H5, WHITE)); // 2 cases

            //Kill in all direction
            pieces.clear();
            pieces.put(E1, W_KING);
            pieces.put(E8, B_KING);

            pieces.put(D5, W_PAWN);
            pieces.put(D3, B_PAWN);
            pieces.put(F5, W_PAWN);
            pieces.put(F3, B_PAWN);

            pieces.put(C6, B_PAWN);
            pieces.put(G6, B_PAWN);
            pieces.put(C2, W_PAWN);
            pieces.put(G2, W_PAWN);

            Assert.assertTrue(gameHandler.movePiece(D5, C6, WHITE));
            Assert.assertTrue(gameHandler.movePiece(D3, C2, BLACK));
            Assert.assertTrue(gameHandler.movePiece(F5, G6, WHITE));
            Assert.assertTrue(gameHandler.movePiece(F3, G2, BLACK));

        } catch (GameException e) {
            e.printStackTrace();
            fail();
        }
    }
}
