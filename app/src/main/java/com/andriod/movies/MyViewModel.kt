package com.andriod.movies

import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DummyDataProvider
import com.andriod.movies.entity.Movie

object MyViewModel {
    val movies = MutableLiveData<MutableMap<String, Movie>>()

    fun getData() {
        val dummy = DummyDataProvider()
        movies.value?.putAll(dummy.data)

        for (movie in dummy.findMovies("")) {
            movies.value?.put(movie.id, movie)
        }
    }
}