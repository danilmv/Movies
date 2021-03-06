package com.andriod.movies.fragment

import android.app.Activity
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.movie_list_view.view.*
import java.util.*

class MovieListFragment : Fragment(), MovieListView.MovieListViewContract {
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
    private val viewsState = mutableMapOf<String, MovieListView.Companion.SortBy>()

    private val gson = Gson()

    private var configurationStarted = false

    private lateinit var refreshBackgroundThread: Thread
    private var lastTimeBackgroundRefreshed: Long = 0
    private val random = Random()

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
        view.isSaveEnabled = true
    }

    override fun onResume() {
        super.onResume()
        configureContent()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        check(context is MovieListContract) { "Activity must implement MovieListContract" }
        isViewCreated = true
    }

    private fun restoreState() {
        if (viewsState.isNotEmpty()) return
        val stringData: String = activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(SHARED_KEY_VIEWS_STATE, "") ?: ""
        if (stringData.isNotBlank()) {
            val setType =
                object : TypeToken<HashMap<String, MovieListView.Companion.SortBy>?>() {}.type
            viewsState.putAll(gson.fromJson(stringData, setType))
        }
    }

    override fun onDetach() {
        super.onDetach()
        isViewCreated = false
        _binding = null
    }

    private fun saveState() {
        val stringData: String = gson.toJson(viewsState)
        if (stringData.isNotBlank()) activity?.getPreferences(Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(SHARED_KEY_VIEWS_STATE, stringData)
            ?.apply()
    }

    private fun configureContent() {
        if (!isViewCreated) return
        if (configurationStarted) {
            return
        } else {
            configurationStarted = true
        }

        restoreState()

        groups.clear()
        lists.clear()

        contract?.onModeChange(showMode)

        setTitle()

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
                    else -> return@observe
                }
                showData(list)
            }
        }
        configurationStarted = false
    }

    fun setTitle() {
        when (showMode) {
            ShowMode.LIST -> contract?.setTitle(getString(R.string.title_list))
            ShowMode.FAVORITES -> contract?.setTitle(getString(R.string.title_favorites))
            ShowMode.SEARCHING -> contract?.setTitle(getString(R.string.title_search))
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

                        val stateId = "$itemValue${showMode.name}"
                        val sortBy = viewsState[stateId] ?: MovieListView.Companion.SortBy.UNSORTED
                            .also { viewsState[stateId] = it }

                        lists.add(
                            MovieListView(context, itemValue, this@MovieListFragment, sortBy)
                            { movie ->
                                if (groupByField.isList) {
                                    movie.listValue(groupByField)?.contains(itemValue!!) == true
                                } else {
                                    movie.fieldValue(groupByField) == itemValue
                                }
                            }
                        )
                    }
                }
            }
        }
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
        refreshBackgroundThread.interrupt()
    }

    companion object {
        private const val TAG = "@@MovieListFragment"
        private const val SHARED_KEY_VIEWS_STATE = "views state"
        private const val REFRESH_TIME: Long = 10_000

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
        fun onMassDetailsRequested(movies: List<Movie>)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MovieListContract) { "Activity must implement MovieListContract" }
        startThread()
    }

    override fun onItemClick(movie: Movie) {
        contract?.changeMovie(movie)
    }

    override fun onFavoriteChanged(movie: Movie) {
        contract?.onMovieChanged(movie)
    }

    override fun onStateChanged(
        title: String,
        sortBy: MovieListView.Companion.SortBy,
        movies: List<Movie>,
    ) {
        viewsState["$title${showMode.name}"] = sortBy
        saveState()
        if (sortBy == MovieListView.Companion.SortBy.REVENUE) {
            contract?.onMassDetailsRequested(movies)
        }
    }

    private fun startThread() {
        lastTimeBackgroundRefreshed = 0
        refreshBackgroundThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    break
                }

                if (System.currentTimeMillis() - lastTimeBackgroundRefreshed >= REFRESH_TIME) {
                    lastTimeBackgroundRefreshed = System.currentTimeMillis()
                    getNextPicture()?.let {
                        binding.imageViewListBackground.post {
                            Glide.with(binding.root)
                                .load(it)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(binding.imageViewListBackground)

                        }
                    }
                }
            }
        }.apply { isDaemon = true }
        refreshBackgroundThread.start()
    }

    private fun getNextPicture(): String? {
        if (MyViewModel.showFullscreenBackground.value != true) return null
        val size = MyViewModel.movies.value?.size ?: 0
        MyViewModel.movies.value?.let {
            val list = it.values.toList()
            for (i in 0 until size) {
                list[random.nextInt(size)].background?.let { picture -> return picture }
            }
        }
        return null
    }
}
