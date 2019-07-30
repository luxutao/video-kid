package cn.animekid.videokid.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.ImageAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.HomeNewsDataBean
import cn.animekid.videokid.data.ImageData
import cn.animekid.videokid.data.ListSpicBean
import cn.animekid.videokid.ui.PlayerActivity
import cn.animekid.videokid.utils.AutoHeightGridView
import com.app.abby.xbanner.AbstractUrlLoader
import com.app.abby.xbanner.XBanner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentHome: Fragment() {

    private lateinit var xbanner: XBanner
    private var movieList: ArrayList<ImageData> = arrayListOf()
    private var tvList: ArrayList<ImageData> = arrayListOf()
    private var animeList: ArrayList<ImageData> = arrayListOf()
    private var showsList: ArrayList<ImageData> = arrayListOf()
    private lateinit var movieAdapter: ImageAdapter
    private lateinit var tvAdapter: ImageAdapter
    private lateinit var animeAdapter: ImageAdapter
    private lateinit var showsAdapter: ImageAdapter
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var errorview : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        this.swipeLayout = view.findViewById(R.id.swiperereshlayout)

        this.swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light)

        this.swipeLayout.setOnRefreshListener {
            //设置2秒的时间来执行以下事件
            Handler().postDelayed(Runnable {
                this.movieList.clear()
                this.tvList.clear()
                this.animeList.clear()
                this.showsList.clear()
                this.getnewData(view)
                this.swipeLayout.isRefreshing = false
            }, 2000)
        }
        this.errorview = view.findViewById(R.id.noInternet)
        this.initXBanner(view)
        this.getnewData(view)
        return view
    }
    
    fun initXBanner(v: View) {
        this.xbanner = v.findViewById(R.id.xbanner)
        val imageList: ArrayList<String> = arrayListOf()
        val titleList: ArrayList<String> = arrayListOf()
        Requester.VideoService().getSpic().enqueue(object: Callback<ListSpicBean> {
            override fun onResponse(call: Call<ListSpicBean>, response: Response<ListSpicBean>) {
                val data = response.body()!!.data
                for (i in data) {
                    imageList.add(i.v_spic)
                    titleList.add(i.v_name)
                }
                this@FragmentHome.xbanner.isAutoPlay(true)
                    .setBannerTypes(XBanner.NUM_INDICATOR_TITLE)
                    .setDelay(5000)
                    .setImageUrls(imageList)
                    .setTitles(titleList)
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setBannerPageListener(object: XBanner.BannerPageListener {
                        override fun onBannerClick(item: Int) {
                            val detaildata = data.get(item)
                            val intent = Intent(this@FragmentHome.context, PlayerActivity::class.java)
                            intent.putExtra("v_id", detaildata.v_id)
                            startActivity(intent)
                        }

                        override fun onBannerDragging(item: Int) {
                        }

                        override fun onBannerIdle(item: Int) {
                        }
                    })
                    .setImageLoader(object : AbstractUrlLoader() {
                        override fun loadImages(context: Context?, url: String?, image: ImageView?) {
                            Glide.with(context!!)
                                .load(url)
                                .apply(RequestOptions.placeholderOf(R.color.colorBlackT))
                                .apply(RequestOptions.errorOf(R.color.colorBlackT))
                                .into(image!!)
                        }

                        override fun loadGifs(url: String?, gifImageView: GifImageView?, scaleType: ImageView.ScaleType?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }).start()
            }

            override fun onFailure(call: Call<ListSpicBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })

    }
    
    
    fun getnewData(v: View) {
        this.movieAdapter = ImageAdapter(v.context, this.movieList)
        this.tvAdapter = ImageAdapter(v.context, this.tvList)
        this.animeAdapter = ImageAdapter(v.context, this.animeList)
        this.showsAdapter = ImageAdapter(v.context, this.showsList)
        val movieList = v.findViewById<AutoHeightGridView>(R.id.newMovieList)
        val tvList = v.findViewById<AutoHeightGridView>(R.id.newTvList)
        val animeList = v.findViewById<AutoHeightGridView>(R.id.newAnimeList)
        val showsList = v.findViewById<AutoHeightGridView>(R.id.newShowsList)
        movieList.onItemClickListener = this.onItem(this.movieList)
        tvList.onItemClickListener = this.onItem(this.tvList)
        animeList.onItemClickListener = this.onItem(this.animeList)
        showsList.onItemClickListener = this.onItem(this.showsList)
        Requester.VideoService().getNew().enqueue(object: Callback<HomeNewsDataBean> {
            override fun onResponse(call: Call<HomeNewsDataBean>, response: Response<HomeNewsDataBean>) {
                this@FragmentHome.swipeLayout.visibility = View.VISIBLE
                this@FragmentHome.errorview.visibility = View.GONE
                movieList.adapter = this@FragmentHome.movieAdapter
                tvList.adapter = this@FragmentHome.tvAdapter
                animeList.adapter = this@FragmentHome.animeAdapter
                showsList.adapter = this@FragmentHome.showsAdapter
                val data = response.body()!!.data
                this@FragmentHome.movieList.addAll(data.movie)
                this@FragmentHome.tvList.addAll(data.tv)
                this@FragmentHome.animeList.addAll(data.anime)
                this@FragmentHome.showsList.addAll(data.shows)
                this@FragmentHome.movieAdapter.notifyDataSetChanged()
                this@FragmentHome.tvAdapter.notifyDataSetChanged()
                this@FragmentHome.showsAdapter.notifyDataSetChanged()
                this@FragmentHome.animeAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<HomeNewsDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onItem(dataList: ArrayList<ImageData>) : AdapterView.OnItemClickListener {
        val onItem = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = dataList.get(index.toInt())
            val intent = Intent(this.context, PlayerActivity::class.java)
            intent.putExtra("v_id", bean.v_id)
            startActivity(intent)
        }
        return onItem
    }


    companion object {
        fun newInstance(): FragmentHome = FragmentHome()
    }

    override fun onDestroy() {
        super.onDestroy()
        xbanner.releaseBanner()
    }

}