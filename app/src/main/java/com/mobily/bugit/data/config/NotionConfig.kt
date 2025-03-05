package com.mobily.bugit.data.config

import android.content.Context
import java.util.Properties
import javax.inject.Inject

class NotionConfig @Inject constructor(
    private val context: Context
) {
    private val properties: Properties by lazy {
        Properties().apply {
            context.assets.open("config.properties").use {
                load(it)
            }
        }
    }

    val apiKey: String
        get() {
            val key = properties.getProperty("NOTION_API_KEY")
                ?: throw IllegalStateException("NOTION_API_KEY not found in config.properties")
            require(key.startsWith("ntn_")) { "Invalid Notion API key format" }
            return key
        }

    val databaseId: String
        get() {
            val id = properties.getProperty("NOTION_DATABASE_ID")
                ?: throw IllegalStateException("NOTION_DATABASE_ID not found in config.properties")
            require(id.matches(Regex("[a-f0-9]{32}"))) { "Invalid Notion database ID format" }
            return id
        }

    fun validate() {
        // Access both properties to trigger validation
        apiKey
        databaseId
    }
}