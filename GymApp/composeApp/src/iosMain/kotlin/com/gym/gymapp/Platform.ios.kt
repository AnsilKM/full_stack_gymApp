package com.gym.gymapp

import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.Foundation.timeIntervalSince1970

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val baseUrl: String = "https://full-stack-gymapp.onrender.com"
    override val cacheDir: String? get() = platform.Foundation.NSSearchPathForDirectoriesInDomains(
        platform.Foundation.NSCachesDirectory,
        platform.Foundation.NSUserDomainMask,
        true
    ).firstOrNull() as? String
    override fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
    override fun exitApp() { /* Not used on iOS */ }
    
    override fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()