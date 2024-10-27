package com.example.auth

object AuthValidator {

    private val EMAIL_REGEX = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+")
    private val PASSWORD_REGEX = Regex(
        "^" +
                "(?=.*[a-z])"+
                "[a-zA-Z0-9@#$%^&*!?+=]*" +
                ".{6,12}" +
                "$"
    )

    fun isEmailAndPasswordIsEmpty(
        email: String,
        password: String,
        repeatPassword: String
    ): Boolean {
        return email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()
    }

    fun isEmailMatcher(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    fun isPasswordMatcher(password: String): Boolean {
        return PASSWORD_REGEX.matches(password)
    }

    fun validateEmailAndPassword(email: String, password: String): Boolean {
        return isEmailMatcher(email) &&
                isPasswordMatcher(password)
    }

    fun validateRepeatPassword(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }
}