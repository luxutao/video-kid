package me.m123.video.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

class AutoHeightGridView : GridView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec: Int = View.MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)

        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}