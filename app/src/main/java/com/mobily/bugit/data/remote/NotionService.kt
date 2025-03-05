package com.mobily.bugit.data.remote

import retrofit2.Response
import retrofit2.http.*

interface NotionService {
    companion object {
        const val BASE_URL = "https://api.notion.com/"
    }

    @POST("v1/pages")
    suspend fun createPage(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Header("Content-Type") contentType: String = "application/json",
        @Body requestBody: NotionPageRequest
    ): Response<Map<String, Any>>

    @POST("v1/blocks/{block_id}/children")
    suspend fun appendBlock(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("block_id") blockId: String,
        @Body requestBody: NotionBlockRequest
    ): Response<Map<String, Any>>
}