package me.m123.video.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.m123.video.R
import me.m123.video.data.SearchTypeBean

class FragmentTv: BaseFFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_video_list, container, false)
        this.video_types = arrayListOf(
                SearchTypeBean(0, "全部"), SearchTypeBean(13, "大陆剧"), SearchTypeBean(14, "港台剧"),
                SearchTypeBean(15, "欧美剧"), SearchTypeBean(16, "日韩剧")
        )
        this.tid = "1002"
        this.initUI(view)
        this.loadingMore(20)
        return view
    }

    companion object {
        fun newInstance(): FragmentTv = FragmentTv()
    }
}