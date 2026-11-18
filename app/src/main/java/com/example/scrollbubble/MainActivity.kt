package com.example.scrollbubble

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var summary: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(40, 70, 40, 30)
            setBackgroundColor(Color.rgb(247, 248, 251)); gravity = Gravity.TOP
        }
        fun text(value: String, size: Float): TextView = TextView(this).apply {
            this.text = value; textSize = size; setTextColor(Color.rgb(30, 30, 40)); setPadding(0, 12, 0, 12)
        }
        layout.addView(text("ScrollBubble", 28f))
        layout.addView(text("Daily estimated Reels and Shorts transitions", 16f))
        summary = text("", 22f); layout.addView(summary)
        layout.addView(Button(this).apply {
            text = "1. Allow floating bubble"
            setOnClickListener { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))) }
        })
        layout.addView(Button(this).apply {
            text = "2. Enable accessibility service"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        layout.addView(Button(this).apply { text = "Refresh counts"; setOnClickListener { refresh() } })
        layout.addView(text("Privacy: screen labels are processed on your device only. No internet access is requested. Enable permissions only if you trust this app. Counts are experimental and may miss or double-count videos.", 14f))
        setContentView(layout)
    }
    override fun onResume() { super.onResume(); refresh() }
    private fun refresh() {
        val store = ScrollStore(this)
        summary.text = "Instagram Reels: ${store.count("instagram")}\nYouTube Shorts: ${store.count("youtube")}" 
    }
}
