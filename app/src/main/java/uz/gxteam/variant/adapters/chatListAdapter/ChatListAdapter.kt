package uz.gxteam.variant.adapters.chatListAdapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gxteam.variant.databinding.ItemChatBinding
import uz.gxteam.variant.models.getApplications.DataApplication

class ChatListAdapter(var context:Context, var onItemClickListener:OnItemClickListener):ListAdapter<DataApplication,ChatListAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemChatBinding: ItemChatBinding):RecyclerView.ViewHolder(itemChatBinding.root){
        @RequiresApi(Build.VERSION_CODES.M)
        fun onBind(dataApplication: DataApplication, position: Int){
            itemChatBinding.name.text = dataApplication.full_name
            itemChatBinding.number.text = dataApplication.contract_number
            itemChatBinding.card.setOnClickListener {
                onItemClickListener.onItemClick(dataApplication,position)
            }
        }
    }

    class MyDiffUtil:DiffUtil.ItemCallback<DataApplication>(){
        override fun areItemsTheSame(oldItem: DataApplication, newItem: DataApplication): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataApplication,
            newItem: DataApplication
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position),position)
    }

    interface OnItemClickListener{
        fun onItemClick(dataApplication: DataApplication,position: Int)
    }
}