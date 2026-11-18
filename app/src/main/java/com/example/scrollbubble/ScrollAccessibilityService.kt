package com.example.scrollbubble

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/** Experimental heuristic: requires real-device calibration for each app release. */
class ScrollAccessibilityService : AccessibilityService() {
    private lateinit var store: ScrollStore
    private lateinit var bubble: FloatingBubble
    private var active: String? = null
    private var lastTransition = 0L
    private var lastScroll = 0L

    override fun onServiceConnected() { store = ScrollStore(this); bubble = FloatingBubble(this) }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !::bubble.isInitialized) return
        val pkg = event.packageName?.toString()
        val platform = when (pkg) {
            "com.instagram.android" -> "instagram"
            "com.google.android.youtube" -> "youtube"
            else -> { bubble.hide(); active = null; return }
        }
        val root = rootInActiveWindow ?: run { bubble.hide(); active = null; return }
        val labels = mutableListOf<String>()
        collectLabels(root, labels, 0)
        val text = labels.joinToString(" ").lowercase()
        // Deliberately strict to reduce counting ordinary feed scrolls.
        // UI labels vary by app version and language; inspect and adapt on device.
        val isVideoViewer = if (platform == "instagram") {
            Regex("\\breels?\\b").containsMatchIn(text) &&
                (text.contains("like") || text.contains("comment"))
        } else {
            Regex("\\bshorts\\b").containsMatchIn(text) &&
                (text.contains("like") || text.contains("dislike") || text.contains("comment"))
        }
        if (!isVideoViewer) { bubble.hide(); active = null; return }
        val now = SystemClock.elapsedRealtime()
        if (active != platform) { active = platform; lastTransition = now; lastScroll = 0L }
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // A single gesture may fire many events; count at most once per 900 ms.
            // This is an estimate, not verified video identity.
            if (now - lastTransition >= 900L && now - lastScroll >= 900L) {
                store.increment(platform)
                lastTransition = now
                lastScroll = now
            }
        }
        bubble.show(platform, store.count(platform))
    }

    private fun collectLabels(node: AccessibilityNodeInfo, out: MutableList<String>, depth: Int) {
        if (depth > 12 || out.size > 150) return
        node.text?.toString()?.let { out.add(it) }
        node.contentDescription?.toString()?.let { out.add(it) }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectLabels(child, out, depth + 1)
        }
    }
    override fun onInterrupt() { if (::bubble.isInitialized) bubble.hide() }
    override fun onDestroy() { if (::bubble.isInitialized) bubble.hide(); super.onDestroy() }
}
