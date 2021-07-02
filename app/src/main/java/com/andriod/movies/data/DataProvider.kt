package com.andriod.movies.data

import com.andriod.movies.entity.Movie
import java.util.HashSet

abstract class DataProvider {
    private var subscribers: MutableSet<Runnable> = HashSet()
    val data: MutableMap<String, Movie> = HashMap()

    protected fun notifySubscribers() {
        for (subscriber in subscribers) {
            subscriber.run()
        }
    }

    fun subscribe(subscriber: Runnable) {
        subscribers.add(subscriber)
    }

    fun unSubscribe(subscriber: Runnable) {
        subscribers.remove(subscriber)
    }

    abstract fun updateData(movie: Movie)
    abstract fun findMovies(query: String): List<Movie>
}