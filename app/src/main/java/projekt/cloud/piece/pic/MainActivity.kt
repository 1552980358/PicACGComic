package projekt.cloud.piece.pic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import projekt.cloud.piece.pic.databinding.ActivityMainBinding
import projekt.cloud.piece.pic.util.SplashScreenCompat.SplashScreenCompatUtil.applySplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val splashScreenCompat = applySplashScreen()
        splashScreenCompat.setKeepOnScreenCondition(true)
        
        super.onCreate(savedInstanceState)
        
        val applicationConfigs: ApplicationConfigs by viewModels()
        applicationConfigs.setUpWindowProperties(window.decorView)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        applicationConfigs.initializeAccount(this) {
            splashScreenCompat.setKeepOnScreenCondition(false)
        }
    }

}