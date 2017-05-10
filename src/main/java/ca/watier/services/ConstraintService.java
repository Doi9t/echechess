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

package ca.watier.services;

import ca.watier.constraints.*;
import ca.watier.enums.CasePosition;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.utils.Assert;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick on 4/26/2017.
 */

@Service
public class ConstraintService {

    private static final Map<Pieces, MoveConstraint> MOVE_CONSTRAINT_MAP = new HashMap<>();

    static {
        for (Pieces piece : Pieces.values()) {

            Class<? extends MoveConstraint> pieceMoveConstraintClass = getPieceMoveConstraintClass(piece);

            try {
                MOVE_CONSTRAINT_MAP.put(piece, pieceMoveConstraintClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Class<? extends MoveConstraint> getPieceMoveConstraintClass(Pieces pieces) {
        Assert.assertNotNull(pieces);

        Class<? extends MoveConstraint> moveConstraint = null;

        switch (pieces) {
            case W_KING:
            case B_KING:
                moveConstraint = KingMoveConstraint.class;
                break;
            case W_QUEEN:
            case B_QUEEN:
                moveConstraint = QueenMoveConstraint.class;
                break;
            case W_ROOK:
            case B_ROOK:
                moveConstraint = RookMoveConstraint.class;
                break;
            case W_BISHOP:
            case B_BISHOP:
                moveConstraint = BishopMoveConstraint.class;
                break;
            case W_KNIGHT:
            case B_KNIGHT:
                moveConstraint = KnightMoveConstraint.class;
                break;
            case W_PAWN:
            case B_PAWN:
                moveConstraint = PawnMoveConstraint.class;
                break;
        }

        return moveConstraint;
    }

    /**
     * Checks if the piece is movable to the specified location
     *
     * @param from
     * @param to
     * @param playerSide
     * @param piecesLocation
     * @return
     */
    public boolean isPieceMovableTo(CasePosition from, CasePosition to, Side playerSide, Map<CasePosition, Pieces> piecesLocation) {
        Assert.assertNotNull(from, to, playerSide, piecesLocation);

        if (Side.OBSERVER.equals(playerSide)) {
            return false;
        }

        Pieces fromPiece = piecesLocation.get(from);
        MoveConstraint moveConstraint = MOVE_CONSTRAINT_MAP.get(fromPiece);

        Assert.assertNotNull(moveConstraint);

        return moveConstraint.isMoveValid(from, to, playerSide, piecesLocation);
    }

    /**
     * Checks if the piece can attack to the specified location
     *
     * @param from
     * @param to
     * @param playerSide
     * @param piecesLocation
     * @return
     */
    public boolean isPieceCanAttackTo(CasePosition from, CasePosition to, Side playerSide, Map<CasePosition, Pieces> piecesLocation) {
        Assert.assertNotNull(from, to, playerSide, piecesLocation);

        if (Side.OBSERVER.equals(playerSide)) {
            return false;
        }

        Pieces fromPiece = piecesLocation.get(from);
        MoveConstraint moveConstraint = MOVE_CONSTRAINT_MAP.get(fromPiece);

        Assert.assertNotNull(moveConstraint);

        return moveConstraint.canAttackTo(from, to, playerSide, piecesLocation);
    }
}
