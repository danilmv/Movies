package com.andriod.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DummyDataProvider
import com.andriod.movies.entity.Movie

object MyViewModel {
    private val _movies = MutableLiveData<Map<String, Movie>>()
    val movies: LiveData<Map<String, Movie>> = _movies
    private val dummy = DummyDataProvider()

    fun initData() {
        if (movies.value?.isEmpty() != false) {
            _movies.value = HashMap()

            dummy.subscribe {
                _movies.postValue(dummy.data)
            }
        }
    }

    fun updateData(movie: Movie){
        dummy.updateData(movie)
    }
}