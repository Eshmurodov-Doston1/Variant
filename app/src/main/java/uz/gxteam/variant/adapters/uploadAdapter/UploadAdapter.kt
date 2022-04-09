package uz.gxteam.variant.adapters.uploadAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import uz.gxteam.variant.BuildConfig.BASE_URL
import uz.gxteam.variant.databinding.ItemUploadBinding
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos

class UploadAdapter(var onItemLongClick: OnIemLongClick):ListAdapter<UploadPhotos,UploadAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemUploadBinding: ItemUploadBinding):RecyclerView.ViewHolder(itemUploadBinding.root){
        fun onBind(uploadPhotos: UploadPhotos,position: Int){
            itemUploadBinding.image.load("$BASE_URL/${uploadPhotos.file_link}"){
                crossfade(true)
                crossfade(400)
                transformations(RoundedCornersTransformation(30f))
            }
            itemUploadBinding.image.setOnLongClickListener {
                onItemLongClick.onUploadClick(uploadPhotos,position)
                true
            }
        }
    }


    class MyDiffUtil:DiffUtil.ItemCallback<UploadPhotos>(){
        override fun areItemsTheSame(oldItem: UploadPhotos, newItem: UploadPhotos): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UploadPhotos, newItem: UploadPhotos): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUploadBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position),position)
    }

    interface OnIemLongClick{
        fun onUploadClick(uploadPhotos: UploadPhotos,position: Int)
    }
}