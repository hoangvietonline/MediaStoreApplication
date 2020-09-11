package com.example.myapplication.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.OnItemClickListener
import com.example.myapplication.R
import com.example.myapplication.common.Constant
import com.example.myapplication.model.Album
import com.example.myapplication.ui.detailalbum.PhotoActivity
import com.example.myapplication.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnItemClickListener<Album> {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mAdapter = AlbumAdapter(this)

        viewModel.album.observe(this,
            Observer<List<Album>> {
                mAdapter.addAlbumList(it as MutableList<Album>)
            })

        recyclerViewMain.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }

        btnTakePhoto.setOnClickListener {
            openMediaStore()
        }
    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    private fun showImages() {
        viewModel.loadAlbum()
        btnTakePhoto.visibility = View.GONE
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                }
                return
            }
        }
    }

    override fun onItemClick(position: Int, obj: Album) {
        val intent = Intent(this@MainActivity, PhotoActivity::class.java)
        intent.putExtra(Constant.ALBUM_NAME_KEY, obj.displayName)
        startActivity(intent)
    }
}

private const val READ_EXTERNAL_STORAGE_REQUEST: Int = 1234