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

package ca.watier.responses;

import ca.watier.utils.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yannick on 6/20/2017.
 */
public class DualValueResponseTest {

    public static final String FIRST = "First";
    public static final String SECOND = "Second";
    public static final String OLD_MESSAGE = "Old Message";
    private DualValueResponse dualValueResponse;

    @Before
    public void setUp() throws Exception {
        dualValueResponse = new DualValueResponse(FIRST, SECOND, OLD_MESSAGE);
    }

    @Test
    public void getValue1() throws Exception {
        Assert.assertEquals(FIRST, (String) dualValueResponse.getValue1());
    }

    @Test
    public void getValue2() throws Exception {
        Assert.assertEquals(SECOND, (String) dualValueResponse.getValue2());
    }

    @Test
    public void getMessage() throws Exception {
        String message = "A new Message";
        dualValueResponse.setMessage(message);
        Assert.assertEquals(message, dualValueResponse.getMessage());
    }

}