package projekt.cloud.piece.pic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.pic.databinding.ActivityMainBinding
import projekt.cloud.piece.pic.util.ActivityUtil.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        
        val viewModel: MainViewModel by viewModels()
        
        val username = intent.getStringExtra(getString(R.string.auth_sign_in_result_username))
        val password = intent.getStringExtra(getString(R.string.auth_sign_in_result_password))
        val token = intent.getStringExtra(getString(R.string.auth_sign_in_result_token))
        if (username.isNullOrBlank() || password.isNullOrBlank() || token.isNullOrBlank()) {
            startActivity(LauncherActivity::class.java)
            finish()
            return
        }
        viewModel.setAccount(username, password, token)
        viewModel.obtainSystemInsets(window.decorView)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}