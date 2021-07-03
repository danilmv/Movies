package com.andriod.movies.data

import android.os.Handler
import android.os.Looper
import com.andriod.movies.entity.Movie
import java.util.HashSet

abstract class DataProvider {
    private var subscribers: MutableSet<Runnable> = HashSet()
    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    protected fun notifySubscribers() {
        for (subscriber in subscribers) {
            handler.post(subscriber)
        }
    }

    fun subscribe(subscriber: Runnable) {
        subscribers.add(subscriber)
    }

    fun unsubscribe(subscriber: Runnable) {
        subscribers.remove(subscriber)
    }

    abstract fun updateData(movie: Movie)
    abstract fun findMovies(query: String): List<Movie>
}