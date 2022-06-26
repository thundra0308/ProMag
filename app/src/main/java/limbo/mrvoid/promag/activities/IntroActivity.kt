package limbo.mrvoid.promag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import limbo.mrvoid.promag.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding?.signup?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding?.signin?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}