package limbo.mrvoid.promag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.databinding.ActivitySignInBinding
import limbo.mrvoid.promag.firebase.FirestoreClass
import limbo.mrvoid.promag.models.User

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarsingin)
        FirebaseAuth.getInstance()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding?.toolbarsingin?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.btnsignin?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser() {
        val email: String = binding?.etemail?.text.toString()
        val password: String = binding?.etpass?.text.toString()
        if(validateForm(email,password)) {
            showProgressDialog("Signing In ....")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(this@SignInActivity) {
                    task->
                hideProgressDialog()
                if(task.isSuccessful) {
                    startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                    FirestoreClass().loadUserData(this)
                } else {
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter an Email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter a Password")
                false
            } else -> {
                true
            }
        }
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
        finish()
    }

}