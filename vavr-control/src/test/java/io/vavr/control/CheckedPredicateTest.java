/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2018 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckedPredicateTest {

    // -- static .not(CheckedPredicate)

    @Test
    void shouldApplyStaticNotToGivenPredicate() throws Exception {
        final CheckedPredicate<Object> p1 = o -> false;
        final CheckedPredicate<String> p2 = CheckedPredicate.not(p1);
        assertTrue(p2.test(null));
    }

    // -- .test(Object)

    @Test
    void shouldBeAbleToThrowCheckedException() {
        final CheckedConsumer<?> f = ignored -> { throw new Exception(); };
        assertThrows(Exception.class, () -> f.accept(null));
    }

    // -- .and(CheckedPredicate)

    // TODO

    // -- .negate()

    // TODO

    // -- .or(CheckedPredicate)

    // TODO
    
}
