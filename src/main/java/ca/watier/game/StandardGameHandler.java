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

package ca.watier.game;

import ca.watier.enums.CasePosition;
import ca.watier.enums.Direction;
import ca.watier.enums.Pieces;
import ca.watier.enums.Side;
import ca.watier.exceptions.GameException;
import ca.watier.services.ConstraintService;
import ca.watier.utils.Assert;
import ca.watier.utils.MathUtils;
import ca.watier.utils.MultiArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick on 4/17/2017.
 */
public class StandardGameHandler extends GenericGameHandler {

    public StandardGameHandler(ConstraintService constraintService) {
        super(constraintService);
    }

    @Override
    public boolean movePiece(CasePosition from, CasePosition to, Side playerSide) throws GameException {
        Assert.assertNotNull(from, to);

        if (!isPieceMovableTo(from, to, playerSide)) {
            return false;
        }

        super.movePiece(from, to, playerSide);

        Pieces piecesFrom = CURRENT_PIECES_LOCATION.get(from);
        boolean isMoved = false;

        if (piecesFrom != null) {
            Side sideFrom = piecesFrom.getSide();

            if (isPlayerTurn(sideFrom) && sideFrom.equals(playerSide)) {
                CURRENT_PIECES_LOCATION.remove(from);
                CURRENT_PIECES_LOCATION.put(to, piecesFrom);
                changeAllowedMoveSide();

                isMoved = true;
            }
        }

        return isMoved;
    }

    @Override
    protected boolean isKingCheckMate(CasePosition kingPosition, Side playerSide) {
        Assert.assertNotNull(kingPosition, playerSide);
        return isCheckMate(kingPosition, playerSide, 1);
    }

    @Override
    protected boolean isKingCheck(CasePosition kingPosition, Side playerSide) {
        Assert.assertNotNull(kingPosition, playerSide);
        Side otherPlayerSide = Side.BLACK.equals(playerSide) ? Side.WHITE : Side.BLACK;
        Map<CasePosition, Pieces> otherPlayerPiecesLocation = getPiecesLocation(otherPlayerSide);
        boolean isCheck = false;

        //Check if the king is "check" in this position
        for (CasePosition otherPieceLocation : otherPlayerPiecesLocation.keySet()) {
            if (CONSTRAINT_SERVICE.isPieceMovableTo(otherPieceLocation, kingPosition, otherPlayerSide, CURRENT_PIECES_LOCATION)) {
                isCheck = true;
                break;
            }
        }

        return isCheck;
    }

    private boolean isCheckMate(CasePosition kingPosition, Side playerSide, int maxRecursiveDepth) {
        Side otherPlayerSide = Side.BLACK.equals(playerSide) ? Side.WHITE : Side.BLACK;
        boolean isCheckmate = isKingCheck(kingPosition, playerSide);

        if (isCheckmate) { //The current position is compromised, check the others
            List<CasePosition> caseAround = new ArrayList<>();

            //Fetch all the position around the king
            for (Direction direction : Direction.values()) {
                CasePosition nearestPositionFromDirection = MathUtils.getNearestPositionFromDirection(kingPosition, direction);

                if (nearestPositionFromDirection != null) {
                    caseAround.add(nearestPositionFromDirection);
                }
            }

            //Fetch the enemy pieces that can hit around the king
            MultiArrayMap<CasePosition, Pieces> piecesThatCanHitPosition = getPiecesThatCanHitPosition(otherPlayerSide, caseAround.toArray(new CasePosition[caseAround.size()]));

            if (caseAround.size() == piecesThatCanHitPosition.size()) { //There nowhere to move, all the move locations are targeted
                //TODO: Try to block the way

                //Check if the king can kill something to save himself
                for (CasePosition position : caseAround) {
                    Pieces pieces = CURRENT_PIECES_LOCATION.get(position);

                    if (pieces != null && !pieces.getSide().equals(playerSide) && maxRecursiveDepth > 0) {
                        boolean checkMate = isCheckMate(position, playerSide, maxRecursiveDepth--); //FIXME
                        //TODO
                    }
                }

            } else { //There's more place to move
                isCheckmate = false;
            }
        }

        return isCheckmate;
    }
}
