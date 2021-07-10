package com.andriod.movies.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.andriod.movies.entity.Movie
import java.util.HashSet

typealias Subscriber = (() -> Unit)

abstract class DataProvider {
    private var subscribers: MutableSet<Subscriber> = HashSet()
    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    val searchResultsData: MutableList<Movie> = ArrayList()

    protected fun notifySubscribers() {
        for (subscriber in subscribers) {
            handler.post(subscriber)
        }
    }

    fun subscribe(subscriber: Subscriber) {
        subscribers.add(subscriber)
        Log.d(TAG,
            "subscribe() called with: subscriber = $subscriber, numOfSubscribers = ${subscribers.size}")
    }

    fun unsubscribe(subscriber: Subscriber) {
        subscribers.remove(subscriber)
    }

    abstract fun updateData(movie: Movie)
    abstract fun findMovies(query: String)

    companion object {
        const val TAG = "@@DataProvider"
    }
}