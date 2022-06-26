package limbo.mrvoid.promag.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.databinding.ActivitySignUpBinding
import limbo.mrvoid.promag.firebase.FirestoreClass
import limbo.mrvoid.promag.models.User

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarsingup)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding?.toolbarsingup?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.btnsignup?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name: String = binding?.etname?.text.toString().trim{it<=' '}
        val email: String = binding?.etemail?.text.toString().trim{it<=' '}
        val password: String = binding?.etpass?.text.toString().trim{it<=' '}
        if(validateForm(name,email,password)) {
            showProgressDialog("Registering ....")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(this@SignUpActivity) {
                    task->
                    if(task.isSuccessful) {
                        var firebaseUser: FirebaseUser = task.result!!.user!!
                        var registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    } else {
                        Toast.makeText(this,"Registration Failed !!".toString(),Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter a Name")
                false
            }
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

    fun userRegisteredSuccess() {
        Toast.makeText(this,"You Have Been Successfully Registered",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

}