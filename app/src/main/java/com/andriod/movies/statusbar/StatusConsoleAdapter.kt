package com.andriod.movies.statusbar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.R
import com.andriod.movies.databinding.ItemStatusConsoleBinding

class StatusConsoleAdapter : RecyclerView.Adapter<StatusConsoleAdapter.ViewHolder>() {

    var data: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_status_console, parent, false)) {

        private val binding = ItemStatusConsoleBinding.bind(itemView)

        fun bind(line: String) {
            binding.textView.text = line
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}