package com.andriod.movies.data

import android.os.Handler
import android.os.Looper
import com.andriod.movies.entity.Movie
import java.util.HashSet
typealias Subscriber = (() -> Unit)

abstract class DataProvider {
    private var subscribers: MutableSet<Subscriber> = HashSet()
    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    protected fun notifySubscribers() {
        for (subscriber in subscribers) {
            handler.post(subscriber)
        }
    }

    fun subscribe(subscriber: Subscriber) {
        subscribers.add(subscriber)
    }

    fun unsubscribe(subscriber: Subscriber) {
        subscribers.remove(subscriber)
    }

    abstract fun updateData(movie: Movie)
    abstract fun findMovies(query: String): List<Movie>
}