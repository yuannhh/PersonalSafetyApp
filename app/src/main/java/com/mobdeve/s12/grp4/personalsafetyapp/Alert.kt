package com.mobdeve.s12.grp4.personalsafetyapp

data class Alert(
    val id: Int,
    val name: String,
    val type: String,
    val userId: Int,
    val timestamp: String
)
