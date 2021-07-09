package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.MovieListView
import com.andriod.movies.MyViewModel
import com.andriod.movies.databinding.FragmentListBinding
import com.andriod.movies.entity.Movie
import java.util.*

class MovieListFragment : Fragment(), MovieListView.OnItemClickListener {
    private var binding: FragmentListBinding? = null
    var showFavorites = false

    private val contract: MovieListContract?
        get() = activity as MovieListContract?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureContent()
    }

    private fun configureContent() {
        val groups = mutableSetOf<String?>()
        val lists = TreeSet<MovieListView>()
        var groupBy: GroupBy = GroupBy.TYPE

        MyViewModel.groupBy.observe(viewLifecycleOwner) {
            groupBy = it
        }

        MyViewModel.movies.observe(viewLifecycleOwner) {
            Log.d(TAG, "configureRecyclerView():observation called: size= ${it.values.size}")
            val list = it.values.toList()
            list.forEach { movieItem ->
                if (!groups.contains(movieItem.fieldValue(groupBy))) {
                    groups.add(movieItem.fieldValue(groupBy))

                    lists.add(MovieListView(context,
                        movieItem.fieldValue(groupBy),
                        this@MovieListFragment)
                    { movie ->
                        movie.fieldValue(groupBy) == movieItem.fieldValue(groupBy)
                                && (movie.isFavorite || !showFavorites)
                    }
                    )

                    binding?.container?.removeAllViews()
                    lists.forEach { movieListView -> binding?.container?.addView(movieListView) }
                }
                lists.forEach { v -> v.setData(list) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "@@ListFragment"

        enum class GroupBy(val id: Int) { TYPE(0), YEAR(1), GENRE(2) }

        private fun Movie.fieldValue(groupBy: GroupBy) = when (groupBy) {
            GroupBy.TYPE -> this.type
            GroupBy.YEAR -> this.year
            GroupBy.GENRE -> this.genre
        }
    }

    interface MovieListContract {
        fun changeMovie(movie: Movie)
        fun onMovieChanged(movie: Movie)
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
