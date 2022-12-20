package projekt.cloud.piece.pic.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashScreenCompat private constructor(activity: Activity) {
    
    companion object SplashScreenCompatUtil {
        @JvmStatic
        fun Activity.applySplashScreen(): SplashScreenCompat {
            return SplashScreenCompat(this).apply { install() }
        }
    }
    
    private abstract class SplashScreen(protected val activity: Activity) {
    
        protected var isKeepOnScreen = true
            private set
        
        abstract fun install()
        
        fun setKeepOnScreenCondition(boolean: Boolean) {
            isKeepOnScreen = boolean
        }
        
    }
    
    private class SplashScreenImpl(activity: Activity): SplashScreen(activity) {
        private lateinit var splashScreen: androidx.core.splashscreen.SplashScreen
    
        override fun install() {
            splashScreen = activity.installSplashScreen()
            splashScreen.setKeepOnScreenCondition { isKeepOnScreen }
        }
    
    }
    
    @RequiresApi(Build.VERSION_CODES.S)
    private class SplashScreenApi31Impl(activity: Activity): SplashScreen(activity) {
        
        private val viewTreeObserver: ViewTreeObserver
            get() = activity.findViewById<View>(android.R.id.content).viewTreeObserver
        
        private lateinit var splashScreen: android.window.SplashScreen
        
        override fun install() {
            splashScreen = activity.splashScreen
    
            viewTreeObserver.addOnPreDrawListener(
                object: OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        if (isKeepOnScreen) {
                            viewTreeObserver.removeOnPreDrawListener(this)
                        }
                        return isKeepOnScreen
                    }
                }
            )
        }
        
    }
    
    private val splashScreen = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> SplashScreenApi31Impl(activity)
        else -> SplashScreenImpl(activity)
    }
    
    fun install() {
        splashScreen.install()
    }
    
    fun setKeepOnScreenCondition(boolean: Boolean) {
        splashScreen.setKeepOnScreenCondition(boolean)
    }
    
}