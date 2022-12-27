package projekt.cloud.piece.pic.ui.browsing

import android.app.Activity
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.databinding.FragmentBrowsingBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

abstract class BrowsingLayoutCompat private constructor(protected val binding: FragmentBrowsingBinding) {

    companion object BrowsingLayoutCompatUtil {
        @JvmStatic
        fun FragmentBrowsingBinding.getLayoutCompat(activity: Activity) = when (activity.getLayoutSize()) {
            COMPACT -> BrowsingLayoutCompatImpl(this)
            MEDIUM -> BrowsingCompatW600dpImpl(this)
            EXPANDED -> BrowsingCompatW1240dpImpl(this)
        }
    }

    protected val toolbar: MaterialToolbar
        get() = binding.materialToolbar!!

    private val diversion: MaterialSwitch
        get() = binding.materialSwitchDiversion
    private val proxy: MaterialSwitch
        get() = binding.materialSwitchProxy

    private val proxyType: TextInputLayout
        get() = binding.textInputLayoutProxyType
    private val proxyTypeField: AutoCompleteTextView
        get() = proxyType.editText as AutoCompleteTextView

    protected lateinit var navController: NavController
        private set

    init {
        @Suppress("LeakingThis")
        binding.layoutCompat = this
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    open fun setupActionBar(fragment: Fragment) = Unit

    open fun onSetupInputs() {
        proxyTypeField.setText(proxyTypeField.adapter.getItem(0).toString(), false)
    }

    fun onGoClicked() {
        navController.navigate(BrowsingDirections.toSigning())
    }

    private class BrowsingLayoutCompatImpl(binding: FragmentBrowsingBinding): BrowsingLayoutCompat(binding) {

        private val diversionSever: TextInputLayout
            get() = binding.textInputLayoutDiversion!!
        private val diversionServerField: MaterialAutoCompleteTextView
            get() = diversionSever.editText as MaterialAutoCompleteTextView

        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                navController.navigateUp()
            }
        }

        override fun onSetupInputs() {
            super.onSetupInputs()
            // diversionServerField.setText(diversionServerField.adapter.getItem(0).toString(), false)
        }

    }

    private open class BrowsingCompatW600dpImpl(binding: FragmentBrowsingBinding): BrowsingLayoutCompat(binding) {

        private val diversionServerB: MaterialRadioButton
            get() = binding.materialRadioButtonDiversionB!!

        override fun onSetupInputs() {
            super.onSetupInputs()
            diversionServerB.isChecked = true
        }

    }

    private class BrowsingCompatW1240dpImpl(binding: FragmentBrowsingBinding): BrowsingCompatW600dpImpl(binding)

}