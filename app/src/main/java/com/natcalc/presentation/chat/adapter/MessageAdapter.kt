package com.natcalc.presentation.chat.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.natcalc.databinding.ItemMessageBotBinding
import com.natcalc.databinding.ItemMessageUserBinding
import com.natcalc.domain.model.Message

sealed class ChatRow {
    data class UserRow(val text: String) : ChatRow()
    data class BotRow(val text: String) : ChatRow()
}

fun List<Message>.toChatRows(): List<ChatRow> = flatMap { msg ->
    listOf(ChatRow.UserRow(msg.userInput), ChatRow.BotRow(msg.botResponse))
}

class MessageAdapter : ListAdapter<ChatRow, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT = 1

        private val DIFF = object : DiffUtil.ItemCallback<ChatRow>() {
            override fun areItemsTheSame(old: ChatRow, new: ChatRow) = old == new
            override fun areContentsTheSame(old: ChatRow, new: ChatRow) = old == new
        }
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position) is ChatRow.UserRow) TYPE_USER else TYPE_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER)
            UserViewHolder(ItemMessageUserBinding.inflate(inflater, parent, false))
        else
            BotViewHolder(ItemMessageBotBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is ChatRow.UserRow -> (holder as UserViewHolder).bind(row.text)
            is ChatRow.BotRow  -> (holder as BotViewHolder).bind(row.text)
        }
    }
}

class UserViewHolder(private val b: ItemMessageUserBinding) : RecyclerView.ViewHolder(b.root) {
    fun bind(text: String) { b.tvUserMessage.text = text }
}

class BotViewHolder(private val b: ItemMessageBotBinding) : RecyclerView.ViewHolder(b.root) {
    fun bind(text: String) {
        b.tvBotMessage.text = text
        b.tvBotMessage.gravity =
            if (text.any { it in '؀'..'ۿ' }) Gravity.END else Gravity.START
    }
}
