package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.OnItemClickListener
import com.example.myapplication.R
import com.example.myapplication.model.Album
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumAdapter (private val onItemClickListener: OnItemClickListener<Album>):
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var albumList = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    fun addAlbumList(albums: MutableList<Album>) {
        albumList = albums
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        if (album.mediaStoreImages != null) {
            Glide.with(holder.itemView.context)
                .load(album.mediaStoreImages?.contentUri)
                .into(holder.itemView.imgAlbum)
        }
        holder.itemView.tvAlbum.text = album.displayName
        holder.itemView.setOnClickListener{
            onItemClickListener.onItemClick(position,album)
        }
    }
}