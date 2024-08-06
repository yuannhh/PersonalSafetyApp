package com.mobdeve.s12.grp4.personalsafetyapp

data class Incident(
    val id: Int,
    val userId: Int,
    val type: String,
    val details: String,
    val location: String,
    val timestamp: String,
    val status: String
)
