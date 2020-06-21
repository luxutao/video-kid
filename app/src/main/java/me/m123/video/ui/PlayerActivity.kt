package me.m123.video.ui


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import me.m123.video.R
import me.m123.video.adapter.PlayerListAdapter
import me.m123.video.api.Requester
import me.m123.video.utils.*
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import me.m123.video.data.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayerActivity : BaseAAppCompatActivity() {

    private lateinit var videoView: JzvdStd
    private lateinit var v_name: TextView
    private lateinit var v_score_star: RatingBar
    private lateinit var v_score: TextView
    private lateinit var v_year: TextView
    private lateinit var v_area: TextView
    private lateinit var v_tname: TextView
    private lateinit var v_hit: TextView
    private lateinit var v_lang: TextView
    private lateinit var v_desc: TextView
    private lateinit var v_actor: LinearLayout
    private var player1list: ArrayList<DetailDataBean.VPlaydata.Playdata> = arrayListOf()
    private var player2list: ArrayList<DetailDataBean.VPlaydata.Playdata> = arrayListOf()
    private var player3list: ArrayList<DetailDataBean.VPlaydata.Playdata> = arrayListOf()
    private lateinit var player1Adapter: PlayerListAdapter
    private lateinit var player2Adapter: PlayerListAdapter
    private lateinit var player3Adapter: PlayerListAdapter
    private lateinit var loading: AlertDialog
    private lateinit var player1_player_layout: LinearLayout
    private lateinit var player2_player_layout: LinearLayout
    private lateinit var player3_player_layout: LinearLayout
    private lateinit var player1_image_button: ImageButton
    private lateinit var player2_image_button: ImageButton
    private lateinit var player3_image_button: ImageButton
    private var player1_status: Boolean = false
    private var player2_status: Boolean = true
    private var player3_status: Boolean = false
    private lateinit var v_digg_image: ImageView
    private lateinit var v_digg_text: TextView
    private lateinit var v_tread_image: ImageView
    private lateinit var v_tread_text: TextView
    private lateinit var v_collect_image: ImageView
    private lateinit var v_collect_text: TextView
    private lateinit var v_question: ImageView
    private var v_id: Int = 1
    private var isCollect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.v_id = intent.extras.getInt("v_id")
        this.loading = LoadingDialog(this).showLoading()
        this.initUI()
        this.getDetail()
        this.setListener()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_player
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_player
    }

    fun setListener() {
        this.v_digg_image.setOnClickListener {
            this.v_digg_image.setImageDrawable(getDrawable(R.drawable.ic_digg_solid))
            this.v_digg_text.text = String.format(this.getString(R.string.format_string), this.v_digg_text.text.toString().toInt() + 1)
            this.diggTread("digg", "感谢你的点赞")
        }
        this.v_tread_image.setOnClickListener {
            this.v_tread_image.setImageDrawable(getDrawable(R.drawable.ic_tread_solid))
            this.v_tread_text.text = String.format(this.getString(R.string.format_string), this.v_tread_text.text.toString().toInt() + 1)
            this.diggTread("tread", "(⊙o⊙)哦，要努力了")
        }
        this.v_question.setOnClickListener {
            Requester.VideoService().feedbackNotPlay(v_id, ToolsHelper.getToken(this)).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    Toast.makeText(this@PlayerActivity, "感谢您的反馈，我们会尽快解决。", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("error", t.message)
                }
            })
        }
        this.v_collect_image.setOnClickListener {
            var v_type = ""
            var message = ""
            if (this.isCollect) {
                this.isCollect = false
                v_type = "false"
                message = "(⊙o⊙)哦，取消收藏了"
                this.v_collect_image.setImageDrawable(getDrawable(R.drawable.ic_collect))
                this.v_collect_text.text = String.format(this.getString(R.string.format_string), this.v_collect_text.text.toString().toInt() - 1)
            } else {
                this.isCollect = true
                v_type = "true"
                message = "收藏成功咯"
                this.v_collect_image.setImageDrawable(getDrawable(R.drawable.ic_collect_solid))
                this.v_collect_text.text = String.format(this.getString(R.string.format_string), this.v_collect_text.text.toString().toInt() + 1)
            }
            Requester.CollectService().collectVideo(this.UserInfo.id, this.v_id, ToolsHelper.getToken(this)).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    Toast.makeText(this@PlayerActivity, message, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("error", t.message)
                }
            })
        }
    }
    
    fun initUI() {
        this.videoView = this.findViewById(R.id.video_view)
        this.v_name = this.findViewById(R.id.v_name)
        this.v_score_star = this.findViewById(R.id.v_score_star)
        this.v_score = this.findViewById(R.id.v_score)
        this.v_year = this.findViewById(R.id.v_year)
        this.v_area = this.findViewById(R.id.v_area)
        this.v_tname = this.findViewById(R.id.v_tname)
        this.v_hit = this.findViewById(R.id.v_hit)
        this.v_lang = this.findViewById(R.id.v_lang)
        this.v_desc = this.findViewById(R.id.v_desc)
        this.v_actor = this.findViewById(R.id.horizontalScrollViewItemContainer)
        this.player1_player_layout = this.findViewById(R.id.player1_player_layout)
        this.player1_image_button = this.findViewById(R.id.player1_image_button)
        this.player2_player_layout = this.findViewById(R.id.player2_player_layout)
        this.player2_image_button = this.findViewById(R.id.player2_image_button)
        this.player3_player_layout = this.findViewById(R.id.player3_player_layout)
        this.player3_image_button = this.findViewById(R.id.player3_image_button)
        this.v_digg_image = this.findViewById(R.id.v_digg_image)
        this.v_digg_text = this.findViewById(R.id.v_digg_text)
        this.v_tread_image = this.findViewById(R.id.v_tread_image)
        this.v_tread_text = this.findViewById(R.id.v_tread_text)
        this.v_collect_image = this.findViewById(R.id.v_collect_image)
        this.v_collect_text = this.findViewById(R.id.v_collect_text)
        this.v_question = this.findViewById(R.id.v_question_image)

    }

    fun getDetail() {

        Requester.VideoService().getDetail(videoid = this.v_id, token = ToolsHelper.getToken(this)).enqueue(object: Callback<DetailDataBean> {
            override fun onResponse(call: Call<DetailDataBean>, response: Response<DetailDataBean>) {
                val res = response.body()!!
                this@PlayerActivity.isCollect = res.is_collect.toBoolean()
                this@PlayerActivity.v_collect_image.setImageResource(R.drawable.ic_collect_solid)
                this@PlayerActivity.v_name.text = res.v_name
                this@PlayerActivity.v_year.text = res.v_publishyear.toString()
                this@PlayerActivity.v_area.text = res.v_publisharea
                this@PlayerActivity.v_tname.text = res.tid.tname
                this@PlayerActivity.v_hit.text = String.format(this@PlayerActivity.getString(R.string.player_hit), res.v_hit)
                this@PlayerActivity.v_digg_text.text = String.format(this@PlayerActivity.getString(R.string.format_string), res.v_digg)
                this@PlayerActivity.v_tread_text.text = String.format(this@PlayerActivity.getString(R.string.format_string), res.v_tread)
                this@PlayerActivity.v_collect_text.text = String.format(this@PlayerActivity.getString(R.string.format_string), 10)
                this@PlayerActivity.v_score.text = res.score
                this@PlayerActivity.v_desc.text = res.v_content
                this@PlayerActivity.v_score_star.rating = (res.score.toFloat() * 0.5).toFloat()
                this@PlayerActivity.player1list.addAll(res.v_playdata[0].playdata)
                this@PlayerActivity.player2list.addAll(res.v_playdata[1].playdata)
                this@PlayerActivity.player3list.addAll(res.v_playdata[2].playdata)
                this@PlayerActivity.v_lang.text = res.v_lang
                for (actor in res.v_actor) {
                    val newLyout = this@PlayerActivity.layoutInflater.inflate(R.layout.detail_actor_item, v_actor, false)
                    val actor_name = newLyout.findViewById<MarqueeText>(R.id.actor_name)
                    val actor_avatar = newLyout.findViewById<PrettyImageView>(R.id.actor_avatar)
                    Glide.with(this@PlayerActivity)
                            .load(actor.avatar)
                            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .apply(RequestOptions.placeholderOf(R.drawable.video_item_loading_placeholder))
                            .apply(RequestOptions.errorOf(R.drawable.video_item_loading_error))
                            .apply(RequestOptions().centerCrop())
                            .apply(RequestOptions().dontAnimate())
                            .into(actor_avatar)
                    actor_name.text = actor.name
                    this@PlayerActivity.v_actor.addView(newLyout)
                    this@PlayerActivity.v_actor.invalidate()
                }
                this@PlayerActivity.makePlayerList()
                this@PlayerActivity.videoView.setUp(this@PlayerActivity.player1list[0].url,  this@PlayerActivity.player1list[0].gather)
                this@PlayerActivity.loading.dismiss()
            }
            override fun onFailure(call: Call<DetailDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                Toast.makeText(this@PlayerActivity, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                this@PlayerActivity.finish()
            }
        })
    }

    fun makePlayerList() {
        this.player1Adapter = PlayerListAdapter(this, this.player1list)
        this.player2Adapter = PlayerListAdapter(this, this.player2list)
        this.player3Adapter = PlayerListAdapter(this, this.player3list)
        val player1Grid = this.findViewById<AutoHeightGridView>(R.id.player1_palyer_list)
        val player2Grid = this.findViewById<AutoHeightGridView>(R.id.player2_palyer_list)
        val player3Grid = this.findViewById<AutoHeightGridView>(R.id.player3_palyer_list)
        player1Grid.adapter = this.player1Adapter
        player2Grid.adapter = this.player2Adapter
        player3Grid.adapter = this.player3Adapter
        player1Grid.onItemClickListener = this.onItem(this.player1list)
        player2Grid.onItemClickListener = this.onItem(this.player2list)
        player3Grid.onItemClickListener = this.onItem(this.player3list)
        this.player1Adapter.notifyDataSetChanged()
        this.player2Adapter.notifyDataSetChanged()
        this.player3Adapter.notifyDataSetChanged()

        player1_player_layout.setOnClickListener {
            if (this.player1_status) {
                this.player1_image_button.background = getDrawable(R.drawable.ic_right)
                player1Grid.visibility = View.GONE
                this.player1_status = false
            } else {
                this.player1_image_button.background = getDrawable(R.drawable.ic_down)
                player1Grid.visibility = View.VISIBLE
                this.player1_status = true
            }
        }
        
        player2_player_layout.setOnClickListener {
            if (this.player2_status) {
                this.player2_image_button.background = getDrawable(R.drawable.ic_right)
                player2Grid.visibility = View.GONE
                this.player2_status = false
            } else {
                this.player2_image_button.background = getDrawable(R.drawable.ic_down)
                player2Grid.visibility = View.VISIBLE
                this.player2_status =true
            }
        }

        player3_player_layout.setOnClickListener {
            if (this.player3_status) {
                this.player3_image_button.background = getDrawable(R.drawable.ic_right)
                player3Grid.visibility = View.GONE
                this.player3_status = false
            } else {
                this.player3_image_button.background = getDrawable(R.drawable.ic_down)
                player3Grid.visibility = View.VISIBLE
                this.player3_status = true
            }
        }
    }

    fun onItem(playerlist: ArrayList<DetailDataBean.VPlaydata.Playdata>): AdapterView.OnItemClickListener {
        val onItem = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = playerlist.get(index.toInt())
            if (this.videoView.state == Jzvd.STATE_ERROR || this.videoView.state == Jzvd.STATE_NORMAL) {
                this.videoView.setUp(bean.url, bean.gather)
            } else {
                this.videoView.changeUrl(bean.url, bean.gather,0)
            }
            this.videoView.startVideo()
        }
        return onItem
    }

    fun diggTread(action: String, message: String) {
        this.v_digg_image.isEnabled = false
        this.v_tread_image.isEnabled = false
        var json = JSONObject()
        json.put("action", action)
        Requester.VideoService().diggTread(this.v_id, RequestBody.create(this.JSON, json.toString()), ToolsHelper.getToken(this)).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Toast.makeText(this@PlayerActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("error", t.message)
            }
        })
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.resetAllVideos()
    }

}
