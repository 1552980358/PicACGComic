package projekt.cloud.piece.pic.ui.account.login

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentAccountLoginBinding
import projekt.cloud.piece.pic.ui.account.login.login.LoginFragment
import projekt.cloud.piece.pic.ui.account.login.register.RegisterFragment
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar

class AccountLoginFragment: BaseFragment<FragmentAccountLoginBinding>() {

    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val tabLayout: TabLayout
        get() = binding.tabLayout
    private val viewPager2: ViewPager2
        get() = binding.viewPager2

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = requireParentFragment().findNavController()
    }
    
    override fun setUpActionBar() {
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)
    }
    
    override fun setUpViews() {
        binding.viewPager2.adapter = object: FragmentStateAdapter(this) {
            private val fragments = arrayOf(LoginFragment(), RegisterFragment())
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }
        arrayOf(R.string.account_login_tab_login, R.string.account_login_tab_register).let {
            TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
                tab.setText(it[pos])
            }.attach()
        }
    }

}