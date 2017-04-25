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
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by yannick on 4/24/2017.
 */
public class DirectionTest {
    private static final Direction NORTH = Direction.NORTH;
    private static final Direction NORTH_WEST = Direction.NORTH_WEST;
    private static final Direction WEST = Direction.WEST;
    private static final Direction SOUTH_WEST = Direction.SOUTH_WEST;
    private static final Direction SOUTH = Direction.SOUTH;
    private static final Direction SOUTH_EAST = Direction.SOUTH_EAST;
    private static final Direction EAST = Direction.EAST;
    private static final Direction NORTH_EAST = Direction.NORTH_EAST;

    private static final CasePosition D_5 = CasePosition.D5;


    @Test
    public void getDirectionFromPosition() throws Exception {

        Assert.assertEquals(Direction.NONE, Direction.getDirectionFromPosition(D_5, D_5));

        Assert.assertEquals(NORTH, Direction.getDirectionFromPosition(D_5, CasePosition.D6));
        Assert.assertEquals(NORTH, Direction.getDirectionFromPosition(D_5, CasePosition.D8));

        Assert.assertEquals(NORTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.C8));
        Assert.assertEquals(NORTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.C6));
        Assert.assertEquals(NORTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.A8));

        Assert.assertEquals(WEST, Direction.getDirectionFromPosition(D_5, CasePosition.A5));
        Assert.assertEquals(WEST, Direction.getDirectionFromPosition(D_5, CasePosition.C5));

        Assert.assertEquals(SOUTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.B4));
        Assert.assertEquals(SOUTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.C4));
        Assert.assertEquals(SOUTH_WEST, Direction.getDirectionFromPosition(D_5, CasePosition.A1));

        Assert.assertEquals(SOUTH, Direction.getDirectionFromPosition(D_5, CasePosition.D4));
        Assert.assertEquals(SOUTH, Direction.getDirectionFromPosition(D_5, CasePosition.D1));

        Assert.assertEquals(SOUTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.F1));
        Assert.assertEquals(SOUTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.E4));
        Assert.assertEquals(SOUTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.H1));

        Assert.assertEquals(EAST, Direction.getDirectionFromPosition(D_5, CasePosition.E5));
        Assert.assertEquals(EAST, Direction.getDirectionFromPosition(D_5, CasePosition.H5));

        Assert.assertEquals(NORTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.G6));
        Assert.assertEquals(NORTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.E6));
        Assert.assertEquals(NORTH_EAST, Direction.getDirectionFromPosition(D_5, CasePosition.H8));
    }
}