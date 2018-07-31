package com.google.common.truth.kotlin

import com.google.common.truth.Expect
import com.google.common.truth.ExpectFailure
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TruthKtTest {
    @get:Rule val expect = Expect.create()
    @get:Rule val expectFailure = ExpectFailure()

    val nullString: String? = null
    val nullIterable: List<String>? = null
    val nullAny: Any? = null

    @Test
    fun simpleSuccessfulAssertion() {
        "foobarbaz" that {
            isEqualTo("foobarbaz")
        }
    }

    @Test
    fun simpleFailedAssertion() {
        expectFailure {
            "foobarbaz" that { isEqualTo("fizzbuzz") }
        }
    }

    @Test
    fun stringCompoundAssertion() {
        expectFailure {
            "foobarbaz" that { isEqualTo("fizzbuzz") }
        }
        "foobarbaz" that {
            isEqualTo("foobarbaz")
            hasLength(9)
        }
    }

    @Test
    fun expectFailureMessageUnnamed() {
        expectFailure {
            "foobarbaz" that { isEqualTo("fizzbuzz") }
        }
        expectFailure withFacts(listOf(
            "name" to null,
            "expected" to "fizzbuzz",
            "but was" to "foobarbaz"))
    }

    @Test
    fun expectFailureMessageNamed() {
        expectFailure {
            "foobarbaz" that { isNamed("the foo") { isEqualTo("fizzbuzz") } }
        }
        expectFailure withFacts(listOf(
            "name" to "the foo",
            "expected" to "fizzbuzz",
            "but was" to "foobarbaz"))
    }

    @Test
    fun stringCompoundAssertionWithAssertionFail() {
        val string = "foobarbaz"
        string that {
            isEqualTo("foobarbaz")
            hasLength(9)
        }

        failOnSuccess("should have noticed incorrect string length") {
            string that {
                isEqualTo("foobarbaz")
                hasLength(3)
            }
        }
    }

    @Test
    fun simpleExpectSuccess() {
        expect {
            "foobarbaz" that {
                isEqualTo("foobarbaz")
                hasLength(9)
            }
        }
    }

    @Test
    fun assertSupportsNullSubjects() {
        nullAny that { isNull() }
        nullIterable that { isNull() }
        nullString that { isNull() }
    }

    @Test
    fun expectSupportsNullSubjects() {
        expect {
            nullAny that { isNull() }
            nullIterable that { isNull() }
            nullString that { isNull() }
        }
    }

    @Test
    fun expectFailureSupportsNullAnySubjects() {
        expectFailure { nullAny that { isNotNull() } }
    }

    @Test
    fun expectFailureSupportsNullIterableSubjects() {
        expectFailure { nullIterable that { isNotNull() } } }

    @Test
    fun expectFailureSupportsNullStringSubjects() {
        expectFailure { nullString that { isNotNull() } }
    }

    private inline fun failOnSuccess(msg: String? = null, init: () -> Unit) {
        try {
            init()
            throw(ShouldHaveFailed())
        } catch (e: Throwable) {
            when (e) {
                is ShouldHaveFailed -> fail(msg)
                else -> {}
            }
        }
    }
}

private class ShouldHaveFailed : Exception()
