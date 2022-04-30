package com.andriod.movies.data

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.andriod.movies.entity.Genre
import com.andriod.movies.entity.Movie

typealias Subscriber = (() -> Unit)

abstract class DataProvider {
    private var subscribers = mutableMapOf(
        SubscriberType.DATA to HashSet<Subscriber>(),
        SubscriberType.SEARCH to HashSet(),
        SubscriberType.ERROR to HashSet(),
        SubscriberType.DETAILS to HashSet(),
        SubscriberType.GENRES to HashSet(),
    )

    val data: MutableMap<String, Movie> = HashMap()
    private val handler = Handler(Looper.getMainLooper())

    val searchResultsData: MutableMap<String, Movie> = HashMap()

    var errorMessage = ""
        set(value) {
            field = value
            notifySubscribers(SubscriberType.ERROR)
        }
    val genres: MutableMap<String, Genre> = HashMap()
    var isGenresLoaded = false

    private val dataThread = HandlerThread("dataThread").apply { isDaemon = true;start() }
    protected val dataHandler = Handler(dataThread.looper)

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
        if (data.containsKey(movie.id)) {
            data[movie.id]?.populateData(movie)
        } else {
            data[movie.id] = movie
        }
        notifySubscribers((SubscriberType.DATA))


        if (searchResultsData.containsKey(movie.id)) {
            searchResultsData[movie.id]?.populateData(data[movie.id] ?: movie)
            notifySubscribers((SubscriberType.SEARCH))
        }
    }

    abstract fun findMovies(query: String)

    abstract fun startService()

    open fun getMovieDetails(movie: Movie) {}
    open fun getMovieDetails(movies: List<Movie>) {}
    open fun requestMoreData() {}
    open fun getMovieVideos(movie: Movie){}

    protected open fun updateGenres(data: MutableMap<String, Movie> = this.data) {
//        if (!isGenresLoaded) return
        if (genres.isEmpty()) return

        var dataChanged = false

        for (movie in data.values.filter { !it.isGenreUpdated }) {
            movie.isGenreUpdated = true
            if (movie._genre.isNullOrEmpty()) {
                continue
            }
            movie.genre.addAll(movie._genre)
            for (i in movie._genre.indices) {
                movie.genre[i] = genres[movie._genre[i]]?.name ?: "?"
            }
            dataChanged = true
        }
        if (dataChanged) notifySubscribers(SubscriberType.DATA)
    }

    companion object {
        const val TAG = "@@DataProvider"

        enum class SubscriberType { DATA, SEARCH, ERROR, DETAILS, GENRES }

        enum class StatusGroup(val id: Int){
            WEB_DATA_REQUESTED(1),
            ROOM_DATA_REQUESTED(2),
            WEB_DETAILS_REQUESTED(3),

        }
    }
}