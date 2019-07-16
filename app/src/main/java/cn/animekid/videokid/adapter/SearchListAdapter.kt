package cn.animekid.videokid.adapter


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.animekid.videokid.R
import cn.animekid.videokid.data.Data
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions


class SearchListAdapter(private val _context: Context, private val _list: ArrayList<Data>) : BaseAdapter() {

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
        val holder: ImageViewHolder
        val v: View
        if (convertView == null) {

            v = View.inflate(_context, R.layout.search_item, null)
            holder = ImageViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ImageViewHolder
        }

        Log.e("tag",_list[position].v_pic)
        Glide.with(v)
                .load(_list[position].v_pic)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading))
                .apply(RequestOptions.errorOf(R.drawable.ic_image_loading_error))
                .transition(withCrossFade())
                .into(holder.simage)

        holder.sname.setText(String.format(_context.getString(R.string.score),  _list[position].v_name))
        holder.sarea.setText(String.format(_context.getString(R.string.score),  _list[position].v_publisharea))
        holder.syear.setText(String.format(_context.getString(R.string.score),  _list[position].v_publishyear))
        holder.stype.setText(String.format(_context.getString(R.string.score),  _list[position].tid.tname))
        holder.slang.setText(String.format(_context.getString(R.string.score),  _list[position].v_lang))
        holder.sstatus.setText(String.format(_context.getString(R.string.score),  _list[position].v_note))
        holder.sactor.setText(String.format(_context.getString(R.string.actors),  _list[position].v_actors))
        holder.sdirector.setText(String.format(_context.getString(R.string.directors),  _list[position].v_director))
        return v
    }



    class ImageViewHolder(viewItem: View) {
        var simage: ImageView = viewItem.findViewById(R.id.search_pic) as ImageView
        var sname: TextView = viewItem.findViewById(R.id.search_name) as TextView
        var sarea: TextView = viewItem.findViewById(R.id.search_area) as TextView
        var slang: TextView = viewItem.findViewById(R.id.search_lang) as TextView
        var syear: TextView = viewItem.findViewById(R.id.search_year) as TextView
        var stype: TextView = viewItem.findViewById(R.id.search_type) as TextView
        var sstatus: TextView = viewItem.findViewById(R.id.search_status) as TextView
        var sactor: TextView = viewItem.findViewById(R.id.search_actor) as TextView
        var sdirector: TextView = viewItem.findViewById(R.id.search_director) as TextView

    }
}