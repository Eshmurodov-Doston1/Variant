package uz.gxteam.variant.adapters.category_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gxteam.variant.databinding.ItemCreateRequestBinding
import uz.gxteam.variant.models.uploaCategory.UploadCategoryItem

class CategoryAdapter(var listCategory:ArrayList<UploadCategoryItem>, private val onClick:(item: UploadCategoryItem, position:Int)->Unit):RecyclerView.Adapter<CategoryAdapter.Vh>() {
    inner class Vh(var itemCreateRequestBinding: ItemCreateRequestBinding):RecyclerView.ViewHolder(itemCreateRequestBinding.root){
        fun onBind(uploadCategoryItem: UploadCategoryItem, position: Int){
            itemCreateRequestBinding.title.text = uploadCategoryItem.title
            itemCreateRequestBinding.card.setOnClickListener {
                if (uploadCategoryItem.is_uploaded!=1){
                    onClick.invoke(uploadCategoryItem,position)
                }else{
                    onClick.invoke(UploadCategoryItem(-2,-2,1,"Этот раздел уже загружен"),position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemCreateRequestBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(listCategory[position],position)
    }

    override fun getItemCount(): Int {
     return listCategory.size
    }
}