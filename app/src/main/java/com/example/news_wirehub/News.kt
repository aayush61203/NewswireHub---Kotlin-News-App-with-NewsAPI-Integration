package com.example.news_wirehub

import java.text.SimpleDateFormat
import java.util.*


data class News(
    val author: String,
    val title: String,
    val url: String,
    val imageUrl: String,
    var publishedAt: String,
    val source: String
) {
    // Function to convert the date format
    fun formattedDate(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(publishedAt)
        return outputFormat.format(date!!)
    }
}