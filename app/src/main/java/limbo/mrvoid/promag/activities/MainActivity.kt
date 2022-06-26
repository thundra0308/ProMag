package limbo.mrvoid.promag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SharedMemory
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations
import de.hdodenhof.circleimageview.CircleImageView
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.adapters.BoardItemAdapter
import limbo.mrvoid.promag.databinding.ActivityMainBinding
import limbo.mrvoid.promag.databinding.NavHeaderMainBinding
import limbo.mrvoid.promag.firebase.FirestoreClass
import limbo.mrvoid.promag.models.Board
import limbo.mrvoid.promag.models.User
import limbo.mrvoid.promag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String
    private lateinit var mSharedPref: SharedPreferences
    private var mainMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setActionBar()
        mSharedPref = this.getSharedPreferences(Constants.PROMAG_PREFRENCES, MODE_PRIVATE)
        val tokenUpdated = mSharedPref.getBoolean(Constants.FCM_TOKEN_UPDATED,false)
        if(tokenUpdated) {
            showProgressDialog("Please Wait ...")
            FirestoreClass().loadUserData(this,true)
        } else {
            FirebaseInstanceId.getInstance()
                .instanceId.addOnSuccessListener(this@MainActivity) { instanceIdResult ->
                    updateFCMToken(instanceIdResult.token)
            }
        }
        binding?.navview?.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this,true)
        binding?.appbar?.floatBoard?.setOnClickListener {
            val intent = Intent(this@MainActivity,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, MY_BOARD_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if(resultCode==Activity.RESULT_OK && requestCode == MY_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardList(this)
        } else {
            Log.e("Cancelled","Cancelled")
        }
    }

    fun updateNavigationUserDetails(user: User,readBoardList: Boolean) {
        hideProgressDialog()
        mUserName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_nav_user)
            .into(findViewById<CircleImageView>(R.id.hdoden))

        val name = findViewById<TextView>(R.id.tvusername)
        name.text = user.name
        if(readBoardList) {
            showProgressDialog("Please Wait ...")
            FirestoreClass().getBoardList(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.myprof -> {
                startActivityForResult(Intent(this@MainActivity,MyProfileActivity::class.java),MY_PROFILE_REQUEST_CODE)
            }
            R.id.navsignout -> {
                FirebaseAuth.getInstance().signOut()
                mSharedPref.edit().clear().apply()
                val intent = Intent(this@MainActivity,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return true
    }

    fun populateBoardListToUI(boardList: ArrayList<Board>) {
        hideProgressDialog()
        if(boardList.size>0) {
            binding?.appbar?.llmaincontent?.rvboardlist?.visibility = View.VISIBLE
            binding?.appbar?.llmaincontent?.tvnoboardavailable?.visibility = View.GONE
            binding?.appbar?.llmaincontent?.rvboardlist?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding?.appbar?.llmaincontent?.rvboardlist?.setHasFixedSize(true)
            val adapter = BoardItemAdapter(this,boardList)
            binding?.appbar?.llmaincontent?.rvboardlist?.adapter = adapter
            adapter.setOnClickListener(object: BoardItemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            binding?.appbar?.llmaincontent?.rvboardlist?.visibility = View.GONE
            binding?.appbar?.llmaincontent?.tvnoboardavailable?.visibility = View.VISIBLE
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding?.appbar?.toolbarmainacivity)
        binding?.appbar?.toolbarmainacivity?.setNavigationIcon(R.drawable.ic_hamburger)
        binding?.appbar?.toolbarmainacivity?.setNavigationOnClickListener {
            if(binding?.drawerl?.isDrawerOpen(GravityCompat.START)==true) {
                binding?.drawerl?.closeDrawer(GravityCompat.START)
            } else {
                binding?.drawerl?.openDrawer(GravityCompat.START)
            }
        }
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPref.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog("Please Wait ...")
        FirestoreClass().loadUserData(this,true)
    }

    private fun updateFCMToken(token:String) {
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog("Please Wait ...")
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(binding?.drawerl?.isDrawerOpen(GravityCompat.START)==true) {
            binding?.drawerl?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val MY_BOARD_REQUEST_CODE: Int = 12
    }

}
