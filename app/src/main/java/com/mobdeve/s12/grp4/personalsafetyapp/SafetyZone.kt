package com.mobdeve.s12.grp4.personalsafetyapp

data class SafetyZone(
    val id: Int,
    val userId: Int,
    val name: String,
    val addressLine: String,
    val city: String,
    val state: String,
    val country: String
)
