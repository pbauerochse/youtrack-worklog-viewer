package de.pbauerochse.worklogviewer.plugins.dialog

data class FileChooserSpecification(
    val title : String,
    val initialFileName : String,
    val fileType : FileType?
)

data class FileType(
    val description : String,
    val extension : String
)