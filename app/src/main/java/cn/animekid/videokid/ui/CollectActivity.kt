package cn.animekid.videokid.ui

import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.ImageAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.ImageData
import cn.animekid.videokid.data.ListDataBean
import cn.animekid.videokid.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectActivity: BaseAAppCompatActivity() {

    private lateinit var myCollectList: GridView
    private var collectData: ArrayList<ImageData> = arrayListOf()
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
        Requester.VideoService().getCollectList(token = ToolsHelper.getToken(this), page = page).enqueue(object: Callback<ListDataBean> {
            override fun onResponse(call: Call<ListDataBean>?, response: Response<ListDataBean>?) {
                this@CollectActivity.collectData.addAll(response!!.body()!!.data)
                this@CollectActivity.collectAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<ListDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@CollectActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }
}