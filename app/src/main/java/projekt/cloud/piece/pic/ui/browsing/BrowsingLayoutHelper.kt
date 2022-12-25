package projekt.cloud.piece.pic.ui.browsing

import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import projekt.cloud.piece.pic.databinding.FragmentBrowsingBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode

object BrowsingLayoutHelper {

    abstract class BrowsingLayoutCompat(protected val binding: FragmentBrowsingBinding) {

        protected val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!

        private val diversion: MaterialSwitch
            get() = binding.materialSwitchDiversion
        private val proxy: MaterialSwitch
            get() = binding.materialSwitchProxy
        private val go: MaterialButton
            get() = binding.materialButtonGo

        private val proxyType: TextInputLayout
            get() = binding.textInputLayoutProxyType
        private val proxyTypeField: AutoCompleteTextView
            get() = proxyType.editText as AutoCompleteTextView

        open fun setupActionBar(fragment: Fragment, navController: NavController) = Unit

        open fun onSetupInputs() {
            proxyTypeField.setText(proxyTypeField.adapter.getItem(0).toString(), false)
        }

    }

    private class BrowsingLayoutCompatImpl(binding: FragmentBrowsingBinding): BrowsingLayoutCompat(binding) {

        private val diversionSever: TextInputLayout
            get() = binding.textInputLayoutDiversion!!
        private val diversionServerField: MaterialAutoCompleteTextView
            get() = diversionSever.editText as MaterialAutoCompleteTextView

        override fun setupActionBar(fragment: Fragment, navController: NavController) {
            fragment.setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController)
        }

        override fun onSetupInputs() {
            super.onSetupInputs()
            diversionServerField.setText(diversionServerField.adapter.getItem(0).toString(), false)
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

    fun FragmentBrowsingBinding.getLayoutHelper(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
        LayoutSizeMode.COMPACT -> BrowsingLayoutCompatImpl(this)
        LayoutSizeMode.MEDIUM -> BrowsingCompatW600dpImpl(this)
        LayoutSizeMode.EXPANDED -> BrowsingCompatW1240dpImpl(this)
    }

}