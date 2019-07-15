package cn.animekid.videokid.utils

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageView
import cn.animekid.videokid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class LoadingDialog(var mContext: Context) : AlertDialog(mContext) {

    fun showLoading(): AlertDialog {

        val dialog = AlertDialog.Builder(mContext).create()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutInflater = LayoutInflater.from(mContext)
        val dialog_layout = layoutInflater.inflate(R.layout.loading_dialog, null)
        val loadingView = dialog_layout.findViewById<ImageView>(R.id.loading_image)

        Glide.with(mContext)
            .asGif()
            .load(R.drawable.loading)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(loadingView)
        dialog.setView(dialog_layout)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

}