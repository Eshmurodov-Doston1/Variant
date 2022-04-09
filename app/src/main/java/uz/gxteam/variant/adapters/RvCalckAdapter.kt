package uz.gxteam.variant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.ItemLockBinding

class RvCalckAdapter(var onItemClickListener: OnItemClickListener, var listNumber:List<String>):RecyclerView.Adapter<RvCalckAdapter.Vh>() {
    inner class Vh(var itemLockBinding: ItemLockBinding):RecyclerView.ViewHolder(itemLockBinding.root){
        fun onBind(str:String,position: Int){
            itemLockBinding.name.text = str
            when(position){
                9->{
                    itemLockBinding.image.visibility = View.VISIBLE
                    itemLockBinding.cardNumber.visibility = View.GONE
                    itemLockBinding.image.setOnClickListener {
                        onItemClickListener.bioMetrickClick(position)
                    }
                }
                11->{
                    itemLockBinding.image.visibility = View.VISIBLE
                    itemLockBinding.cardNumber.visibility = View.GONE
                    itemLockBinding.image.setImageResource(R.drawable.ic_delete)

                    itemLockBinding.image.setOnClickListener {
                        onItemClickListener.onItemClickDelete(position)
                    }
                }
                else->{
                    itemLockBinding.image.visibility = View.GONE
                    itemLockBinding.cardNumber.visibility = View.VISIBLE
                }
            }
            itemLockBinding.cardNumber.setOnClickListener {
                onItemClickListener.onItemClick(str,position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemLockBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(listNumber[position],position)
    }

    override fun getItemCount(): Int {
       return listNumber.size
    }

    interface OnItemClickListener{
        fun onItemClick(str:String,position: Int)
        fun onItemClickDelete(position: Int)
        fun bioMetrickClick(position: Int)
    }
}