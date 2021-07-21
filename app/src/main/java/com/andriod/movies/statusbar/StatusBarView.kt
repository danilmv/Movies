package com.andriod.movies.statusbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.andriod.movies.databinding.MovieListViewBinding
import com.andriod.movies.databinding.StatusBarViewBinding

class StatusBarView : LinearLayout {

    private lateinit var binding: StatusBarViewBinding

    private lateinit var statuses: LiveData<MutableMap<Int, String>>

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        initView(context)
    }

    fun setStatuses(statuses: LiveData<MutableMap<Int, String>>){
        this.statuses = statuses

        statuses.observe(context as LifecycleOwner) {
            Log.d(TAG, "statuses.observe called")

            if (it.isEmpty()) {
                binding.progressBar.isVisible = false
                binding.textView.text = ""
            } else {
                binding.progressBar.isVisible = true
                binding.textView.text = it.values.joinToString(", ")
            }
        }
    }

    private fun initView(context: Context?) {
        binding = StatusBarViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    companion object{
        const val TAG = "@@StatusBarView"
    }
}