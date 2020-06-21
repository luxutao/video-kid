package me.m123.video.data

data class LoginResponse(
        var code: Int,
        var msg: String,
        var data: LoginData
)

data class LoginData(
        var userid: Int,
        var token: String
)