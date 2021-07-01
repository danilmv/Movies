package com.andriod.movies

import androidx.lifecycle.MutableLiveData
import com.andriod.movies.entity.Movie

object MyViewModel {
    val movies = MutableLiveData<MutableMap<String, Movie>>()

    fun getData(){

    }
}