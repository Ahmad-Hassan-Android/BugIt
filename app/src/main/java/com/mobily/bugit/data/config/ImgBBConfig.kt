package com.mobily.bugit.data.config

import android.content.Context
import com.mobily.bugit.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgBBConfig @Inject constructor(
    private val context: Context
) {
    val apiKey: String
        get() = context.getString(R.string.imgbb_api_key)

    fun validate() {
        require(apiKey.isNotBlank()) { "ImgBB API key is not configured" }
    }
} 