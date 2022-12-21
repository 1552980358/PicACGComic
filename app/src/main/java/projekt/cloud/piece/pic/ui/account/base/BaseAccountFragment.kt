package projekt.cloud.piece.pic.ui.account.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseAuthFragment
import projekt.cloud.piece.pic.ui.account.AccountFragment

abstract class BaseAccountFragment<VB: ViewBinding>: BaseAuthFragment<VB>() {

    private val accountFragment: AccountFragment
        get() = findParentAs()

    protected fun transactionTo(fragment: Fragment, enterTransition: Any) {
        fragment.enterTransition = enterTransition
        accountFragment.childFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
        }
    }

}