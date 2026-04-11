package com.dam.lockgo.data.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AppBlockingService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Aquí irá la lógica para detectar qué app se abre
    }

    override fun onInterrupt() {}
}