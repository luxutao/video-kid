package cn.animekid.videokid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.animekid.videokid.R
import cn.animekid.videokid.data.SearchTypeBean

class FragmentShows: BaseFFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_video_list, container, false)
        this.video_types = arrayListOf(
                SearchTypeBean(0, "全部")
        )
        this.vtp = "shows"
        this.initUI(view)
        this.loadingMore(1, size = 20)
        return view
    }

    companion object {
        fun newInstance(): FragmentShows = FragmentShows()
    }
}