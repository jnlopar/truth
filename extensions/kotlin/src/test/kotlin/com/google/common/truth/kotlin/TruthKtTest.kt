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
