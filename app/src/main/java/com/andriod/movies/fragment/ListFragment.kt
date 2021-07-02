package com.andriod.movies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.MyViewModel
import com.andriod.movies.R
import com.andriod.movies.adapter.ListAdapter
import com.andriod.movies.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.inflate(layoutInflater)
        configureRecyclerView(view)
    }

    private fun configureRecyclerView(view: View) {
//        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ListAdapter()
        adapter.movies = MyViewModel.movies.value?.values?.toList() ?: ArrayList()
//        binding.recyclerView.adapter = adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

    }
}