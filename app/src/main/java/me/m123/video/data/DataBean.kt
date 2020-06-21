package me.m123.video.data

data class SearchTypeBean(
        var id: Int,
        var name: String
)

data class SpicDataBean(
        val count: Int,
        val next: String,
        val previous: String,
        val results: List<Data>
) {
    data class Data(
            val v_id: Int,
            val v_name: String,
            val v_spic: String
    )
}

data class HotSearchDataBean(
        val count: Int,
        val next: String,
        val previous: String,
        val results: List<Data>
) {
    data class Data(
            val v_name: String
    )
}

data class DetailDataBean(
        val tid: Tid,
        val v_actor: List<Actor>,
        val v_addtime: Int,
        val v_content: String,
        val v_digg: Int,
        val v_director: String,
        val v_enname: String,
        val v_id: Int,
        val v_lang: String,
        val v_name: String,
        val v_note: String,
        val v_pic: String,
        val v_playdata: List<VPlaydata>,
        val v_publisharea: String,
        val v_publishyear: Int,
        val v_score: Int,
        val v_scorenum: Int,
        val v_spic: String,
        val v_tread: Int,
        val score: String,
        val v_hit: Int,
        val is_collect: String,
        val v_actors: String
) {
    data class Tid(
            val tenname: String,
            val tid: Int,
            val tname: String,
            val upid: Int
    )

    data class Actor(
            val avatar: String,
            val name: String
    )

    data class VPlaydata(
            val playdata: List<Playdata>,
            val player: String
    ) {

        data class Playdata(
                val gather: String,
                val url: String
        )
    }
}


data class VideoListResultDataBean(
        val count: Int,
        val next: String,
        val previous: String,
        val results: List<Data>
) {
    data class Data(
            val v_id: Int,
            val tid: Int,
            val v_actor: String,
            val v_addtime: Int,
            val v_content: String,
            val v_digg: Int,
            val v_director: String,
            val v_enname: String,
            val v_lang: String,
            val v_name: String,
            val v_note: String,
            val v_spic: String,
            val v_pic: String,
            val v_publisharea: String,
            val v_publishyear: Int,
            val v_score: Int,
            val v_scorenum: Int,
            val v_tread: Int,
            val score: String,
            val v_hit: Int
    )
}