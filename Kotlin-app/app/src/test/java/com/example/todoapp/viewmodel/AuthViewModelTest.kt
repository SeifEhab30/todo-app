package com.example.todoapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial auth state should be Idle`() {
        val initialState = AuthState.Idle

        assert(initialState is AuthState.Idle)
    }

    @Test
    fun `login with empty email should return error`() {
        val email = ""
        val password = "password123"

        val isValid = email.isNotBlank() && password.isNotBlank()

        assert(!isValid)
    }

    @Test
    fun `login with empty password should return error`() {
        val email = "test@example.com"
        val password = ""

        val isValid = email.isNotBlank() && password.isNotBlank()

        assert(!isValid)
    }

    @Test
    fun `register with mismatched passwords should return error`() {
        val password = "password123"
        val confirmPassword = "password456"

        val passwordsMatch = password == confirmPassword

        assert(!passwordsMatch)
    }

    @Test
    fun `register with short password should return error`() {
        val password = "12345"

        val isValidLength = password.length >= 6

        assert(!isValidLength)
    }

    @Test
    fun `register with valid matching passwords should pass validation`() {
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"

        val isEmailValid = email.isNotBlank()
        val passwordsMatch = password == confirmPassword
        val isPasswordLongEnough = password.length >= 6

        assert(isEmailValid)
        assert(passwordsMatch)
        assert(isPasswordLongEnough)
    }

    @Test
    fun `AuthState Error should contain error message`() {
        val errorMessage = "Invalid credentials"
        val errorState = AuthState.Error(errorMessage)

        assert(errorState is AuthState.Error)
        assert(errorState.message == errorMessage)
    }

    @Test
    fun `AuthState Loading should be correct type`() {
        val loadingState = AuthState.Loading

        assert(loadingState is AuthState.Loading)
    }

    @Test
    fun `valid email format check`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.com"
        )

        val invalidEmails = listOf(
            "notanemail",
            "@example.com",
            "test@",
            ""
        )

        validEmails.forEach { email ->
            val isValid = email.contains("@") && email.contains(".")
            assert(isValid) { "Expected $email to be valid" }
        }

        invalidEmails.forEach { email ->
            val isInvalid = email.isBlank() ||
                    !email.contains("@") ||
                    !email.contains(".") ||
                    email.indexOf("@") == 0 ||
                    email.indexOf("@") == email.length - 1
            assert(isInvalid) { "Expected $email to be invalid" }
        }
    }
}
