package me.m123.video.ui

import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import me.m123.video.R
import me.m123.video.adapter.ImageAdapter
import me.m123.video.api.Requester
import me.m123.video.data.VideoListResultDataBean
import me.m123.video.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectActivity: BaseAAppCompatActivity() {

    private lateinit var myCollectList: GridView
    private var collectData: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private lateinit var collectAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        this.loadingData(1)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_collect_list
    }

    override fun getToolbarTitle(): Int {
        return R.string.my_collect
    }

    fun initUI() {
        this.myCollectList = this.findViewById(R.id.my_collect_list)
        this.collectAdapter = ImageAdapter(this, this.collectData)
        this.myCollectList.adapter = this.collectAdapter
        this.myCollectList.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = this@CollectActivity.collectData.size
                val page : Int = length / 20 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%20 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@CollectActivity.loadingData(page)
                }
            }
        })
        this.myCollectList.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = this@CollectActivity.collectData.get(index.toInt())
            this.isLogin(bean.v_id)
        }
    }

    fun loadingData(page: Int) {
        Requester.CollectService().getCollectList(token = ToolsHelper.getToken(this)).enqueue(object: Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>?, response: Response<VideoListResultDataBean>?) {
                this@CollectActivity.collectData.addAll(response!!.body()!!.results)
                this@CollectActivity.collectAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@CollectActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }
}