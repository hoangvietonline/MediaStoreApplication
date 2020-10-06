package com.example.myapplication.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Album
import com.example.myapplication.model.MediaStoreImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _album = MutableLiveData<List<Album>>()
    private val _image = MutableLiveData<List<MediaStoreImage>>()
    private val _file = MutableLiveData<List<MediaStoreImage>>()

    val album: LiveData<List<Album>> = _album
    val image: LiveData<List<MediaStoreImage>> = _image
    val file: LiveData<List<MediaStoreImage>> = _file

    private var contentObserver: ContentObserver? = null

    fun loadAlbum() {
        viewModelScope.launch {
            val albumList = queryAlbum()
            _album.postValue(albumList)

            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadAlbum()
                }
            }
        }
    }

    fun loadImageByAlbum(album: String?) {
        if (album == null)
            return

        viewModelScope.launch {
            val imageList = queryImage(album)
            _image.postValue(imageList)
            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ) {
                    if (it) {
                        loadImageByAlbum(album)
                    }
                }
            }
        }
    }

    private suspend fun queryImage(album: String): List<MediaStoreImage>? {
        val images = mutableListOf<MediaStoreImage>()
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.MediaColumns.DATA
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val file = File(cursor.getString(name)).parentFile
                    if (file != null) {
                        val albumName = file.name
                        if (albumName == album) {
                            val image = MediaStoreImage(
                                id,
                                displayName,
                                dateModified,
                                contentUri,
                                albumName
                            )
                            images += image
                        }
                    }
                }
            }
        }
        return images
    }

    private fun ContentResolver.registerObserver(
        uri: Uri,
        observer: (selfChange: Boolean) -> Unit
    ): ContentObserver {
        val contentObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }
        }
        registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }

    fun String.themTenViet(): String {
        return "$this Viet"
    }

    private suspend fun queryAlbum(): List<Album> {
        val images = mutableListOf<MediaStoreImage>()
        val albums = mutableListOf<Album>()
        val albumNames = hashSetOf<String>()
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.MediaColumns.DATA
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val path = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                var file: File?

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                    )
                    file = File(cursor.getString(path)).parentFile
                    if (file != null) {
                        val albumName = file.name
                        albumNames.add(albumName)
                        val image =
                            MediaStoreImage(id, displayName, dateModified, contentUri, albumName)
                        images += image
                    }
                }

                for (x in albumNames) {
                    albums.add(Album(x))
                }

                for (x in images) {
                    for (i in 0 until albums.size) {
                        if (x.albumName == albums[i].displayName) {
                            albums[i].mediaStoreImages = x
                        }
                    }
                }
            }
        }
        return albums
    }
}
