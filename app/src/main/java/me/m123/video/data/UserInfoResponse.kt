package me.m123.video.data

data class UserInfoResponse(
        var id: Int,
        var username: String,
        var first_name: String,
        var last_name: String,
        var last_login: String,
        var date_joined: String,
        var email: String,
        var gender: String,
        var avatar: String
)