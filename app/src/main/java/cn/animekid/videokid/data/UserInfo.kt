package cn.animekid.videokid.data


data class UserInfo(
        var code: Int,
        var msg: String,
        var data: UserInfoData
)

data class UserInfoData(
        var userid: Int,
        var token: String,
        var name: String,
        var create_time: String,
        var email: String,
        var sex: String,
        var avatar: String
)