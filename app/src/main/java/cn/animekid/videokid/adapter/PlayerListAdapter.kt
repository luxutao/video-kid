package cn.animekid.videokid.adapter


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import cn.animekid.videokid.R
import java.util.*
import android.widget.*
import cn.animekid.videokid.data.Playdata


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

            v = View.inflate(_context, R.layout.player_item, null)
            holder = PlayerListHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as PlayerListHolder
        }

        holder.palyerlist.text = String.format(_context.getString(R.string.score),  _list[position].gather)
        return v
    }


    class PlayerListHolder(viewItem: View) {
        var palyerlist: Button = viewItem.findViewById(R.id.gatherItem) as Button

    }
}