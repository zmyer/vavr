/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2014-2018 Vavr, http://vavr.io
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

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    // -- Testees

    private static final String SUCCESS_VALUE = "success";
    private static final Try<String> SUCCESS = Try.success(SUCCESS_VALUE);

    private static final Exception FAILURE_CAUSE = new IllegalStateException("failure");
    private static final Try<String> FAILURE = Try.failure(FAILURE_CAUSE);

    // ---- alternate errors, e.g. when chaining a failure

    private static final Error ERROR = new Error();

    // ---- unexpected behavior like running unexpected code

    private static final AssertionError ASSERTION_ERROR = new AssertionError("unexpected");

    // ---- rethrown fatal errors

    private static final LinkageError LINKAGE_ERROR = new LinkageError();

    private static final ThreadDeath THREAD_DEATH = new ThreadDeath();

    private static final VirtualMachineError VM_ERROR = new VirtualMachineError() {
        private static final long serialVersionUID = 1L;
    };

    // -- static .of(Callable)

    @Test
    void shouldCreateSuccessWhenCallingTryOfWithNullValue() {
        assertNotNull(Try.of(() -> null));
    }

    @Test
    void shouldCreateSuccessWhenCallingTryOfCallable() {
        assertTrue(Try.of(() -> SUCCESS_VALUE).isSuccess());
    }

    @Test
    void shouldCreateFailureWhenCallingTryOfCallable() {
        assertTrue(Try.of(() -> { throw FAILURE_CAUSE; }).isFailure());
    }

    @Test
    void shouldThrowNPEWhenCallingTryOfCallable() {
        assertEquals(
                "callable is null",
                assertThrows(NullPointerException.class, () -> Try.of(null)).getMessage()
        );
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryOfCallable() {
        assertSame(
                LINKAGE_ERROR,
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.of(() -> { throw LINKAGE_ERROR; }))
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryOfCallable() {
        assertSame(
                THREAD_DEATH,
                assertThrows(THREAD_DEATH.getClass(), () -> Try.of(() -> { throw THREAD_DEATH; }))
        );
    }

    @Test
    void shouldRethrowVirtualMachoneErrorWhenCallingTryOfCallable() {
        assertSame(
                VM_ERROR,
                assertThrows(VM_ERROR.getClass(), () -> Try.of(() -> { throw VM_ERROR; }))
        );
    }

    @Test
    void shouldBeIndistinguishableWhenCreatingFailureWithOfFactoryOrWithFailureFactory() {
        final Try<?> failure1 = Try.of(() -> { throw FAILURE_CAUSE; });
        final Try<?> failure2 = Try.failure(FAILURE_CAUSE);
        assertSame(
                FAILURE_CAUSE,
                assertThrows(NonFatalException.class, failure1::get).getCause()
        );
        assertSame(
                FAILURE_CAUSE,
                assertThrows(NonFatalException.class, failure2::get).getCause()
        );
        assertSame(failure1.getCause(), failure2.getCause());
        assertEquals(failure1.isFailure(), failure2.isFailure());
        assertEquals(failure1.isSuccess(), failure2.isSuccess());
        assertEquals(failure1, failure2);
        assertEquals(failure1.hashCode(), failure2.hashCode());
        assertEquals(failure1.toString(), failure2.toString());
    }

    @Test
    void shouldBeIndistinguishableWhenCreatingSuccessWithOfFactoryOrWithSuccessFactory() {
        final Try<?> success1 = Try.of(() -> SUCCESS_VALUE);
        final Try<?> success2 = Try.success(SUCCESS_VALUE);
        assertSame(success1.get(), success2.get());
        assertThrows(UnsupportedOperationException.class, success1::getCause);
        assertThrows(UnsupportedOperationException.class, success2::getCause);
        assertEquals(success1.isFailure(), success2.isFailure());
        assertEquals(success1.isSuccess(), success2.isSuccess());
        assertEquals(success1, success2);
        assertEquals(success1.hashCode(), success2.hashCode());
        assertEquals(success1.toString(), success2.toString());
    }

    // -- static .run(CheckedRunnable)

    @Test
    void shouldCreateSuccessWhenCallingTryRunCheckedRunnable() {
        assertTrue(Try.run(() -> {}).isSuccess());
    }

    @Test
    void shouldCreateFailureWhenCallingTryRunCheckedRunnable() {
        assertTrue(Try.run(() -> { throw ERROR; }).isFailure());
    }

    @Test
    void shouldThrowNPEWhenCallingTryRunCheckedRunnable() {
        assertEquals(
                "runnable is null",
                assertThrows(NullPointerException.class, () -> Try.run(null)).getMessage()
        );
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryRunCheckedRunnable() {
        assertSame(
                LINKAGE_ERROR,
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.run(() -> { throw LINKAGE_ERROR; }))
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryRunCheckedRunnable() {
        assertSame(
                THREAD_DEATH,
                assertThrows(THREAD_DEATH.getClass(), () -> Try.run(() -> { throw THREAD_DEATH; }))
        );
    }

    @Test
    void shouldRethrowVirtualMachineErrorWhenCallingTryRunCheckedRunnable() {
        assertSame(
                VM_ERROR,
                assertThrows(VM_ERROR.getClass(), () -> Try.run(() -> { throw VM_ERROR; }))
        );
    }

    // -- static .success(Object)
    
    @Test
    void shouldCreateSuccessWithNullValue() {
        assertNotNull(Try.success(null));
    }

    @Test
    void shouldCreateSuccess() {
        assertNotNull(Try.success(SUCCESS_VALUE));
    }

    @Test
    void shouldVerifyBasicSuccessProperties() {
        final Try<?> success = Try.success(SUCCESS_VALUE);
        assertSame(SUCCESS_VALUE, success.get());
        assertSame(
                "Success.getCause()",
                assertThrows(UnsupportedOperationException.class, success::getCause).getMessage()
        );
        assertFalse(success.isFailure());
        assertTrue(success.isSuccess());
        assertEquals(SUCCESS, success);
        assertEquals(31 + Objects.hashCode(SUCCESS_VALUE), success.hashCode());
        assertEquals("Success(" + SUCCESS_VALUE + ")", success.toString());
    }

    // -- static .failure(Throwable)

    @Test
    void shouldCreateFailureWithNullValue() {
        assertNotNull(Try.failure(null));
    }

    @Test
    void shouldCreateFailure() {
        assertNotNull(Try.failure(FAILURE_CAUSE));
    }

    @Test
    void shouldVerifyBasicFailureProperties() {
        final Try<?> failure = Try.failure(FAILURE_CAUSE);
        assertSame(
                FAILURE_CAUSE,
                assertThrows(RuntimeException.class, failure::get).getCause()
        );
        assertSame(FAILURE_CAUSE, failure.getCause());
        assertFalse(failure.isSuccess());
        assertTrue(failure.isFailure());
        assertEquals(FAILURE, failure);
        assertEquals(Objects.hashCode(FAILURE_CAUSE), failure.hashCode());
        assertEquals("Failure(" + FAILURE_CAUSE + ")", failure.toString());
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryFailure() {
        assertSame(
                LINKAGE_ERROR,
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.failure(LINKAGE_ERROR))
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryFailure() {
        assertSame(
                THREAD_DEATH,
                assertThrows(THREAD_DEATH.getClass(), () -> Try.failure(THREAD_DEATH))
        );
    }

    @Test
    void shouldRethrowVirtualMachineErrorWhenCallingTryFailure() {
        assertSame(
                VM_ERROR,
                assertThrows(VM_ERROR.getClass(), () -> Try.failure(VM_ERROR))
        );
    }

    // -- .failed()

    @Test
    void shouldInvertSuccessByCallingFailed() {
        final Try<?> testee = SUCCESS.failed();
        assertTrue(testee.isFailure());
        assertEquals(UnsupportedOperationException.class, testee.getCause().getClass());
        assertEquals("Success.failed()", testee.getCause().getMessage());
    }

    @Test
    void shouldInvertSuccessWithNullValueByCallingFailed() {
        assertNotNull(Try.success(null).failed());
    }

    @Test
    void shouldInvertFailureByCallingFailed() {
        assertEquals(Try.success(FAILURE_CAUSE), FAILURE.failed());
    }

    @Test
    void shouldInvertFailureWithNullCauseByCallingFailed() {
        assertNotNull(Try.failure(null).failed());
    }

    // -- .filter(CheckedPredicate)

    @Test
    void shouldFilterMatchingPredicateOnFailure() {
        assertSame(FAILURE, FAILURE.filter(s -> true));
    }

    @Test
    void shouldFilterNonMatchingPredicateOnFailure() {
        assertSame(FAILURE, FAILURE.filter(s -> false));
    }

    @Test
    void shouldFilterWithExceptionOnFailure() {
        assertSame(FAILURE, FAILURE.filter(t -> { throw ERROR; }));
    }

    @Test
    void shouldFilterMatchingPredicateOnSuccess() {
        assertSame(SUCCESS, SUCCESS.filter(s -> true));
    }

    @Test
    void shouldFilterNonMatchingPredicateOnSuccess() {
        final Try<String> testee = SUCCESS.filter(s -> false);
        assertTrue(testee.isFailure());
        assertEquals(NoSuchElementException.class, testee.getCause().getClass());
        assertEquals("Predicate does not hold for " + SUCCESS_VALUE, testee.getCause().getMessage());
    }

    @Test
    void shouldFilterWithExceptionOnSuccess() {
        final Try<String> testee = SUCCESS.filter(t -> { throw ERROR; });
        assertTrue(testee.isFailure());
        assertSame(ERROR, testee.getCause());
    }

    @Test
    void shouldThrowNPEWhenFilteringFailureWithNullPredicate() {
        assertEquals(
                "predicate is null",
                assertThrows(NullPointerException.class, () -> FAILURE.filter(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenFilteringSuccessWithNullPredicate() {
        assertEquals(
                "predicate is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.filter(null)).getMessage()
        );
    }

    @Test
    void shouldFilterFailureWithNullCause() {
        assertNotNull(Try.failure(null).filter(x -> true));
    }

    @Test
    void shouldFilterSuccessWithNullValue() {
        assertNotNull(Try.success(null).filter(x -> true));
    }

    // -- .flatMap(CheckedFunction)

    @Test
    void shouldFlatMapSuccessToNull() {
        assertNull(SUCCESS.flatMap(ignored -> null));
    }

    @Test
    void shouldFlatMapToSuccessOnSuccess() {
        assertSame(SUCCESS, SUCCESS.flatMap(ignored -> SUCCESS));
    }

    @Test
    void shouldFlatMapToFailureOnSuccess() {
        assertSame(FAILURE, SUCCESS.flatMap(ignored -> FAILURE));
    }

    @Test
    void shouldFlatMapOnFailure() {
        assertSame(FAILURE, FAILURE.flatMap(ignored -> SUCCESS));
    }

    @Test
    void shouldCaptureExceptionWhenFlatMappingSuccess() {
        assertEquals(Try.failure(ERROR), SUCCESS.flatMap(ignored -> { throw ERROR; }));
    }

    @Test
    void shouldIgnoreExceptionWhenFlatMappingFailure() {
        assertSame(FAILURE, FAILURE.flatMap(ignored -> { throw ERROR; }));
    }

    @Test
    void shouldThrowNPEWhenFlatMappingFailureWithNullParam() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> FAILURE.flatMap(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenFlatMappingSuccessWithNullParam() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.flatMap(null)).getMessage()
        );
    }

    @Test
    void shouldFlatMapFailureWithNullCause() {
        assertNotNull(Try.failure(null).flatMap(x -> null));
    }

    @Test
    void shouldFlatMapSuccessWithNullValue() {
        assertSame(SUCCESS, Try.success(null).flatMap(s -> SUCCESS));
    }

    // -- .fold(Function, Function)

    @Test
    void shouldFoldFailureWhenCauseIsNull() {
        assertEquals(0, Try.failure(null).<Integer> fold(x -> 0, s -> 1).intValue());
    }

    @Test
    void shouldFoldSuccessWhenValueIsNull() {
        assertEquals(1, Try.success(null).<Integer> fold(x -> 0, s -> 1).intValue());
    }

    @Test
    void shouldFoldFailureToNull() {
        assertNull(FAILURE.<Object> fold(x -> null, s -> ""));
    }

    @Test
    void shouldFoldSuccessToNull() {
        assertNull(SUCCESS.<Object> fold(x -> "", s -> null));
    }

    @Test
    void shouldFoldAndReturnValueIfSuccess() {
        final int folded = SUCCESS.fold(x -> { throw ASSERTION_ERROR; }, String::length);
        assertEquals(SUCCESS_VALUE.length(), folded);
    }

    @Test
    void shouldFoldAndReturnAlternateValueIfFailure() {
        final String folded = FAILURE.fold(x -> SUCCESS_VALUE, a -> { throw ASSERTION_ERROR; });
        assertEquals(SUCCESS_VALUE, folded);
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnFailureFunctionIsNullIfSuccess() {
        assertEquals(
                "onFailure is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.fold(null, Function.identity())).getMessage()
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnFailureFunctionIsNullIfFailure() {
        assertEquals(
                "onFailure is null",
                assertThrows(NullPointerException.class, () -> FAILURE.fold(null, Function.identity())).getMessage()
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnSuccessFunctionIsNullIfSuccess() {
        assertEquals(
                "onSuccess is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.fold(Function.identity(), null)).getMessage()
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnSuccessFunctionIsNullIfFailure() {
        assertEquals(
                "onSuccess is null",
                assertThrows(NullPointerException.class, () -> FAILURE.fold(Function.identity(), null)).getMessage()
        );
    }

    // -- .forEach(Consumer)

    @Test
    void shouldConsumeFailureWithForEach() {
        final var list = new ArrayList<>();
        FAILURE.forEach(list::add);
        assertEquals(List.of(), list);
    }

    @Test
    void shouldConsumeSuccessWithForEach() {
        final var list = new ArrayList<>();
        SUCCESS.forEach(list::add);
        assertEquals(List.of(SUCCESS_VALUE), list);
    }

    @Test
    void shouldThrowNPEWhenConsumingFailureWithForEachAndActionIsNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> FAILURE.forEach(null));
    }

    @Test
    void shouldThrowNPEWhenConsumingSuccessWithForEachAndActionIsNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> SUCCESS.forEach(null));
    }

    // -- .get()

    @Test
    void shouldGetOnSuccessWhenValueIsNull() {
        assertNull(Try.success(null).get());
    }
    
    @Test
    void shouldThrowCauseWrappedInRuntimeExceptionWhenGetOnFailure() {
        assertSame(
                FAILURE_CAUSE,
                assertThrows(NonFatalException.class, FAILURE::get).getCause()
        );
    }

    @Test
    void shouldThrowNullCauseWrappedInRuntimeExceptionWhenGetOnFailure() {
        assertNull(assertThrows(NonFatalException.class, () -> Try.failure(null).get()).getCause());
    }

    @Test
    void shouldGetOnSuccess() {
        assertEquals(SUCCESS_VALUE, SUCCESS.get());
    }

    // -- .getCause()

    @Test
    void shouldGetCauseOnFailureWhenCauseIsNull() {
        assertNull(Try.failure(null).getCause());
    }
    
    @Test
    void shouldGetCauseOnFailure() {
        assertSame(FAILURE_CAUSE, FAILURE.getCause());
    }

    @Test
    void shouldThrowWhenCallingGetCauseOnSuccess() {
        assertEquals(
                "Success.getCause()",
                assertThrows(UnsupportedOperationException.class, SUCCESS::getCause).getMessage()
        );
    }

    // -- .getOrElse(Object)

    @Test
    void shouldReturnElseWhenOrElseOnFailure() {
        assertSame(SUCCESS_VALUE, FAILURE.getOrElse(SUCCESS_VALUE));
    }

    @Test
    void shouldGetOrElseOnSuccess() {
        assertSame(SUCCESS_VALUE, SUCCESS.getOrElse(null));
    }

    // -- .getOrElseGet(Supplier)

    @Test
    void shouldReturnElseWhenOrElseGetOnFailure() {
        assertSame(SUCCESS_VALUE, FAILURE.getOrElseGet(() -> SUCCESS_VALUE));
    }

    @Test
    void shouldOrElseGetOnSuccess() {
        assertSame(SUCCESS_VALUE, SUCCESS.getOrElseGet(() -> null));
    }

    // -- .getOrElseThrow(Function)

    @Test
    void shouldThrowOtherWhenGetOrElseThrowOnFailure() {
        assertSame(
                ERROR,
                assertThrows(ERROR.getClass(), () -> FAILURE.getOrElseThrow(x -> ERROR))
        );
    }

    @Test
    void shouldOrElseThrowOnSuccess() {
        assertSame(SUCCESS_VALUE, SUCCESS.getOrElseThrow(x -> null));
    }

    // -- .isFailure()

    @Test
    void shouldDetectFailureIfFailure() {
        assertTrue(FAILURE.isFailure());
    }

    @Test
    void shouldDetectNonFailureIfSuccess() {
        assertFalse(SUCCESS.isFailure());
    }

    // -- .isSuccess()

    @Test
    void shouldDetectSuccessIfSuccess() {
        assertTrue(SUCCESS.isSuccess());
    }

    @Test
    void shouldDetectNonSuccessIfSuccess() {
        assertFalse(FAILURE.isSuccess());
    }

    // -- .iterator()

    @Test
    void shouldReturnIteratorOfSuccess() {
        assertNotNull(SUCCESS.iterator());
    }

    @Test
    void shouldReturnIteratorOfFailure() {
        assertNotNull(FAILURE.iterator());
    }

    // -- .map(CheckedFunction)

    @Test
    void shouldMapOnFailure() {
        assertSame(FAILURE, FAILURE.map(s -> s + "!"));
    }

    @Test
    void shouldMapWithExceptionOnFailure() {
        assertSame(FAILURE, FAILURE.map(ignored -> { throw ERROR; }));
    }

    @Test
    void shouldMapOnSuccess() {
        assertEquals(Try.success(SUCCESS_VALUE + "!"), SUCCESS.map(s -> s + "!"));
    }

    @Test
    void shouldMapOnSuccessWhenValueIsNull() {
        assertEquals(Try.success("null!"), Try.success(null).map(s -> s + "!"));
    }

    @Test
    void shouldMapWithExceptionOnSuccess() {
        assertEquals(Try.failure(ERROR), SUCCESS.map(ignored -> { throw ERROR; }));
    }

    @Test
    void shouldThrowNPEWhenMappingFailureAndParamIsNull() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> FAILURE.map(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenMappingSuccessAndParamIsNull() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.map(null)).getMessage()
        );
    }

    // -- .mapFailure(CheckedFunction)

    @Test
    void shouldMapFailureOnFailure() {
        assertEquals(Try.failure(ERROR), FAILURE.mapFailure(x -> ERROR));
    }

    @Test
    void shouldMapFailureOnFailureWhenCauseIsNull() {
        assertEquals(Try.failure(ERROR), Try.failure(null).mapFailure(x -> ERROR));
    }

    @Test
    void shouldMapFailureWithExceptionOnFailure() {
        assertEquals(Try.failure(ERROR), FAILURE.mapFailure(x -> { throw ERROR; }));
    }

    @Test
    void shouldMapFailureOnSuccess() {
        assertSame(SUCCESS, SUCCESS.mapFailure(x -> ERROR));
    }

    @Test
    void shouldMapFailureWithExceptionOnSuccess() {
        assertSame(SUCCESS, SUCCESS.mapFailure(x -> { throw ERROR; }));
    }

    @Test
    void shouldThrowNPEWhenCallingMapFailureOnFailureAndParamIsNull() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> FAILURE.mapFailure(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenCallingMapFailureOnSuccessAndParamIsNull() {
        assertEquals(
                "mapper is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.mapFailure(null)).getMessage()
        );
    }

    // -- .onFailure(Consumer)

    @Test
    void shouldConsumeThrowableWhenCallingOnFailureGivenFailure() {
        final List<Throwable> sideEffect = new ArrayList<>();
        FAILURE.onFailure(sideEffect::add);
        assertEquals(Collections.singletonList(FAILURE_CAUSE), sideEffect);
    }

    @Test
    void shouldNotHandleUnexpectedExceptionWhenCallingOnFailureGivenFailure() {
        assertSame(
                ERROR,
                assertThrows(ERROR.getClass(), () -> FAILURE.onFailure(ignored -> { throw ERROR; }))
        );
    }
    
    @Test
    void shouldDoNothingWhenCallingOnFailureGivenSuccess() {
        assertSame(SUCCESS, SUCCESS.onFailure(x -> { throw ERROR; }));
    }

    @Test
    void shouldThrowNPEWhenCallingOnFailureWithNullParamOnFailure() {
        assertEquals(
                "action is null",
                assertThrows(NullPointerException.class, () -> FAILURE.onFailure(null)).getMessage()
        );
    }
    
    @Test
    void shouldThrowNPEWhenCallingOnFailureWithNullParamOnSuccess() {
        assertEquals(
                "action is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.onFailure(null)).getMessage()
        );
    }

    // -- .onSuccess(Consumer)

    @Test
    void shouldConsumeValueWhenCallingOnSuccessGivenSuccess() {
        final List<String> sideEffect = new ArrayList<>();
        SUCCESS.onSuccess(sideEffect::add);
        assertEquals(Collections.singletonList(SUCCESS_VALUE), sideEffect);
    }

    @Test
    void shouldNotHandleUnexpectedExceptionWhenCallingOnSuccessGivenSuccess() {
        assertSame(
                ERROR,
                assertThrows(ERROR.getClass(), () -> SUCCESS.onSuccess(ignored -> { throw ERROR; }))
        );
    }

    @Test
    void shouldDoNothingWhenCallingOnSuccessGivenFailure() {
        assertSame(FAILURE, FAILURE.onSuccess(x -> { throw ERROR; }));
    }

    @Test
    void shouldThrowNPEWhenCallingOnSuccessWithNullParamOnFailure() {
        assertEquals(
                "action is null",
                assertThrows(NullPointerException.class, () -> FAILURE.onSuccess(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenCallingOnSuccessWithNullParamOnSuccess() {
        assertEquals(
                "action is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.onSuccess(null)).getMessage()
        );
    }

    // -- .orElse(Callable)

    @Test
    void shouldReturnSelfOnOrElseIfSuccess() {
        assertSame(SUCCESS, SUCCESS.orElse(() -> null));
    }

    @Test
    void shouldReturnAlternativeOnOrElseIfFailure() {
        assertSame(SUCCESS, FAILURE.orElse(() -> SUCCESS));
    }

    @Test
    void shouldCaptureErrorOnOrElseIfFailure() {
        assertSame(ERROR, FAILURE.orElse(() -> { throw ERROR; }).getCause());
    }

    @Test
    void shouldThrowNPEOnOrElseWithNullParameterIfSuccess() {
        assertEquals(
                "supplier is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.orElse(null)).getMessage()

        );
    }

    @Test
    void shouldThrowNPEOnOrElseWithNullParameterIfFailure() {
        assertEquals(
                "supplier is null",
                assertThrows(NullPointerException.class, () -> FAILURE.orElse(null)).getMessage()
        );
    }

    // -- .recover(Class, CheckedFunction)

    @Test
    void shouldRecoverWhenFailureMatchesExactly() {
        assertEquals(SUCCESS, FAILURE.recover(FAILURE_CAUSE.getClass(), x -> SUCCESS_VALUE));
    }

    @Test
    void shouldRecoverWhenFailureIsAssignableFrom() {
        assertEquals(SUCCESS, FAILURE.recover(Throwable.class, x -> SUCCESS_VALUE));
    }

    @Test
    void shouldNotRecoverWhenFailureIsNotAssignableFrom() {
        assertEquals(FAILURE, FAILURE.recover(VirtualMachineError.class, x -> SUCCESS_VALUE));
    }

    @Test
    void shouldRecoverWhenSuccess() {
        assertSame(SUCCESS, SUCCESS.recover(Throwable.class, x -> null));
    }

    @Test
    void shouldThrowNPEOnRecoverFailureWhenExceptionTypeIsNull() {
        assertEquals(
                "exceptionType is null",
                assertThrows(NullPointerException.class, () -> FAILURE.recover(null, x -> null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverFailureWhenRecoveryFunctionIsNull() {
        assertEquals(
                "recoveryFunction is null",
                assertThrows(NullPointerException.class, () -> FAILURE.recover(Error.class, null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverSuccessWhenExceptionTypeIsNull() {
        assertEquals(
                "exceptionType is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.recover(null, x -> null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverSuccessWhenRecoveryFunctionIsNull() {
        assertEquals(
                "recoveryFunction is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.recover(Error.class, null)).getMessage()
        );
    }

    // -- .recoverWith(Class, CheckedFunction)

    @Test
    void shouldRecoverWithWhenFailureMatchesExactly() {
        assertSame(SUCCESS, FAILURE.recoverWith(FAILURE_CAUSE.getClass(), x -> SUCCESS));
    }

    @Test
    void shouldRecoverWithSuccessWhenFailureIsAssignableFrom() {
        assertSame(SUCCESS, FAILURE.recoverWith(Throwable.class, x -> SUCCESS));
    }

    @Test
    void shouldRecoverWithFailureWhenFailureIsAssignableFrom() {
        final Try<String> failure = Try.failure(ERROR);
        assertSame(failure, FAILURE.recoverWith(Throwable.class, x -> failure));
    }

    @Test
    void shouldNotRecoverWithWhenFailureIsNotAssignableFrom() {
        assertSame(FAILURE, FAILURE.recoverWith(VirtualMachineError.class, x -> SUCCESS));
    }

    @Test
    void shouldRecoverWithWhenSuccess() {
        assertSame(SUCCESS, SUCCESS.recoverWith(Throwable.class, x -> null));
    }

    @Test
    void shouldThrowNPEOnRecoverWithFailureWhenExceptionTypeIsNull() {
        assertEquals(
                "exceptionType is null",
                assertThrows(NullPointerException.class, () -> FAILURE.recoverWith(null, x -> null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithFailureWhenRecoveryFunctionIsNull() {
        assertEquals(
                "recoveryFunction is null",
                assertThrows(NullPointerException.class, () -> FAILURE.recoverWith(Error.class, null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithSuccessWhenExceptionTypeIsNull() {
        assertEquals(
                "exceptionType is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.recoverWith(null, x -> null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithSuccessWhenRecoveryFunctionIsNull() {
        assertEquals(
                "recoveryFunction is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.recoverWith(Error.class, null)).getMessage()
        );
    }

    @Test
    void shouldCaptureExceptionWhenRecoverWithFailure() {
        assertEquals(Try.failure(ERROR), FAILURE.recoverWith(Throwable.class, ignored -> { throw ERROR; }));
    }

    // -- .stream()

    @Test
    void shouldStreamFailure() {
        assertEquals(Collections.emptyList(), FAILURE.stream().collect(Collectors.toList()));
    }

    @Test
    void shouldStreamSuccess() {
        assertEquals(Collections.singletonList(SUCCESS_VALUE), SUCCESS.stream().collect(Collectors.toList()));
    }

    // -- .toEither(Function)

    @Test
    void shouldConvertFailureToEitherUsingIdentityThrowableMapper() {
        assertEquals(Either.left(FAILURE_CAUSE), FAILURE.toEither(Function.identity()));
    }

    @Test
    void shouldConvertFailureToEitherUsingNonTrivialThrowableMapper() {
        assertEquals(Either.left(ERROR), FAILURE.toEither(ignored -> ERROR));
    }

    @Test
    void shouldConvertSuccessToEitherUsingIdentityThrowableMapper() {
        assertEquals(Either.right(SUCCESS_VALUE), SUCCESS.toEither(Function.identity()));
    }

    @Test
    void shouldThrowNPEWhenConvertingFailureToEitherUsingNullThrowableMapper() {
        assertEquals(
                "failureMapper is null",
                assertThrows(NullPointerException.class, () -> FAILURE.toEither(null)).getMessage()
        );
    }

    @Test
    void shouldThrowNPEWhenConvertingSuccessToEitherUsingNullThrowableMapper() {
        assertEquals(
                "failureMapper is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.toEither(null)).getMessage()
        );
    }

    // -- .toOption()

    @Test
    void shouldConvertFailureToOption() {
        assertEquals(Option.none(), FAILURE.toOption());
    }


    @Test
    void shouldConvertSuccessOfNonNullToOption() {
        assertEquals(Option.some(SUCCESS_VALUE), SUCCESS.toOption());
    }

    @Test
    void shouldConvertSuccessOfNullToOption() {
        assertEquals(Option.some(null), Try.success(null).toOption());
    }

    // -- .toOptional()

    @Test
    void shouldConvertFailureToOptional() {
        assertEquals(Optional.empty(), FAILURE.toOptional());
    }


    @Test
    void shouldConvertSuccessOfNonNullToOptional() {
        assertEquals(Optional.of(SUCCESS_VALUE), SUCCESS.toOptional());
    }

    @Test
    void shouldConvertSuccessOfNullToOptional() {
        assertEquals(Optional.empty(), Try.success(null).toOptional());
    }

    // -- .transform(CheckedFunction, CheckedFunction)

    @Test
    void shouldTransformFailureWhenCauseIsNull() {
        assertSame(SUCCESS, Try.failure(null).transform(x -> SUCCESS, s -> SUCCESS));
    }

    @Test
    void shouldTransformSuccessWhenValueIsNull() {
        assertSame(SUCCESS, Try.success(null).transform(x -> SUCCESS, s -> SUCCESS));
    }

    @Test
    void shouldTransformFailureToNull() {
        assertNull(FAILURE.transform(x -> null, s -> SUCCESS));
    }

    @Test
    void shouldTransformSuccessToNull() {
        assertNull(SUCCESS.transform(x -> FAILURE, s -> null));
    }

    @Test
    void shouldTransformAndReturnValueIfSuccess() {
        final Try<Integer> transformed = SUCCESS.transform(x -> { throw ASSERTION_ERROR; }, s -> Try.success(s.length()));
        assertEquals(Try.success(SUCCESS_VALUE.length()), transformed);
    }

    @Test
    void shouldTransformAndReturnAlternateValueIfFailure() {
        final Try<String> transformed = FAILURE.transform(x -> SUCCESS, a -> { throw ASSERTION_ERROR; });
        assertSame(SUCCESS, transformed);
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnFailureFunctionIsNullIfSuccess() {
        assertEquals(
                "ifFailure is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.transform(null, s -> SUCCESS)).getMessage()
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnFailureFunctionIsNullIfFailure() {
        assertEquals(
                "ifFailure is null",
                assertThrows(NullPointerException.class, () -> FAILURE.transform(null, s -> SUCCESS)).getMessage()
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnSuccessFunctionIsNullIfSuccess() {
        assertEquals(
                "ifSuccess is null",
                assertThrows(NullPointerException.class, () -> SUCCESS.transform(x -> FAILURE, null)).getMessage()
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnSuccessFunctionIsNullIfFailure() {
        assertEquals(
                "ifSuccess is null",
                assertThrows(NullPointerException.class, () -> FAILURE.transform(x -> FAILURE, null)).getMessage()
        );
    }

    @Test
    void shouldTransformFailureAndCaptureException() {
        final Try<String> transformed = FAILURE.transform(x -> { throw ERROR; }, s -> SUCCESS);
        assertEquals(Try.failure(ERROR), transformed);
    }

    @Test
    void shouldTransformSuccessAndCaptureException() {
        final Try<String> transformed = SUCCESS.transform(x -> FAILURE, s -> { throw ERROR; });
        assertEquals(Try.failure(ERROR), transformed);
    }

    // -- Object.equals(Object)

    @Test
    void shouldEqualFailureIfObjectIsSame() {
        assertEquals(FAILURE, FAILURE);
    }

    @Test
    void shouldNotEqualFailureIfObjectIsNotSame() {
        assertNotEquals(Try.failure(new Error()), Try.failure(new Error()));
    }

    @Test
    void shouldEqualSuccessIfObjectIsSame() {
        assertEquals(SUCCESS, SUCCESS);
    }

    @Test
    void shouldNotEqualFailureAndSuccess() {
        assertNotEquals(SUCCESS, FAILURE);
    }

    @Test
    void shouldNotEqualSuccessAndFailure() {
        assertNotEquals(FAILURE, SUCCESS);
    }

    @Test
    void shouldEqualSuccessIfObjectIsNotSame() {
        assertEquals(Try.success(1), Try.success(1));
    }

    @Test
    void shouldNotEqualSuccessIfValueTypesDiffer() {
        assertNotEquals(Try.success("1"), Try.success(1));
    }

    // -- Object.hashCode()

    @Test
    void shouldHashFailure() {
        assertEquals(Objects.hashCode(FAILURE_CAUSE), FAILURE.hashCode());
    }

    @Test
    void shouldHashFailureWithNullCause() {
        assertEquals(Objects.hashCode(null), Try.failure(null).hashCode());
    }

    @Test
    void shouldHashSuccess() {
        assertEquals(31 + Objects.hashCode(SUCCESS_VALUE), SUCCESS.hashCode());
    }

    @Test
    void shouldHashSuccessWithNullValue() {
        assertEquals(31 + Objects.hashCode(null), Try.success(null).hashCode());
    }

    // -- Object.toString()

    @Test
    void shouldConvertFailureToString() {
        assertEquals("Failure(" + FAILURE_CAUSE + ")", FAILURE.toString());
    }

    @Test
    void shouldConvertFailureWithNullCauseToString() {
        assertEquals("Failure(null)", Try.failure(null).toString());
    }

    @Test
    void shouldConvertSuccessToString() {
        assertEquals("Success(" + SUCCESS_VALUE + ")", SUCCESS.toString());
    }

    @Test
    void shouldConvertSuccessWithNullValueToString() {
        assertEquals("Success(null)", Try.success(null).toString());
    }

    // Serialization

    @Test
    void shouldSerializeFailure() throws IOException, ClassNotFoundException {
        final Try<String> testee = deserialize(serialize(FAILURE));
        assertSame(FAILURE.getCause().getClass(), testee.getCause().getClass());
        assertEquals(FAILURE.getCause().getMessage(), testee.getCause().getMessage());
    }

    @Test
    void shouldSerializeSuccess() throws IOException, ClassNotFoundException {
        assertEquals(SUCCESS, deserialize(serialize(SUCCESS)));
    }

    private static byte[] serialize(Object obj) throws IOException {
        try (final var buf = new ByteArrayOutputStream(); final var stream = new ObjectOutputStream(buf)) {
            stream.writeObject(obj);
            return buf.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (final var stream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) stream.readObject();
        }
    }
}
