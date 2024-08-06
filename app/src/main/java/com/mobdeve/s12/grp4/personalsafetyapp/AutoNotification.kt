package com.mobdeve.s12.grp4.personalsafetyapp

data class AutoNotification(
    val id: Int,
    val userId: Int,
    val notificationText: String,
    val interval: Long,
    val timeUnit: String
)
