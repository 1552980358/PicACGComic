package projekt.cloud.piece.pic.ui.account

import android.os.Bundle
import androidx.fragment.app.commit
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentAccountBinding
import projekt.cloud.piece.pic.ui.account.detail.AccountDetailFragment
import projekt.cloud.piece.pic.ui.account.login.AccountLoginFragment

class AccountFragment: BaseFragment<FragmentAccountBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override fun setUpViews() {
        applicationConfigs.account.observe(viewLifecycleOwner) {
    
            val newFragment = when (it?.token) {
                null -> AccountLoginFragment()
                else -> AccountDetailFragment()
            }
    
            childFragmentManager.findFragmentById(R.id.fragment_container_view)?.let { currentFragment ->
                newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                currentFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            }
            
            childFragmentManager.commit {
                replace(
                    R.id.fragment_container_view,
                    newFragment
                )
            }
        }
    }
    
}