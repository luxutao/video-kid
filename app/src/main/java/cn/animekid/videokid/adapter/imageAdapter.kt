package cn.animekid.videokid.adapter


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.animekid.videokid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import java.util.*
import android.R.attr.level
import android.app.Activity
import android.opengl.ETC1.getHeight
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.GridView
import cn.animekid.videokid.data.ImageData


class ImageAdapter(private val _context: Context, private val _list: ArrayList<ImageData>) : BaseAdapter() {

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

            v = View.inflate(_context, R.layout.image_item, null)
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
            .into(holder.image)
        holder.score.setText(String.format(_context.getString(R.string.score),  _list[position].v_score))
        holder.name.setText(String.format(_context.getString(R.string.score),  _list[position].v_name))
        holder.note.setText(String.format(_context.getString(R.string.score),  _list[position].v_note))
        return v
    }


    class ImageViewHolder(viewItem: View) {
        var image: ImageView = viewItem.findViewById(R.id.imageItem) as ImageView
        var score: TextView = viewItem.findViewById(R.id.scoreItem) as TextView
        var name: TextView = viewItem.findViewById(R.id.nameItem) as TextView
        var note: TextView = viewItem.findViewById(R.id.noteItem) as TextView

    }
}