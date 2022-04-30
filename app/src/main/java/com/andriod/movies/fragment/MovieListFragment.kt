package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.MovieListView
import com.andriod.movies.MyViewModel
import com.andriod.movies.R
import com.andriod.movies.databinding.FragmentMovieListBinding
import com.andriod.movies.entity.Movie
import java.util.*

class MovieListFragment : Fragment(), MovieListView.OnItemClickListener {
    private var _binding: FragmentMovieListBinding? = null
    private val binding: FragmentMovieListBinding get() = _binding!!

    private val contract: MovieListContract?
        get() = activity as MovieListContract?

    var showMode: ShowMode = ShowMode.LIST

    private val groups = mutableSetOf<String?>()
    private val lists = TreeSet<MovieListView>()
    private var groupByField: GroupBy = MyViewModel.groupBy.value ?: GroupBy.TYPE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureContent()
    }

    private fun configureContent() {
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
        val isArray = when (groupByField) {
            GroupBy.TYPE -> false
            GroupBy.YEAR -> false
            GroupBy.GENRE -> true
        }
        list.forEach { movieItem ->

            val listOfValues =
                if (isArray) movieItem.listValue(groupByField)
                else listOf(movieItem.fieldValue(groupByField))

            if (listOfValues != null) {
                for (itemValue in listOfValues) {
                    if (!groups.contains(itemValue)) {
                        groups.add(itemValue)

                        lists.add(MovieListView(context,
                            itemValue,
                            this@MovieListFragment)
                        { movie ->
                            if (isArray) {
                                movie.listValue(groupByField)?.contains(itemValue!!) == true
                            } else {
                                movie.fieldValue(groupByField) == itemValue
                            }
                        }
                        )
                        binding.container.removeAllViews()
                        lists.forEach { movieListView -> binding.container.addView(movieListView) }
                    }
                }
            }
        }
        lists.forEach { movieListView -> movieListView.setData(list) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "@@ListFragment"

        enum class GroupBy(val id: Int) { TYPE(0), YEAR(1), GENRE(2) }

        private fun Movie.fieldValue(groupBy: GroupBy): String? = when (groupBy) {
            GroupBy.TYPE -> this._type
            GroupBy.YEAR -> this.year
            else -> null
        }

        private fun Movie.listValue(groupBy: GroupBy): List<String>? = when (groupBy) {
            GroupBy.GENRE -> this.genre
            else -> null
        }

        enum class ShowMode { LIST, FAVORITES, SEARCHING }
    }

    interface MovieListContract {
        fun changeMovie(movie: Movie)
        fun onMovieChanged(movie: Movie)
        fun setTitle(title: String)
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
