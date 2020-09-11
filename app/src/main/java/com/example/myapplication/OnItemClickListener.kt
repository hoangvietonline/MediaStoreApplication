package com.example.myapplication

interface OnItemClickListener<T> {
    fun onItemClick(position: Int, obj: T)
}