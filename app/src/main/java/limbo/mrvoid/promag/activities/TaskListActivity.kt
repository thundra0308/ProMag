package limbo.mrvoid.promag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.adapters.TaskListItemAdapter
import limbo.mrvoid.promag.databinding.ActivityTaskListBinding
import limbo.mrvoid.promag.firebase.FirestoreClass
import limbo.mrvoid.promag.models.Board
import limbo.mrvoid.promag.models.Card
import limbo.mrvoid.promag.models.Task
import limbo.mrvoid.promag.models.User
import limbo.mrvoid.promag.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog("Please Wait ...")
        FirestoreClass().getBoardDetails(this, mBoardDocumentId)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode== MEMBERS_REQUEST_CODE || requestCode == CARD_DETAIL_REQUEST_CODE) && resultCode== RESULT_OK) {
            showProgressDialog("Please Wait ...")
            FirestoreClass().getBoardDetails(this, mBoardDocumentId)
        } else {
            Log.e("Cancelled","Cancelled")
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding?.toolbartasklist)
        if(supportActionBar!=null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_backwhite)
            supportActionBar?.title = mBoardDetails.name
        }
        binding?.toolbartasklist?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setActionBar()

        showProgressDialog("Please Wait ...")
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        showProgressDialog("Please Wait ...")
        FirestoreClass().getBoardDetails(this@TaskListActivity,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait ...")
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName:String, model:Task) {
        val task = Task(listName,model.createdBy,model.cards)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait ...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait ...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedToUsers: ArrayList<String> = ArrayList()
        cardAssignedToUsers.add(FirestoreClass().getCurrentUserId())
        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedToUsers)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)
        val task = Task(mBoardDetails.taskList[position].title,mBoardDetails.taskList[position].createdBy,cardsList)
        mBoardDetails.taskList[position] = task
        showProgressDialog("Please Wait ...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_member -> {
                val intent = Intent(this@TaskListActivity,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this@TaskListActivity,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAIL_REQUEST_CODE)
    }

    fun boardMemberDetailsList(list: ArrayList<User>) {
        mAssignedMemberDetailList = list
        hideProgressDialog()
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        binding?.rvtasklist?.layoutManager = LinearLayoutManager(this@TaskListActivity,LinearLayoutManager.HORIZONTAL,false)
        binding?.rvtasklist?.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this@TaskListActivity,mBoardDetails.taskList)
        binding?.rvtasklist?.adapter = adapter
    }

    fun updateCardsInTaskList(taskListPosition: Int,cards: ArrayList<Card>) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[taskListPosition].cards = cards
        showProgressDialog("Please Wait ...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAIL_REQUEST_CODE: Int = 14
    }

}