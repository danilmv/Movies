package com.andriod.movies.statusbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriod.movies.databinding.FragmentStatusConsoleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StatusConsoleFragment : BottomSheetDialogFragment() {
    var _binding: FragmentStatusConsoleBinding? = null
    val binding: FragmentStatusConsoleBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatusConsoleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = StatusConsoleAdapter()
        binding.recyclerView.adapter = adapter

        StatusManager.history.observe(viewLifecycleOwner){
            adapter.data = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}