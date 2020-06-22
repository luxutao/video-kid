package me.m123.video.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.m123.video.R
import me.m123.video.data.SearchTypeBean

class FragmentAnime: BaseFFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_video_list, container, false)
        this.video_types = arrayListOf(
                SearchTypeBean(0, "全部")
        )
        this.tid = "1004"
        this.initUI(view)
        this.loadingMore(limit = 20)
        return view
    }

    companion object {
        fun newInstance(): FragmentAnime = FragmentAnime()
    }
}