package com.mobily.bugit.data.remote

data class NotionPageRequest(
    val parent: Parent,
    val properties: Properties
)

data class Parent(
    val database_id: String
)

data class Properties(
    val Description: TextProperty,
    val Image: FilesProperty,
    val Status: StatusProperty,
    val Timestamp: TextProperty
)

data class TextProperty(
    val rich_text: List<TextContent>
)

data class FilesProperty(
    val files: List<FileContent>
)

data class FileContent(
    val name: String,
    val type: String = "external",
    val external: ExternalFile
)

data class ExternalFile(
    val url: String
)

data class StatusProperty(
    val status: Status
)

data class Status(
    val name: String
)

data class TextContent(
    val text: Text
)

data class Text(
    val content: String
)

data class NotionBlockRequest(
    val children: List<Block>
)

data class Block(
    val type: String,
    val paragraph: ParagraphBlock
)

data class ParagraphBlock(
    val rich_text: List<TextContent>
)