package limbo.mrvoid.promag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.activities.CardDetailsActivity
import limbo.mrvoid.promag.activities.TaskListActivity
import limbo.mrvoid.promag.models.Board
import limbo.mrvoid.promag.models.Card
import limbo.mrvoid.promag.models.SelectedMembers

open class CardListItemAdapter(private val context: Context, private val list: ArrayList<Card>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            if(model.labelColor.isNotEmpty()) {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color).setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name
            if((context as TaskListActivity).mAssignedMemberDetailList.size>0) {
                val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()
                for (i in context.mAssignedMemberDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMemberDetailList[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMemberList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMemberList.size>0) {
                    if(selectedMemberList.size==1 && selectedMemberList[0].id==model.createdBy) {
                        holder.itemView.findViewById<RecyclerView>(R.id.rvcardselectedmemberslist).visibility = View.GONE
                    } else {
                        holder.itemView.findViewById<RecyclerView>(R.id.rvcardselectedmemberslist).visibility = View.VISIBLE
                        holder.itemView.findViewById<RecyclerView>(R.id.rvcardselectedmemberslist).layoutManager = GridLayoutManager(context,4)
                        val adapter = CardMemberListItemsAdapter(context,selectedMemberList,false)
                        holder.itemView.findViewById<RecyclerView>(R.id.rvcardselectedmemberslist).adapter=adapter
                        adapter.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener!=null) {
                                    onClickListener!!.onClick(holder.adapterPosition)
                                }
                            }
                        })
                    }
                }
                else {
                    holder.itemView.findViewById<RecyclerView>(R.id.rvcardselectedmemberslist).visibility = View.GONE
                }
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null) {
                    onClickListener!!.onClick(holder.adapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}