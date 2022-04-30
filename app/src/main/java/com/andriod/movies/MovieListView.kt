package com.andriod.movies

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.adapter.MovieListAdapter
import com.andriod.movies.databinding.MovieListViewBinding
import com.andriod.movies.entity.Movie


typealias MyPredicate = (Movie) -> Boolean

class MovieListView : LinearLayout, MovieListAdapter.OnItemClickListener, Comparable<MovieListView> {
    private lateinit var binding: MovieListViewBinding
    private lateinit var adapterMovie: MovieListAdapter
    lateinit var title: String
    private var filter: MyPredicate = { false }
    private var listener: OnItemClickListener? = null

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

    constructor(
        context: Context?,
        title: String?,
        listener: OnItemClickListener,
        filter: MyPredicate,
    ) : super(context) {
        this.title = title ?: "?"
        this.filter = filter
        this.listener = listener

        initView(context)
    }

    private fun initView(context: Context?) {
        binding = MovieListViewBinding.inflate(LayoutInflater.from(context), this, true)

        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        adapterMovie = MovieListAdapter()
        adapterMovie.listener = this

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.adapter = adapterMovie

        binding.textViewHeader.text = title
    }

    fun setData(movies: List<Movie>) {
        adapterMovie.movies = movies.filter(filter)
        binding.textViewHeader.isVisible = adapterMovie.movies.isNotEmpty()
    }

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
        fun onFavoriteChanged(movie: Movie)
    }

    override fun onItemClick(movie: Movie) {
        listener?.onItemClick(movie)
    }

    override fun onFavoriteChanged(movie: Movie) {
        listener?.onFavoriteChanged(movie)
    }

    override fun compareTo(other: MovieListView): Int {
        return  title.compareTo(other.title)
    }
}