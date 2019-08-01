package cn.animekid.videokid.ui

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.animekid.videokid.R
import cn.animekid.videokid.adapter.SearchHistoryAdapter
import cn.animekid.videokid.api.Requester
import cn.animekid.videokid.data.HotSearchData
import cn.animekid.videokid.utils.database
import com.donkingliang.labels.LabelsView
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: BaseAAppCompatActivity() {

    private lateinit var searchHot: LabelsView
    private lateinit var searchHistory: ListView
    private lateinit var searchText: EditText
    private lateinit var searchClearHistory: Button
    private var HistoryData: ArrayList<String> = arrayListOf()
    private lateinit var HistoryAdapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        this.getHotData()
        this.getHistoryData()
    }

    fun initUI() {
        this.searchHot = this.findViewById(R.id.search_hot)
        this.searchHot.setOnLabelClickListener(object: LabelsView.OnLabelClickListener {
            override fun onLabelClick(label: TextView?, data: Any?, position: Int) {
                this@SearchActivity.insertHistoryData(data.toString())
                this@SearchActivity.searchText.text = SpannableStringBuilder(data.toString())
                val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
                intent.putExtra(SearchManager.QUERY, data.toString())
                startActivity(intent)
            }
        })
        this.searchHistory = this.findViewById(R.id.search_history)
        this.HistoryAdapter = SearchHistoryAdapter(this, this.HistoryData)
        this.searchHistory.adapter = this.HistoryAdapter
        this.searchHistory.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val data = this@SearchActivity.HistoryData.get(index.toInt())
            this@SearchActivity.insertHistoryData(data)
            this@SearchActivity.searchText.text = SpannableStringBuilder(data)
            val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
            intent.putExtra(SearchManager.QUERY, data)
            startActivity(intent)
        }
        this.searchClearHistory = this.findViewById(R.id.search_clear_history)
        this.searchClearHistory.setOnClickListener {
            this.database.writableDatabase.delete("search_history")
            this.getHistoryData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search_item).actionView as SearchView
        val searchViewIcon = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        this.searchText = searchView.findViewById(R.id.search_src_text)
        val linearLayoutSearchView = searchViewIcon.parent as ViewGroup
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                this@SearchActivity.insertHistoryData(p0.toString())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView.queryHint = "搜索影片名称、主演"
        searchView.setSearchableInfo(searchableInfo)
        searchView.setIconifiedByDefault(false)
        searchView.requestFocus()
        searchView.requestFocusFromTouch()
        linearLayoutSearchView.removeView(searchViewIcon)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_item -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun getToolbarTitle(): Int {
        return R.string.search_menu_title
    }

    fun getHotData() {
        Requester.VideoService().getHotVideo().enqueue(object: Callback<HotSearchData> {
            override fun onResponse(call: Call<HotSearchData>, response: Response<HotSearchData>) {
                val res = response.body()!!
                this@SearchActivity.searchHot.setLabels(res.data)
            }

            override fun onFailure(call: Call<HotSearchData>, t: Throwable) {
                Log.e("getHotError", t.message)
            }
        })
    }

    fun insertHistoryData(v_name: String) {
        this.database.writableDatabase.insert(
                "search_history", "v_name" to v_name
        )
        this.getHistoryData()
    }


    fun getHistoryData() {
        val history = this.database.writableDatabase.select("search_history", "v_name")
                .orderBy("id", SqlOrderDirection.DESC).parseList(org.jetbrains.anko.db.StringParser)
        this.HistoryData.clear()
        this.HistoryData.addAll(history)
        this.HistoryAdapter.notifyDataSetChanged()
    }
}