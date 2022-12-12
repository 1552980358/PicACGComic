package projekt.cloud.piece.pic.util

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.view.View

object AnimationUtil {
    
    private const val ANIMATION_DURATION_LONG = 400L
    
    fun View.animateAlphaTo(newAlpha: Float) {
        if (animation == null) {
            if (alpha != newAlpha) {
                animate().alpha(newAlpha)
                    .setDuration(ANIMATION_DURATION_LONG)
                    .setListener(object: AnimatorListener {
                        override fun onAnimationStart(animation: Animator) = Unit
                        override fun onAnimationEnd(animation: Animator) {
                            this@animateAlphaTo.animation = null
                        }
                        override fun onAnimationCancel(animation: Animator) = Unit
                        override fun onAnimationRepeat(animation: Animator) = Unit
                    })
                    .start()
            }
        }
    }
    
}