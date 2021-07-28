package com.andriod.movies.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.MovieListView
import com.andriod.movies.MyViewModel
import com.andriod.movies.R
import com.andriod.movies.databinding.FragmentMovieListBinding
import com.andriod.movies.entity.Movie
import kotlinx.android.synthetic.main.movie_list_view.view.*
import java.util.*

class MovieListFragment : Fragment(), MovieListView.OnItemClickListener {
    private var _binding: FragmentMovieListBinding? = null
    private val binding: FragmentMovieListBinding get() = _binding!!

    private val contract: MovieListContract?
        get() = activity as MovieListContract?

    var showMode: ShowMode = ShowMode.LIST
        set(value) {
            field = value
            configureContent()
        }

    private var isViewCreated = false

    private val groups = mutableSetOf<String?>()
    private val lists = TreeSet<MovieListView>()
    private var groupByField: GroupBy = MyViewModel.groupBy.value ?: GroupBy.TYPE

    private val listOfId = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,
            "onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)
        view.isSaveEnabled = true
        configureContent()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        check(context is MovieListContract) { "Activity must implement MovieListContract" }
        isViewCreated = true
    }

    override fun onDetach() {
        super.onDetach()
        isViewCreated = false
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState() called with: outState = $outState")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored() called with: savedInstanceState = $savedInstanceState")
        super.onViewStateRestored(savedInstanceState)
    }

    private fun configureContent() {
        if (!isViewCreated) return

        groups.clear()
        lists.clear()

        contract?.onModeChange(showMode)

        when (showMode) {
            ShowMode.LIST -> contract?.setTitle(getString(R.string.title_list))
            ShowMode.FAVORITES -> contract?.setTitle(getString(R.string.title_favorites))
            ShowMode.SEARCHING -> contract?.setTitle(getString(R.string.title_search))
        }

        MyViewModel.groupBy.observe(viewLifecycleOwner) {
            groupByField = it
        }

        if (showMode == ShowMode.SEARCHING) {
            MyViewModel.searchResults.observe(viewLifecycleOwner) {
                showData(it.values.toList())
            }
        } else {
            MyViewModel.movies.observe(viewLifecycleOwner) {
                val list = when (showMode) {
                    ShowMode.LIST -> it.values.toList()
                    ShowMode.FAVORITES -> it.values.toList().filter { movie -> movie.isFavorite }
                    ShowMode.SEARCHING -> return@observe
                }
                showData(list)
            }
        }
    }

    private fun showData(list: List<Movie>) {
        list.forEach { movieItem ->

            val listOfValues =
                if (groupByField.isList) movieItem.listValue(groupByField)
                else listOf(movieItem.fieldValue(groupByField))

            if (listOfValues != null) {
                for (itemValue in listOfValues) {
                    if (!groups.contains(itemValue)) {
                        groups.add(itemValue)

                        lists.add(
                            MovieListView(context, itemValue, this@MovieListFragment)
                            { movie ->
                                if (groupByField.isList) {
                                    movie.listValue(groupByField)?.contains(itemValue!!) == true
                                } else {
                                    movie.fieldValue(groupByField) == itemValue
                                }
                            }.apply { id = getViewIdByTitle(itemValue ?: "")
                            isSaveEnabled = true}
                        )
                    }
                }
            }
        }
        Log.d(TAG, "showData() >> listViews added")
        binding.container.removeAllViews()
        lists.forEach { movieListView ->
            binding.container.addView(movieListView)
            movieListView.setData(list)
        }
    }

    private fun getViewIdByTitle(title: String): Int {
        return listOfId[title] ?: View.generateViewId().also { listOfId[title] = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "@@MovieListFragment"

        enum class GroupBy(
            val id: Int,
            val isList: Boolean = false,
            val isInverse: Boolean = false,
        ) {
            TYPE(0),
            YEAR(1, isInverse = true),
            GENRE(2, true),
            LISTS(3, true),
            RATING(4, isInverse = true)
        }

        private fun Movie.fieldValue(groupBy: GroupBy): String? = when (groupBy) {
            GroupBy.TYPE -> this._type
            GroupBy.YEAR -> this.year
            GroupBy.RATING -> this.rating
            else -> null
        }

        private fun Movie.listValue(groupBy: GroupBy): List<String>? = when (groupBy) {
            GroupBy.GENRE -> this.genre
            GroupBy.LISTS -> this.lists
            else -> null
        }

        enum class ShowMode { LIST, FAVORITES, SEARCHING }
    }

    interface MovieListContract {
        fun changeMovie(movie: Movie)
        fun onMovieChanged(movie: Movie)
        fun setTitle(title: String)
        fun onModeChange(mode: ShowMode)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MovieListContract) { "Activity must implement MovieListContract" }
    }

    override fun onItemClick(movie: Movie) {
        contract?.changeMovie(movie)
    }

    override fun onFavoriteChanged(movie: Movie) {
        contract?.onMovieChanged(movie)
    }
}
