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

package ca.watier.enums;


import ca.watier.echechess.common.enums.KingStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by yannick on 6/20/2017.
 */
public class KingStatusTest {

    @Test
    public void isCheckOrCheckMate() {
        assertTrue(KingStatus.isCheckOrCheckMate(KingStatus.CHECKMATE));
        assertTrue(KingStatus.isCheckOrCheckMate(KingStatus.CHECK));
        assertFalse(KingStatus.isCheckOrCheckMate(KingStatus.OK));
    }
}