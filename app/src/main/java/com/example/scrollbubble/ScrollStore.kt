package com.example.scrollbubble

import android.content.Context
import java.time.LocalDate

class ScrollStore(context: Context) {
    private val prefs = context.getSharedPreferences("counts", Context.MODE_PRIVATE)
    @Synchronized private fun rollover() {
        val day = LocalDate.now().toString()
        if (prefs.getString("day", null) != day) prefs.edit().putString("day", day)
            .putInt("instagram", 0).putInt("youtube", 0).apply()
    }
    @Synchronized fun count(platform: String): Int { rollover(); return prefs.getInt(platform, 0) }
    @Synchronized fun increment(platform: String): Int {
        rollover()
        val next = prefs.getInt(platform, 0) + 1
        prefs.edit().putInt(platform, next).apply()
        return next
    }
}
