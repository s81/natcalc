package com.natcalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.natcalc.domain.model.Message
import com.natcalc.domain.model.MessageType

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userInput: String,
    val botResponse: String,
    val timestamp: Long,
    val type: String
)

fun MessageEntity.toDomain() = Message(
    id = id,
    userInput = userInput,
    botResponse = botResponse,
    timestamp = timestamp,
    type = runCatching { MessageType.valueOf(type) }.getOrDefault(MessageType.CALCULATION)
)

fun Message.toEntity() = MessageEntity(
    id = id,
    userInput = userInput,
    botResponse = botResponse,
    timestamp = timestamp,
    type = type.name
)
