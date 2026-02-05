package com.example.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import com.example.app.MainActivity

class AppBlockAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Apps to block
        val blockedApps = listOf(
            "com.instagram.android",
            "com.facebook.katana",
            "com.google.android.youtube"
        )

        // TODO: Replace with real task-completion state (DataStore / ViewModel)
        val tasksCompleted = false

        if (!tasksCompleted && blockedApps.contains(packageName)) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onInterrupt() {
        // Required override
    }
}
