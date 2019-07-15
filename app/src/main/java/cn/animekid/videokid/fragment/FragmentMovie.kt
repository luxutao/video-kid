package cn.animekid.videokid.fragment

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import cn.animekid.videokid.R
import cn.animekid.videokid.data.PostDataBean
import cn.animekid.videokid.data.SearchTypeBean
import org.jetbrains.anko.custom.style
import java.util.*

class FragmentMovie: BaseFFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        this.vtp = "movie"
        this.initUI(view)
        this.loadingMore(1)
        return view
    }

    companion object {
        fun newInstance(): FragmentMovie = FragmentMovie()
    }
}