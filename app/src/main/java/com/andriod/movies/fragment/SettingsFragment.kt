package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.andriod.movies.MyViewModel
import com.andriod.movies.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val contract: SettingsContract?
        get() = activity as SettingsContract?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerValues = MovieListFragment.Companion.GroupBy.values()
        val adapter =
            ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGroupBy.adapter = adapter

        val groupBy = MyViewModel.groupBy.value?.id ?: 0
        binding.spinnerGroupBy.setSelection(groupBy)

        binding.spinnerGroupBy.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                ) {
                    MyViewModel.groupBy.value =
                        MovieListFragment.Companion.GroupBy.values()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        binding.buttonStartService.setOnClickListener { contract?.onStartService() }
        binding.buttonStartLoading.setOnClickListener { contract?.onStartLoading() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    interface SettingsContract{
        fun onStartService()
        fun onStartLoading()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is SettingsContract) { "Activity must implement SettingsContract" }
    }
}