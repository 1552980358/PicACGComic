package projekt.cloud.piece.pic.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.State.STARTED

object FragmentUtil {

    @Suppress("UNCHECKED_CAST")
    fun <A: FragmentActivity> Fragment.activityAs() = requireActivity() as A

    fun Fragment.setSupportActionBar(toolbar: Toolbar) =
        activityAs<AppCompatActivity>().setSupportActionBar(toolbar)
    
    fun Fragment.addMenuProvider(menuProvider: MenuProvider) =
        requireActivity().addMenuProvider(menuProvider, this, STARTED)

}