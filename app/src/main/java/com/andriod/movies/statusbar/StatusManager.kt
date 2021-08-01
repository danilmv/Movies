package com.andriod.movies.statusbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.HashMap

object StatusManager {
    private var currentId = 0
    private var currentGroupId = 1000

    private val _statuses = MutableLiveData<Map<Int, String>>()
    val statuses: LiveData<Map<Int, String>> = _statuses
    private val statusesMap: MutableMap<Int, String> = HashMap()

    private val _groups = mutableMapOf<Int, MutableSet<Int>>()
    val groups get() = _groups
    private val groupById = mutableMapOf<Int, Int>()

    private val _history = MutableLiveData<MutableList<String>>()
    val history: LiveData<MutableList<String>> get() = _history
    private val historyValue = mutableListOf<String>()

    const val TAG = "@@StatusManager"

    init {
        _statuses.value = HashMap()
    }

    fun create(message: String, groupId: Int = currentGroupId++): Int {
        val id = currentId++
        statusesMap[id] = message
        historyValue.add("$id: $message")

        if (_groups[groupId] == null) _groups[groupId] = TreeSet()
        _groups[groupId]?.add(id)
        groupById[id] = groupId

        _statuses.postValue(statusesMap)
        _history.postValue(historyValue)

        return id
    }

    fun change(id: Int, message: String) {
        statusesMap[id] = message
        historyValue.add("$id: $message")

        _statuses.postValue(statusesMap)
        _history.postValue(historyValue)
    }

    fun close(id: Int) {
        statusesMap.remove(id)
        historyValue.add("$id: closed")

        _statuses.postValue(statusesMap)
        _groups[groupById[id]]?.remove(id)
        _history.postValue(historyValue)
    }
}