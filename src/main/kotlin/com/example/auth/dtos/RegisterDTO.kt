package com.example.auth.dtos

data class RegisterDTO(
    var id: Int? = null,
    var name: String = "",
    var email: String = "",
    var password: String = ""
)