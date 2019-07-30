package cn.animekid.videokid.adapter


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import cn.animekid.videokid.R
import cn.animekid.videokid.data.Playdata
import java.util.*


class PlayerListAdapter(private val _context: Context, private val _list: ArrayList<Playdata>) : BaseAdapter() {

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
        val holder: PlayerListHolder
        val v: View
        if (convertView == null) {

            v = View.inflate(_context, R.layout.activity_player_gather_item, null)
            holder = PlayerListHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as PlayerListHolder
        }

        holder.palyerlist.text = String.format(_context.getString(R.string.format_string),  _list[position].gather)
        return v
    }


    class PlayerListHolder(viewItem: View) {
        var palyerlist: Button = viewItem.findViewById(R.id.gatherItem) as Button

    }
}