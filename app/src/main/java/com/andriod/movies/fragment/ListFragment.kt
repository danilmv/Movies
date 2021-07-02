package com.andriod.movies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriod.movies.MyViewModel
import com.andriod.movies.adapter.ListAdapter
import com.andriod.movies.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var binding: FragmentListBinding? = null
    private lateinit var adapter: ListAdapter

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
        adapter = ListAdapter()
        MyViewModel.movies.observe(viewLifecycleOwner) {
            Log.d(TAG, "configureRecyclerView():observation called: size= ${it.values.size}")
            adapter.movies = it.values.toList()
        }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "@@ListFragment"
    }
}