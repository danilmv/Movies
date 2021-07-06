package com.andriod.movies

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.adapter.MovieListAdapter
import com.andriod.movies.databinding.MovieListViewBinding
import com.andriod.movies.entity.Movie


class MovieListView : LinearLayout {
    private lateinit var binding: MovieListViewBinding
    private lateinit var adapterMovie: MovieListAdapter

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context?) {
        binding = MovieListViewBinding.inflate(LayoutInflater.from(context), this, true)

        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        adapterMovie = MovieListAdapter()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.adapter = adapterMovie

        binding.textViewHeader.text = "Movies"
    }

    fun setData(movies: List<Movie>){
        adapterMovie.movies = movies
    }
}