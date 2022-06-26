package limbo.mrvoid.promag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import limbo.mrvoid.promag.R
import limbo.mrvoid.promag.models.Board

open class BoardItemAdapter(private val context: Context, private val list: ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_itemboard, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_nav_user)
                .into(holder.itemView.findViewById(R.id.ivboardimage))

            holder.itemView.findViewById<TextView>(R.id.tvname).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tvcreatedby).text = "Created By: ${model.createdBy}"
            holder.itemView.setOnClickListener {
                if(onClickListener!=null) {
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}