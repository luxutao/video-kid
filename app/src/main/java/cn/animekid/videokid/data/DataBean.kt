package cn.animekid.videokid.data

data class ImageData(
    var v_id: Int,
    var v_score: String,
    var v_pic: String,
    var v_name: String,
    var v_note: String
)

data class HomeNewsDataBean(
    var code: Int,
    var data: Data,
    var msg: String
){
    data class Data(
        var movie: List<ImageData>,
        var tv: List<ImageData>,
        var anime: List<ImageData>,
        var shows: List<ImageData>
    )

}

data class ListDataBean(
    var code: Int,
    var data: List<ImageData>,
    var msg: String
)

data class ListSpicBean(
    var code: Int,
    var data: List<Data>,
    var msg: String
){
    data class Data(
        var v_id: Int,
        var v_name: String,
        var v_spic: String
    )
}

data class UserInfoBean(
    var code: Int,
    var msg: String,
    var data: Data
) {
    data class Data(
        var userid: Int,
        var token: String,
        var name: String,
        var create_time: String,
        var email: String,
        var sex: String,
        var avatar: String
    )
}

data class SearchTypeBean(
    var id: Int,
    var name: String
)

data class PostDataBean(
    var type: String,
    var area: String,
    var lang: String,
    var year: String
)

