package com.andriod.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DummyDataProvider
import com.andriod.movies.entity.Movie

object MyViewModel {
    private val _movies = MutableLiveData<Map<String, Movie>>()
    val movies: LiveData<Map<String, Movie>> = _movies

    fun initData() {
        if (movies.value?.isEmpty() != false) {
            _movies.value = HashMap()

            val dummy = DummyDataProvider()
            dummy.subscribe {
                _movies.postValue(dummy.data)
            }
        }
    }
}