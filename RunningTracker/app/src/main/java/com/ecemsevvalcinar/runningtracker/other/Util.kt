package com.ecemsevvalcinar.runningtracker.other

import android.graphics.Bitmap
import android.icu.util.Calendar
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Locale

@BindingAdapter("android:loadImg")
fun loadImg(view: ImageView, img: Bitmap?) {
    Glide.with(view.context).load(img).into(view)
}

@BindingAdapter("android:textDateInFormatted")
fun textDateInFormatted(view: MaterialTextView, timestamp: Long?) {
    val calendar = Calendar.getInstance().apply {
        if (timestamp != null) {
            timeInMillis = timestamp
        }
    }
    val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    view.text = dateFormat.format(calendar.time)
}

@BindingAdapter("android:textTimeInFormatted")
fun textTimeInFormatted(view: MaterialTextView, timeInMillis: Long?) {
    if (timeInMillis != null) {
        view.text = TrackingUtility.getFormattedStopWatchTime(timeInMillis)
    }
}
