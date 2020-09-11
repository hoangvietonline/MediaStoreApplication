package com.example.myapplication.ui.detailalbum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.MediaStoreImage
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private var photoList = mutableListOf<MediaStoreImage>()

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun addPhotoList(photoList: MutableList<MediaStoreImage>) {
        this.photoList = photoList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val mediaStoreImage = photoList[position]
        Glide.with(holder.itemView.context)
            .load(mediaStoreImage.contentUri)
            .into(holder.itemView.imgPhoto)
    }
}
