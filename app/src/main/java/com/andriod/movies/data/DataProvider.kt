package com.andriod.movies.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.andriod.movies.entity.Movie

typealias Subscriber = (() -> Unit)

abstract class DataProvider {
    private var subscribers = mutableMapOf(
        SubscriberType.DATA to HashSet<Subscriber>(),
        SubscriberType.SEARCH to HashSet(),
        SubscriberType.ERROR to HashSet(),
        SubscriberType.DETAILS to HashSet(),
    )

    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    val searchResultsData: MutableMap<String, Movie> = HashMap()

    var errorMessage = ""
        set(value) {
            field = value
            notifySubscribers(SubscriberType.ERROR)
        }

    init {
        startService()
    }

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


    open fun updateData(movie: Movie) {
        data[movie.id] = movie
        notifySubscribers((SubscriberType.DATA))
    }

    abstract fun findMovies(query: String)

    abstract fun startService()

    open fun getMovieDetails(movie: Movie){}

    companion object {
        const val TAG = "@@DataProvider"

        enum class SubscriberType { DATA, SEARCH, ERROR, DETAILS }
    }
}