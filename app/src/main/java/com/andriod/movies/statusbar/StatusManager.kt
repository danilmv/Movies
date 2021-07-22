package com.andriod.movies.statusbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.HashMap

object StatusManager {
    private var currentId = 0

    private val _statuses = MutableLiveData<Map<Int, String>>()
    val statuses: LiveData<Map<Int, String>> = _statuses
    private val statusesMap: MutableMap<Int, String> = HashMap()

    private val _groups = mutableMapOf<Int, MutableSet<Int>>()
    val groups get() = _groups
    private val groupById = mutableMapOf<Int, Int>()

    const val TAG = "@@StatusManager"

    init {
        _statuses.value = HashMap()
    }

    fun create(message: String, groupId: Int = 0): Int {
        val id = currentId++
        statusesMap[id] = message
        _statuses.postValue(statusesMap)

        if (_groups[groupId] == null) _groups[groupId] = TreeSet()
        _groups[groupId]?.add(id)
        groupById[id] = groupId

        return id
    }

    fun change(id: Int, message: String) {
        statusesMap[id] = message
        _statuses.postValue(statusesMap)
    }

    fun close(id: Int) {
        statusesMap.remove(id)
        _statuses.postValue(statusesMap)

        _groups[groupById[id]]?.remove(id)
    }
}