package com.example.testing4.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.testing4.R

object ViewUtils {

    fun setIconColor(imageView: ImageView, @ColorRes colorRes: Int, context: Context) {
        val drawable = imageView.drawable?.mutate()
        drawable?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(context, colorRes))
            imageView.setImageDrawable(it)
        }
    }

    fun setTextColor(textView: TextView, @ColorRes colorRes: Int, context: Context) {
        textView.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    fun setBackground(
        viewMap: Map<Int, Pair<ImageView, TextView>>,
        context: Context,
        selectedId: Int
    ) {
        viewMap.forEach { (id, pair) ->
            val parentView = pair.second.parent as? View ?: return@forEach
            val isSelected = id == selectedId
            if (isSelected) parentView.setBackgroundResource(R.drawable.selected_bacckground)
            else parentView.background = null
        }
    }
}