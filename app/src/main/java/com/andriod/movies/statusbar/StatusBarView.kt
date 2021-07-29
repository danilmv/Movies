package com.andriod.movies.statusbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.andriod.movies.databinding.StatusBarViewBinding
import java.lang.Thread.sleep


class StatusBarView : LinearLayout {

    private lateinit var binding: StatusBarViewBinding

    private lateinit var statuses: LiveData<Map<Int, String>>

    private val groupIndexes = HashMap<Int, Int>()

    private lateinit var showDataThread: Thread

    private var lastTimeDataRefreshed: Long = 0

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
        if (values.isEmpty()) return ""

        val sb = mutableListOf<String?>()
        for (group in StatusManager.groups) {
            try {
                if (group.value.isNotEmpty()) sb.add(getNextValueFromGroup(group, values))
            } catch (e: Exception) {
            }
        }
        return sb.joinToString("\n")
    }

    private fun getNextValueFromGroup(
        group: MutableMap.MutableEntry<Int, MutableSet<Int>>,
        listOfValues: Map<Int, String>,
    ): String? {
        lastTimeDataRefreshed = System.currentTimeMillis()

        val groupId = group.key
        val groupValues = group.value.toList()
        groupIndexes[groupId] = groupIndexes[groupId]?.plus(1)?.rem(groupValues.size) ?: 0
        return listOfValues[groupValues[groupIndexes[groupId]!!]]
    }

    private fun initView(context: Context?) {
        binding = StatusBarViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun startShowDataThread() {
        showDataThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    sleep(200)
                } catch (e: InterruptedException) {
                    break
                }

                if (System.currentTimeMillis() - lastTimeDataRefreshed >= REFRESH_TIME) {
                    statuses.value?.let {
                        if (it.isNotEmpty()) {
                            binding.textView.post { binding.textView.text = getStatusText(it) }
                        }
                    }
                }
            }
        }.apply { isDaemon = true }
        showDataThread.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startShowDataThread()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        showDataThread.interrupt()
    }

    companion object {
        const val TAG = "@@StatusBarView"
        const val REFRESH_TIME: Long = 1000
    }
}