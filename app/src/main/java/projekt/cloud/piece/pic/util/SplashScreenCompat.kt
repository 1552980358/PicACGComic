package projekt.cloud.piece.pic.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

abstract class SplashScreenCompat private constructor() {
    
    companion object {
        @JvmStatic
        val Activity.applySplashScreenCompat: SplashScreenCompat
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> SplashScreenCompatApi31Impl(this)
                else -> SplashScreenCompatImpl(this)
            }
    }
    
    protected var keepOnScreen = false
        private set
    
    fun setKeepOnScreen(boolean: Boolean) {
        keepOnScreen = boolean
    }
    
    private class SplashScreenCompatImpl(activity: Activity): SplashScreenCompat() {
        
        private val splashScreen = activity.installSplashScreen()
        
        init {
            splashScreen.setKeepOnScreenCondition { keepOnScreen }
        }
        
    }
    
    @RequiresApi(Build.VERSION_CODES.S)
    private class SplashScreenCompatApi31Impl(activity: Activity): SplashScreenCompat() {
        
        init {
            val contentView = activity.findViewById<View>(android.R.id.content)
            val listener = object: ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (!keepOnScreen) {
                        contentView.viewTreeObserver.removeOnPreDrawListener(this)
                    }
                    return !keepOnScreen
                }
            }
            contentView.viewTreeObserver.addOnPreDrawListener(listener)
        }
        
    }

}