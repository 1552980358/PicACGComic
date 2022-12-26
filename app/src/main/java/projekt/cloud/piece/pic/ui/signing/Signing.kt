package projekt.cloud.piece.pic.ui.signing

import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSigningBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT

class Signing: BaseFragment<FragmentSigningBinding>(), OnClickListener {

    private lateinit var navController: NavController

    private val signIn: MaterialTextView
        get() = binding.materialTextViewSignIn

    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        when (layoutSizeMode) {
            COMPACT -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            else -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            }
        }
    }

    override fun onSetupView(binding: FragmentSigningBinding) {
        navController = findNavController()
        signIn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            signIn -> {
                navController.navigate(SigningDirections.toSignIn())
            }
        }
    }

}