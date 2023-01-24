package projekt.cloud.piece.pic.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import projekt.cloud.piece.pic.util.ContextUtil.defaultSharedPreference

object FragmentUtil {

    fun Fragment.setSupportActionBar(toolbar: Toolbar) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }
    
    val Fragment.defaultSharedPreference: SharedPreferences
        get() = requireContext().defaultSharedPreference
    
    inline fun <reified F: Fragment> Fragment.findParentAs(): F {
        var parent = requireParentFragment()
        while (parent !is F) {
            parent = parent.requireParentFragment()
        }
        return parent
    }

}