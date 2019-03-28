package de.pbauerochse.worklogviewer.plugin

data class FileChooserSpec(
    val title : String,
    val initialFileName : String,
    val fileType : FileType?
)

data class FileType(
    val description : String,
    val extension : String
)