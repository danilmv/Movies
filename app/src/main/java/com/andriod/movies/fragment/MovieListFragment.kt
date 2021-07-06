package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.MyViewModel
import com.andriod.movies.adapter.MovieListAdapter
import com.andriod.movies.databinding.FragmentListBinding
import com.andriod.movies.entity.Movie

class MovieListFragment : Fragment(), MovieListAdapter.OnItemClickListener {
    private var binding: FragmentListBinding? = null
    private lateinit var adapterMovie: MovieListAdapter

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
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        adapterMovie = MovieListAdapter()
        MyViewModel.movies.observe(viewLifecycleOwner) {
            Log.d(TAG, "configureRecyclerView():observation called: size= ${it.values.size}")
            adapterMovie.movies = it.values.toList()
        }
        adapterMovie.listener = this

        binding?.recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding?.recyclerView?.adapter = adapterMovie

        binding?.textViewHeader?.text = "Movies"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "@@ListFragment"
    }

    interface MovieListContract {
        fun changeMovie(movie: Movie)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MovieListContract) { "Activity must implement MovieListContract" }
    }

    override fun onItemClick(movie: Movie) {
        contract?.changeMovie(movie)
    }
}