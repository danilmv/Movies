package com.andriod.movies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriod.movies.MyViewModel
import com.andriod.movies.adapter.MovieListAdapter
import com.andriod.movies.databinding.FragmentListBinding

class MovieListFragment : Fragment() {
    private var binding: FragmentListBinding? = null
    private lateinit var adapterMovie: MovieListAdapter

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

        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = adapterMovie
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "@@ListFragment"
    }
}