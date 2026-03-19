package com.gym.gymapp

import platform.UIKit.UIDevice
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val baseUrl: String = "http://localhost:3000"
    override fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
    override fun exitApp() { /* Not used on iOS */ }
}

actual fun getPlatform(): Platform = IOSPlatform()