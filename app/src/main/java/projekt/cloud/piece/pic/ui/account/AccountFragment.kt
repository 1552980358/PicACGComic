package projekt.cloud.piece.pic.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.google.android.material.transition.platform.MaterialContainerTransform
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
    
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountBinding.inflate(inflater, container, false)
    
    override fun setUpViews() {
        childFragmentManager.commit {
            replace(
                R.id.fragment_container_view,
                when (applicationConfigs.token.value) {
                    null -> AccountLoginFragment()
                    else -> AccountDetailFragment()
                }
            )
        }
    }
    
    override fun setUpContainerTransitionName(): String? = null
    
}