package uz.gxteam.variant.adapters.chatListAdapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gxteam.variant.databinding.ItemChatFromBinding
import uz.gxteam.variant.databinding.ItemChatToBinding
import uz.gxteam.variant.models.messages.resMessage.Message

class ChatAdapter(var userIdApp:Int):ListAdapter<Message,RecyclerView.ViewHolder>(MyDiffUtil()) {
    inner class FromVh(var itemChatFromBinding: ItemChatFromBinding):RecyclerView.ViewHolder(itemChatFromBinding.root){
        fun fromBind(message: Message,position: Int){
            itemChatFromBinding.messageFrom.text = message.message
        }
    }

    inner class ToVh(var itemChatToBinding: ItemChatToBinding):RecyclerView.ViewHolder(itemChatToBinding.root){
        fun toBind(message: Message,position: Int){
            itemChatToBinding.messageFrom.text = message.message
        }
    }

    class MyDiffUtil:DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            return FromVh(ItemChatFromBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ItemChatToBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
            val fromVh = holder as FromVh
            fromVh.fromBind(getItem(position),position)
        }else{
            val toVh = holder as ToVh
            toVh.toBind(getItem(position),position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).user_id==userIdApp){
            return 1
        }else{
            return 2
        }
    }
}