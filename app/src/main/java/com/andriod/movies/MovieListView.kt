package com.andriod.movies

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.adapter.MovieListAdapter
import com.andriod.movies.databinding.MovieListViewBinding
import com.andriod.movies.entity.Movie


typealias MyPredicate = (Movie) -> Boolean

class MovieListView : LinearLayout, MovieListAdapter.OnItemClickListener,
    Comparable<MovieListView> {
    private lateinit var binding: MovieListViewBinding
    private lateinit var adapterMovie: MovieListAdapter
    lateinit var title: String
    private var filter: MyPredicate = { false }
    private var listener: OnItemClickListener? = null

    var sortBy: SortBy = SortBy.SORT

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

        isSaveEnabled = true

        configureRecyclerView()
        configureSortBySpinner()
    }

    private fun configureSortBySpinner() {
        val spinnerValues = SortBy.values()
        val adapter =
            ArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortBySpinner.adapter = adapter

        binding.sortBySpinner.setSelection(sortBy.id)

        binding.sortBySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                ) {
                    sortBy = SortBy.values()[position]
                    resortData()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun resortData() {
        adapterMovie.movies = adapterMovie.movies.sort()
    }

    private fun List<Movie>.sort(): List<Movie> {
        return this.sortedWith { o1, o2 ->
            when (sortBy) {
                SortBy.SORT -> 0
                SortBy.RATING -> o1.rating?.let { o2.rating?.compareTo(it) }!!
                SortBy.YEAR -> o2.year.compareTo(o1.year)
                SortBy.TITLE -> o1.title.compareTo(o2.title)
            }
        }
    }

    private fun configureRecyclerView() {
        adapterMovie = MovieListAdapter()
        adapterMovie.listener = this
        adapterMovie.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.adapter = adapterMovie

        binding.textViewHeader.text = title
    }

    fun setData(movies: List<Movie>) {
        adapterMovie.movies = movies.filter(filter).sort()
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

    override fun compareTo(other: MovieListView): Int = when (MyViewModel.groupBy.value) {
        null -> 0
        else -> title.compareTo(other.title) * if (MyViewModel.groupBy.value!!.isInverse) -1 else 1
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        Log.d(TAG, "onAttachedToWindow() called")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "onDetachedFromWindow() called")
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d(TAG, "onSaveInstanceState() called")
        return Bundle().apply {
            putParcelable(SAVE_INSTANCE_KEY_SUPER, super.onSaveInstanceState())
            putString(SAVE_INSTANCE_KEY_SORTED_BY, sortBy.name)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG, "onRestoreInstanceState() called with: state = $state")
        var superState = state
        if (state != null && state is Bundle) {
            sortBy = SortBy.valueOf(
                state.getString(SAVE_INSTANCE_KEY_SORTED_BY) ?: SortBy.SORT.name)

            superState = state.getParcelable(SAVE_INSTANCE_KEY_SUPER)
        }
        super.onRestoreInstanceState(superState)
    }

    companion object {
        const val TAG = "@@MovieListView"
        const val SAVE_INSTANCE_KEY_SUPER = "super"
        const val SAVE_INSTANCE_KEY_SORTED_BY = "sorted_by"

        enum class SortBy(val id: Int) {
            SORT(0),
            RATING(1),
            YEAR(2),
            TITLE(2),
        }
    }
}
