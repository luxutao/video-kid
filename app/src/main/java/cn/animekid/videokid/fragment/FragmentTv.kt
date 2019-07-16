package cn.animekid.videokid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.animekid.videokid.R
import cn.animekid.videokid.data.SearchTypeBean

class FragmentTv: BaseFFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tv, container, false)
        this.movie_types = arrayListOf(
                SearchTypeBean(0, "全部"), SearchTypeBean(13, "大陆剧"), SearchTypeBean(14, "港台剧"),
                SearchTypeBean(15, "欧美剧"), SearchTypeBean(16, "日韩剧")
        )
        this.vtp = "tv"
        this.initUI(view)
        this.loadingMore(1)
        return view
    }

    companion object {
        fun newInstance(): FragmentTv = FragmentTv()
    }
}