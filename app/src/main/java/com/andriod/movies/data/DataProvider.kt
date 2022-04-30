package com.andriod.movies.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.andriod.movies.entity.Movie
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

typealias Subscriber = (() -> Unit)

abstract class DataProvider {
    private var subscribers = mutableMapOf(
        SubscriberType.DATA to HashSet<Subscriber>(),
        SubscriberType.SEARCH to HashSet())

    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    val searchResultsData: MutableList<Movie> = ArrayList()

    protected fun notifySubscribers(type: SubscriberType) {
        subscribers[type]?.let {
            for (subscriber in it) {
                handler.post(subscriber)
            }
        }
    }

    fun subscribe(type: SubscriberType, subscriber: Subscriber) {
        subscribers[type]?.add(subscriber)
        Log.d(TAG,
            "subscribe() called with: type = ${type.name}, numOfSubscribers = ${subscribers[type]?.size}")
    }

    fun unsubscribe(type: SubscriberType, subscriber: Subscriber) {
        subscribers[type]?.remove(subscriber)
        Log.d(TAG,
            "unsubscribe() called with: type = ${type.name}, numOfSubscribers = ${subscribers[type]?.size}")
    }

    abstract fun updateData(movie: Movie)
    abstract fun findMovies(query: String)

    companion object {
        const val TAG = "@@DataProvider"

        enum class SubscriberType { DATA, SEARCH }
    }
}