package projekt.cloud.piece.pic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import projekt.cloud.piece.pic.databinding.ActivityLauncherBinding

class LauncherActivity: AppCompatActivity() {
    
    private lateinit var binding: ActivityLauncherBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}