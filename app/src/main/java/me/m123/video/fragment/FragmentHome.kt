package me.m123.video.fragment

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
import me.m123.video.R
import me.m123.video.adapter.ImageAdapter
import me.m123.video.api.Requester
import me.m123.video.ui.PlayerActivity
import me.m123.video.utils.AutoHeightGridView
import me.m123.video.utils.MarqueeText
import com.app.abby.xbanner.AbstractUrlLoader
import com.app.abby.xbanner.XBanner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.m123.video.data.*
import me.m123.video.utils.ToolsHelper
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentHome: Fragment() {

    private lateinit var xbanner: XBanner
    private var movieList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private var tvList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private var animeList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private var showsList: ArrayList<VideoListResultDataBean.Data> = arrayListOf()
    private lateinit var movieAdapter: ImageAdapter
    private lateinit var tvAdapter: ImageAdapter
    private lateinit var animeAdapter: ImageAdapter
    private lateinit var showsAdapter: ImageAdapter
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var errorview : LinearLayout
    private lateinit var AnnouncementTxt: MarqueeText
    lateinit var activityInteraction: BaseFFragment.FragmentInteraction


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        this.swipeLayout = view.findViewById(R.id.swiperereshlayout)
        this.AnnouncementTxt = view.findViewById(R.id.txt)

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
        this.getAnnouncementTxt()
        return view
    }
    
    fun initXBanner(v: View) {
        this.xbanner = v.findViewById(R.id.xbanner)
        val imageList: ArrayList<String> = arrayListOf()
        val titleList: ArrayList<String> = arrayListOf()
        Requester.VideoService().getSpic(token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<SpicDataBean> {
            override fun onResponse(call: Call<SpicDataBean>, response: Response<SpicDataBean>) {
                val data = response.body()!!.results
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
                            this@FragmentHome.activityInteraction.isLogin(detaildata.v_id)
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

            override fun onFailure(call: Call<SpicDataBean>, t: Throwable) {
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
        this@FragmentHome.swipeLayout.visibility = View.VISIBLE
        this@FragmentHome.errorview.visibility = View.GONE

        Requester.VideoService().getNew(new = "movie", token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val data = response.body()!!.results
                movieList.adapter = this@FragmentHome.movieAdapter
                this@FragmentHome.movieList.addAll(data)
            }
            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
        Requester.VideoService().getNew(new = "tv", token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val data = response.body()!!.results
                tvList.adapter = this@FragmentHome.tvAdapter
                this@FragmentHome.tvList.addAll(data)
            }
            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
        Requester.VideoService().getNew(new = "anime", token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val data = response.body()!!.results
                animeList.adapter = this@FragmentHome.animeAdapter
                this@FragmentHome.animeList.addAll(data)
            }
            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
        Requester.VideoService().getNew(new = "shows", token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<VideoListResultDataBean> {
            override fun onResponse(call: Call<VideoListResultDataBean>, response: Response<VideoListResultDataBean>) {
                val data = response.body()!!.results
                showsList.adapter = this@FragmentHome.showsAdapter
                this@FragmentHome.showsList.addAll(data)
            }
            override fun onFailure(call: Call<VideoListResultDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                this@FragmentHome.swipeLayout.visibility = View.GONE
                this@FragmentHome.errorview.visibility = View.VISIBLE
                Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
            }
        })
        if (this@FragmentHome.swipeLayout.visibility == View.VISIBLE) {
            this@FragmentHome.movieAdapter.notifyDataSetChanged()
            this@FragmentHome.tvAdapter.notifyDataSetChanged()
            this@FragmentHome.showsAdapter.notifyDataSetChanged()
            this@FragmentHome.animeAdapter.notifyDataSetChanged()
        }
    }


    fun onItem(dataList: ArrayList<VideoListResultDataBean.Data>) : AdapterView.OnItemClickListener {
        val onItem = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = dataList.get(index.toInt())
            activityInteraction.isLogin(bean.v_id)
        }
        return onItem
    }


    companion object {
        fun newInstance(): FragmentHome = FragmentHome()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is BaseFFragment.FragmentInteraction) {
            activityInteraction = activity as BaseFFragment.FragmentInteraction
        } else {
            throw IllegalArgumentException("activity must implements FragmentInteraction")
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        xbanner.releaseBanner()
    }


    private fun getAnnouncementTxt() {
        Requester.PublicService().getAnnouncement(package_name=this.context!!.packageName, token = ToolsHelper.getToken(this.context!!)).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                this@FragmentHome.AnnouncementTxt.text = response.body()!!.data.toString()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.d("logoutError",t.message)
            }
        })
    }


}