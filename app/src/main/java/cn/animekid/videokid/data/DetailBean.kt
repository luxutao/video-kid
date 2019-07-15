package cn.animekid.videokid.data

data class DetailBean(
    val `data`: Data,
    val code: Int,
    val msg: String
)

data class Data(
    val tid: Tid,
    val v_actor: List<Actor>,
    val v_addtime: Int,
    val v_content: VContent,
    val v_digg: Int,
    val v_director: String,
    val v_enname: String,
    val v_id: Int,
    val v_lang: String,
    val v_name: String,
    val v_note: String,
    val v_pic: String,
    val v_playdata: VPlaydata,
    val v_publisharea: String,
    val v_publishyear: Int,
    val v_score: Int,
    val v_scorenum: Int,
    val v_spic: String,
    val v_tread: Int,
    val score: String,
    val v_hit: Int
)

data class Actor(
    val avatar: String,
    val name: String
)

data class VPlaydata(
    val body: List<Body>,
    val tid: Int,
    val v_id: Int
)

data class Body(
    val playdata: List<Playdata>,
    val player: String
)

data class Playdata(
    val gather: String,
    val url: String
)

data class Tid(
    val tenname: String,
    val tid: Int,
    val tname: String,
    val upid: Int
)

data class VContent(
    val body: String,
    val tid: Int,
    val v_id: Int
)