package cn.animekid.videokid.fragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.ImageAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.ImageData
import cn.animekid.videokid.data.ListDataBean
import cn.animekid.videokid.data.SearchTypeBean
import cn.animekid.videokid.ui.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class BaseFFragment : Fragment() {

    var videoImageList: ArrayList<ImageData> = arrayListOf()
    lateinit var videoAdapter : ImageAdapter
    lateinit var errorview : LinearLayout
    lateinit var gridList: GridView

     var movie_types: java.util.ArrayList<SearchTypeBean> = arrayListOf(
            SearchTypeBean(0, "全部"), SearchTypeBean(9, "战争片"), SearchTypeBean(10, "喜剧片"),
            SearchTypeBean(6, "爱情片"), SearchTypeBean(5, "动作片"), SearchTypeBean(8, "恐怖片"),
            SearchTypeBean(7, "科幻片"), SearchTypeBean(12, "剧情片"), SearchTypeBean(11, "纪录片")
    )
     var movie_areas: java.util.ArrayList<SearchTypeBean> = arrayListOf(
            SearchTypeBean(100, "全部"), SearchTypeBean(101, "大陆"), SearchTypeBean(102, "香港"),
            SearchTypeBean(103, "台湾"), SearchTypeBean(104, "日本"), SearchTypeBean(105, "美国"),
            SearchTypeBean(106, "泰国"), SearchTypeBean(107, "印度"), SearchTypeBean(108, "西班牙"),
            SearchTypeBean(109, "韩国"), SearchTypeBean(110, "法国"), SearchTypeBean(111, "其他")
    )
     var movie_langs: java.util.ArrayList<SearchTypeBean> = arrayListOf(
            SearchTypeBean(1000, "全部"), SearchTypeBean(1001, "国语"), SearchTypeBean(1002, "粤语"),
            SearchTypeBean(1003, "英语"), SearchTypeBean(1004, "日语"), SearchTypeBean(1005, "韩语"),
            SearchTypeBean(1006, "其他")
    )
     var movie_years: java.util.ArrayList<SearchTypeBean> = arrayListOf(SearchTypeBean(10000, "全部"))
     lateinit var movieTypesGroup: RadioGroup
     lateinit var movieAreasGroup: RadioGroup
     lateinit var movieLangsGroup: RadioGroup
     lateinit var movieYearsGroup: RadioGroup
     var params: ContentValues = ContentValues()
    var vtp: String = ""

    init {
        this.params.put("type", "")
        this.params.put("area", "")
        this.params.put("lang", "")
        this.params.put("year", "")
        this.makeYears()
    }

    fun initUI(view: View) {
        this.videoAdapter = ImageAdapter(view.context, this.videoImageList)
        this.gridList = view.findViewById(R.id.gridList)
        this.gridList.adapter = this.videoAdapter
        this.errorview = view.findViewById(R.id.noInternet)
        this.movieTypesGroup = view.findViewById(R.id.movie_types)
        this.movieAreasGroup = view.findViewById(R.id.movie_areas)
        this.movieLangsGroup = view.findViewById(R.id.movie_langs)
        this.movieYearsGroup = view.findViewById(R.id.movie_years)
        this.makeRadio(this.movie_types, this.movieTypesGroup)
        this.makeRadio(this.movie_areas, this.movieAreasGroup)
        this.makeRadio(this.movie_langs, this.movieLangsGroup)
        this.makeRadio(this.movie_years, this.movieYearsGroup)

        this.movieTypesGroup.setOnCheckedChangeListener(this.radioListener(view, "type", true))
        this.movieAreasGroup.setOnCheckedChangeListener(this.radioListener(view, "area", false))
        this.movieLangsGroup.setOnCheckedChangeListener(this.radioListener(view, "lang", false))
        this.movieYearsGroup.setOnCheckedChangeListener(this.radioListener(view, "year", false))
        this.gridList.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = this@BaseFFragment.videoImageList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@BaseFFragment.loadingMore(page)
                }
            }
        })
        this.gridList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = this@BaseFFragment.videoImageList.get(index.toInt())
            val intent = Intent(this.context, PlayerActivity::class.java)
            intent.putExtra("v_id", bean.v_id)
            startActivity(intent)
        }
    }

    fun loadingMore(page: Int, reload: Boolean = false) {
        Requester.ImageService().getVideo(area = this.params["area"].toString(), type = this.params["type"].toString(),
                lang = this.params["lang"].toString(), year = this.params["year"].toString(),vtp = this.vtp, page = page).enqueue(object: Callback<ListDataBean> {
            override fun onResponse(call: Call<ListDataBean>?, response: Response<ListDataBean>?) {
                this@BaseFFragment.gridList.visibility = View.VISIBLE
                Log.e("tag", response!!.body()!!.data.toString())
                this@BaseFFragment.errorview.visibility = View.GONE
                if (reload) {
                    this@BaseFFragment.videoImageList.clear()
                    this@BaseFFragment.gridList.smoothScrollToPositionFromTop(0, 0)
                }
                this@BaseFFragment.videoImageList.addAll(response.body()!!.data)
                this@BaseFFragment.videoAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<ListDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@BaseFFragment.gridList.visibility = View.GONE
                this@BaseFFragment.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun radioListener(v: View, key: String, column: Boolean = false):RadioGroup.OnCheckedChangeListener {
        return RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val value = v.findViewById<RadioButton>(checkedId)
            this.params.remove(key)
            if (column) {
                if (checkedId == 0) {
                    this.params.put(key, "")
                } else {
                    this.params.put(key, checkedId)
                }
            } else {
                val _v = value.text.toString()
                if (_v == "全部") {
                    this.params.put(key, "")
                } else {
                    this.params.put(key, _v)
                }
            }

            Log.e("params", this.params.toString())

            Log.e("group", this.params.get(key).toString())
            this.loadingMore(1, true)
        }
    }

    fun makeRadio(list: ArrayList<SearchTypeBean>, group: RadioGroup) {
        for (i in list) {
            val radioItem = this.layoutInflater.inflate(R.layout.selector_item, group, false)
            val radio = radioItem.findViewById<RadioButton>(R.id.radioButtonItem)
            radio.text = i.name
            radio.id = i.id
            if (i.name == "全部") {
                radio.isChecked = true
            }
            group.addView(radio)
            group.invalidate()
        }
    }

    fun makeYears() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 0..10) {
            val changeYear = year - i
            this.movie_years.add(SearchTypeBean(year, changeYear.toString()))
        }
        this.movie_years.add(SearchTypeBean(10001, "更早"))
    }


}