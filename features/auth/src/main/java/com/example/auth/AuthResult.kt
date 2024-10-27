package com.example.auth

sealed class AuthResult {
    data object LoginResult : AuthResult()
    data object RegisterResult : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
}