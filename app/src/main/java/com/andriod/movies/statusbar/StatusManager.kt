package com.andriod.movies.statusbar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object StatusManager {
    private var currentId = 0
    private val _statuses = MutableLiveData<MutableMap<Int, String>>()
    val statuses: LiveData<MutableMap<Int, String>> = _statuses

    const val TAG = "@@StatusManager"

    init {
        _statuses.value = HashMap()
    }

    fun create(message: String): Int {
        Log.d(TAG, "create() called with: message = $message id = $currentId")
        val id: Int = currentId++

        val value = _statuses.value
        value?.set(id, message)
        _statuses.postValue(value)

        return id
    }

    fun change(id: Int, message: String) {
        Log.d(TAG, "change() called with: id = $id, message = $message")

        val value = _statuses.value
        value?.set(id, message)
        _statuses.postValue(value)
    }

    fun close(id: Int) {
        val value = _statuses.value
        value?.remove(id)
        _statuses.postValue(value)

        Log.d(TAG, "close() called with: id = $id numOfStatuses = ${_statuses.value?.size}")
    }
}