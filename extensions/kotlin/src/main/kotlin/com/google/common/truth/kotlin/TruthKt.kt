package com.google.common.truth.kotlin

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.DefaultSubject
import com.google.common.truth.Expect
import com.google.common.truth.ExpectFailure
import com.google.common.truth.IterableSubject
import com.google.common.truth.StringSubject
import com.google.common.truth.Subject

// "Top-level" assertThat wrappers.

inline infix fun Iterable<*>?.that(init: IterableSubject.() -> Unit) {
    _assertKt { it that init }
}

inline infix fun String?.that(init: StringSubject.() -> Unit) {
    _assertKt { it that init }
}

inline infix fun <T : Any> T?.that(init: Subject<DefaultSubject, Any?>.() -> Unit) {
    _assertKt { it that init }
}

inline fun <T> T._assertKt(init: AssertKt.(T) -> Unit) {
    let { AssertKt.apply { init(it) } }
}

object AssertKt : ThatFn() {
    override fun Iterable<*>?.subject() = assertThat(this)
    override fun String?.subject() = assertThat(this)
    override fun <T : Any> T?.subject() = assertThat(this)
}

inline operator fun Expect.invoke(init: ExpectKt.() -> Unit) = ExpectKt(this).apply(init)

class ExpectKt(val expect: Expect) : ThatFn() {
    override fun Iterable<*>?.subject() = expect.that(this)
    override fun String?.subject() = expect.that(this)
    override fun <T : Any> T?.subject() = expect.that(this)
}

inline operator fun ExpectFailure.invoke(init: ExpectFailureKt.() -> Unit) = ExpectFailureKt(this).apply(init)

class ExpectFailureKt(val expectFailure: ExpectFailure) : ThatFn() {
    override fun Iterable<*>?.subject() = expectFailure.whenTesting().that(this)
    override fun String?.subject() = expectFailure.whenTesting().that(this)
    override fun <T : Any> T?.subject() = expectFailure.whenTesting().that(this)
}

sealed class ThatFn {
    abstract fun Iterable<*>?.subject(): IterableSubject
    abstract fun String?.subject(): StringSubject
    abstract fun <T : Any> T?.subject(): Subject<DefaultSubject, Any?>

    inline infix fun Iterable<*>?.that(init: IterableSubject.() -> Unit) {
        this.subject().apply(init)
    }

    inline infix fun String?.that(init: StringSubject.() -> Unit) {
        this.subject().apply(init)
    }

    inline infix fun <T : Any> T?.that(init: Subject<DefaultSubject, Any?>.() -> Unit) {
        this.subject().apply(init)
    }
}

inline fun <T : Subject<T, U>, U : Any> T.isNamed(name: String, init: T.() -> Unit) {
    this.named(name).apply(init)
}

infix fun ExpectFailure.withFacts(entries: List<Pair<String, String?>>) {
    ExpectFailure.assertThat(failure).apply {
        for ((key, value) in entries.filter({ (_, value) -> value != null })) {
            factValue(key).apply {
                isNamed("fact value for key $key") {
                    isEqualTo(value ?: "null")
                }
            }
        }

        val nullKeys = entries
            .filter({ (_, value) -> value == null })
            .map({ it.first })

        factKeys().apply {
            isNamed("fact keys") {
                containsNoneIn(nullKeys)
            }
        }
    }
}
