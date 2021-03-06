/*
 * Copyright 2015 Hannes Bibel, Valentin Sawadski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abominableshrine.taptounlock.mocks;

import android.os.IBinder;

/**
* Created by vsaw on 18/01/15.
*/
public class CountingDeathRecipient implements IBinder.DeathRecipient {

    /**
     * The count how often binderDied was called
     */
    public int count = 0;

    @Override
    public void binderDied() {
        count++;
    }
}
