package com.natcalc.domain.model

data class Message(
    val id: Long = 0,
    val userInput: String,
    val botResponse: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.CALCULATION
)
