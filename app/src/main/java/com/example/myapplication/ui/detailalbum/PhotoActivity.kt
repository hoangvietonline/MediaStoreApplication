package com.example.myapplication.ui.detailalbum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.common.Constant
import com.example.myapplication.model.MediaStoreImage
import com.example.myapplication.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_details_album.*

class PhotoActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_album)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mAdapter = PhotoAdapter()

        val albumName = intent.getStringExtra(Constant.ALBUM_NAME_KEY)

        viewModel.loadImageByAlbum(albumName)
        viewModel.image.observe(this,
            Observer<List<MediaStoreImage>> {
                mAdapter.addPhotoList(it as MutableList<MediaStoreImage>)
            })

        recyclerViewPhoto.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@PhotoActivity, 2)
        }
    }
}
