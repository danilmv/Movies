package com.andriod.movies.statusbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.andriod.movies.databinding.StatusBarViewBinding
import java.lang.StringBuilder


class StatusBarView : LinearLayout {

    private lateinit var binding: StatusBarViewBinding

    private lateinit var statuses: LiveData<Map<Int, String>>

    private val groupIndexes = HashMap<Int, Int>()

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        readAttributes(attrs)
        initView(context)
    }

    private fun readAttributes(attrs: AttributeSet?) {
//        val array = context.obtainStyledAttributes(attrs, R.styleable.StatusBar)
//        mCustomAttribute = array.getDimension(R.styleable.Status,30dp)
//        array.recycle()
    }

    fun setStatuses(statuses: LiveData<Map<Int, String>>) {
        this.statuses = statuses

        statuses.observe(context as LifecycleOwner) {
            Log.d(TAG, "statuses.observe called")

            if (it.isEmpty()) {
                binding.textView.text = ""
                binding.root.isVisible = false
            } else {
                binding.root.isVisible = true
                binding.textView.text = getStatusText(it)
            }
        }
    }

    private fun getStatusText(values: Map<Int, String>): String {
        val sb = StringBuilder()
        for (group in StatusManager.groups) {
            sb.append(getNextValueFromGroup(group, values))
        }
        return sb.toString()
    }

    private fun getNextValueFromGroup(
        group: MutableMap.MutableEntry<Int, MutableSet<Int>>,
        listOfValues: Map<Int, String>
    ): String? {
        val groupId = group.key
        val groupValues = group.value.toList()
        groupIndexes[groupId] = groupIndexes[groupId]?.plus(1)?.rem(groupValues.size) ?: 0
        return listOfValues[groupValues[groupIndexes[groupId]!!]]
    }

    private fun initView(context: Context?) {
        binding = StatusBarViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    companion object {
        const val TAG = "@@StatusBarView"
    }
}