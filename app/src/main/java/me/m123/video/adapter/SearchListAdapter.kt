package me.m123.video.adapter


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import me.m123.video.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import me.m123.video.data.VideoListResultDataBean


class SearchListAdapter(private val _context: Context, private val _list: ArrayList<VideoListResultDataBean.Data>) : BaseAdapter() {

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

            v = View.inflate(_context, R.layout.activity_search_result_item, null)
            holder = ImageViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ImageViewHolder
        }
        var v_pic = _list[position].v_pic
        if ("http" !in v_pic) {
            v_pic = "https://v.123m.me/" + v_pic
        }

        Glide.with(v)
                .load(v_pic)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(RequestOptions().placeholder(R.drawable.video_item_loading_placeholder))
                .apply(RequestOptions().error(R.drawable.video_item_loading_error))
                .apply(RequestOptions().centerCrop())
                .apply(RequestOptions().dontAnimate())
                .into(holder.simage)

        holder.sname.setText(String.format(_context.getString(R.string.format_string),  _list[position].v_name))
        holder.sarea.setText(String.format(_context.getString(R.string.format_string),  _list[position].v_publisharea))
        holder.syear.setText(String.format(_context.getString(R.string.format_string),  _list[position].v_publishyear))
        holder.stype.setText(String.format(_context.getString(R.string.format_string),  _list[position].tid))
        holder.slang.setText(String.format(_context.getString(R.string.format_string),  _list[position].v_lang))
        holder.sstatus.setText(String.format(_context.getString(R.string.format_string),  _list[position].v_note))
        holder.sactor.setText(String.format(_context.getString(R.string.actors),  _list[position].v_actor))
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