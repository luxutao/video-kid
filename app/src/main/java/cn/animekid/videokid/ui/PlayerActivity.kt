package cn.animekid.videokid.ui


import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.PlayerListAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.DetailBean
import cn.animekid.videokid.data.Playdata
import cn.animekid.videokid.utils.AutoHeightGridView
import cn.animekid.videokid.utils.LoadingDialog
import cn.animekid.videokid.utils.MarqueeText
import cn.animekid.videokid.utils.PrettyImageView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private lateinit var v_digg: TextView
    private lateinit var v_lang: TextView
    private lateinit var v_desc: TextView
    private lateinit var v_actor: LinearLayout
    private var player1list: ArrayList<Playdata> = arrayListOf()
    private var player2list: ArrayList<Playdata> = arrayListOf()
    private var player3list: ArrayList<Playdata> = arrayListOf()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val v_id = intent.extras.getInt("v_id")
        loading = LoadingDialog(this).showLoading()
        this.initUI()
        this.getDetail(v_id)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_player
    }
    
    fun initUI() {
        videoView = findViewById(R.id.video_view)
        v_name = this.findViewById(R.id.v_name)
        v_score_star = this.findViewById(R.id.v_score_star)
        v_score = this.findViewById(R.id.v_score)
        v_year = this.findViewById(R.id.v_year)
        v_area = this.findViewById(R.id.v_area)
        v_tname = this.findViewById(R.id.v_tname)
        v_digg = this.findViewById(R.id.v_digg)
        v_lang = this.findViewById(R.id.v_lang)
        v_desc = this.findViewById(R.id.v_desc)
        v_actor = this.findViewById(R.id.horizontalScrollViewItemContainer)
        player1_player_layout = this.findViewById(R.id.player1_player_layout)
        player1_image_button = this.findViewById(R.id.player1_image_button)
        player2_player_layout = this.findViewById(R.id.player2_player_layout)
        player2_image_button = this.findViewById(R.id.player2_image_button)
        player3_player_layout = this.findViewById(R.id.player3_player_layout)
        player3_image_button = this.findViewById(R.id.player3_image_button)

    }

    fun getDetail(v_id: Int) {
        Requester.ImageService().getDetail(vid = v_id).enqueue(object: Callback<DetailBean> {
            override fun onResponse(call: Call<DetailBean>, response: Response<DetailBean>) {
                val res = response.body()!!.data
                v_name.text = res.v_name
                v_year.text = res.v_publishyear.toString()
                v_area.text = res.v_publisharea
                v_tname.text = res.tid.tname
                v_digg.text = String.format(this@PlayerActivity.getString(R.string.player_hit), res.v_hit)
                v_score.text = res.score
                v_desc.text = res.v_content.body
                v_score_star.rating = (res.score.toFloat() * 0.5).toFloat()
                this@PlayerActivity.player1list.addAll(res.v_playdata.body[0].playdata)
                this@PlayerActivity.player2list.addAll(res.v_playdata.body[1].playdata)
                this@PlayerActivity.player3list.addAll(res.v_playdata.body[2].playdata)
                v_lang.text = res.v_lang
                for (actor in res.v_actor) {
                    val newLyout = this@PlayerActivity.layoutInflater.inflate(R.layout.detail_actor_item, v_actor, false)
                    val actor_name = newLyout.findViewById<MarqueeText>(R.id.actor_name)
                    val actor_avatar = newLyout.findViewById<PrettyImageView>(R.id.actor_avatar)
                    Glide.with(this@PlayerActivity)
                        .load(actor.avatar)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading))
                        .apply(RequestOptions.errorOf(R.drawable.ic_image_loading_error))
                        .into(actor_avatar)
                    actor_name.text = actor.name
                    v_actor.addView(newLyout)
                    v_actor.invalidate()
                }
                this@PlayerActivity.makePlayerList()
                videoView.setUp(this@PlayerActivity.player1list[0].url,  res.v_name)
                videoView.startVideo()
                this@PlayerActivity.loading.dismiss()
            }
            override fun onFailure(call: Call<DetailBean>, t: Throwable) {
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

    fun onItem(playerlist: ArrayList<Playdata>): AdapterView.OnItemClickListener {
        val onItem = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = playerlist.get(index.toInt())
            if (videoView.state == Jzvd.STATE_ERROR) {
                videoView.setUp(bean.url, bean.gather)
            } else {
                videoView.changeUrl(bean.url, bean.gather,0)
            }
            videoView.startVideo()
        }
        return onItem
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
