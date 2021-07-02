package com.andriod.movies

import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DummyDataProvider
import com.andriod.movies.entity.Movie

object MyViewModel {
    val movies = MutableLiveData<MutableMap<String, Movie>>()

    fun getData() {
        if (movies.value?.isEmpty() != false) {
            movies.value = HashMap()

            val dummy = DummyDataProvider()
            dummy.subscribe {
                movies.value = dummy.data
            }
        }
    }
}