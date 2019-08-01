package cn.animekid.videokid.adapter


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.animekid.videokid.R
import cn.animekid.videokid.data.Data
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class SearchHistoryAdapter(private val _context: Context, private val _list: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return _list.size
    }

    override fun getItem(position: Int): Any {
        return _list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: HistoryHolder
        val v: View
        if (convertView == null) {

            v = View.inflate(_context, R.layout.activity_search_history_item, null)
            holder = HistoryHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as HistoryHolder
        }

        holder.history_text.text = _list[position]
        return v
    }


    class HistoryHolder(viewItem: View) {
        var history_text: TextView = viewItem.findViewById(R.id.search_history_text)
    }
}