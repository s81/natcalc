package com.natcalc.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.natcalc.databinding.ItemHistoryBinding
import com.natcalc.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<Message, HistoryAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(old: Message, new: Message) = old.id == new.id
            override fun areContentsTheSame(old: Message, new: Message) = old == new
        }
        private val DATE_FMT = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    }

    inner class ViewHolder(private val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(message: Message) {
            b.tvHistoryInput.text = message.userInput
            b.tvHistoryResult.text = message.botResponse
            b.tvHistoryTime.text = DATE_FMT.format(Date(message.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
