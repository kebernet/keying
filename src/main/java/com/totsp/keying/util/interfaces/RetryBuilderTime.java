/**
 *    Copyright 2013 Robert Cooper
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
package com.totsp.keying.util.interfaces;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Masking interface for specifying the time component of the builder.
 */
public interface RetryBuilderTime {

    /**
     * Specifies the base time to back off.
     * @param time long unit of time.
     * @param units The TimeUnit the long value represents.
     * @return  interface for specifying the the backoff strategy.
     */
    public RetryBuilderStrategy every(long time, @Nonnull TimeUnit units);
}
