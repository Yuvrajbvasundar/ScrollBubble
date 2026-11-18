package com.example.scrollbubble

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import kotlin.math.roundToInt

class FloatingBubble(private val context: Context) {
    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private fun dp(n: Int) = (n * context.resources.displayMetrics.density).roundToInt()
    private val label = TextView(context).apply {
        text = "0"; textSize = 18f; setTextColor(Color.WHITE); gravity = Gravity.CENTER
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.argb(230, 28, 28, 36))
            setStroke(dp(2), Color.rgb(100, 100, 115))
        }
    }
    private val params = WindowManager.LayoutParams(dp(64), dp(64),
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = dp(12); y = dp(180) }
    private var attached = false
    private var downX = 0f; private var downY = 0f
    private var startX = 0; private var startY = 0
    init {
        label.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { downX = event.rawX; downY = event.rawY; startX = params.x; startY = params.y; true }
                MotionEvent.ACTION_MOVE -> {
                    params.x = (startX + event.rawX - downX).roundToInt().coerceAtLeast(0)
                    params.y = (startY + event.rawY - downY).roundToInt().coerceAtLeast(0)
                    if (attached) wm.updateViewLayout(label, params)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> true
                else -> false
            }
        }
    }
    fun show(platform: String, count: Int) {
        if (!Settings.canDrawOverlays(context)) return
        label.text = if (platform == "instagram") "IG\n$count" else "YT\n$count"
        if (!attached) {
            try { wm.addView(label, params); attached = true } catch (_: Exception) { attached = false }
        }
    }
    fun hide() {
        if (attached) { try { wm.removeView(label) } catch (_: Exception) {} ; attached = false }
    }
}
