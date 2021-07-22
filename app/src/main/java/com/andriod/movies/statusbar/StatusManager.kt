package com.andriod.movies.statusbar

import androidx.lifecycle.MutableLiveData

object StatusManager {
    private var currentId = 0

    private val _statuses = MutableLiveData<MutableMap<Int, String>>()
    val statuses: MutableLiveData<MutableMap<Int, String>> = _statuses
    private val statusesMap: MutableMap<Int, String> = HashMap()

    init {
        _statuses.value = HashMap()
    }

    fun create(message: String): Int {
        val id: Int = currentId++

        statusesMap[id] = message
        _statuses.postValue(statusesMap)

        return id
    }

    fun change(id: Int, message: String) {
        statusesMap[id] = message
        _statuses.postValue(statusesMap)
    }

    fun close(id: Int) {
        statusesMap.remove(id)
        _statuses.postValue(statusesMap)
    }
}