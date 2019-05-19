package de.pbauerochse.worklogviewer.connector.workitem

class AddWorkItemResult private constructor(
    val success: Boolean,
    val errorMessage: String? = null,
    val worklogItem: MinimalWorklogItem? = null
) {

    companion object {
        @JvmStatic
        fun error(message: String): AddWorkItemResult {
            return AddWorkItemResult(success = false, errorMessage = message)
        }

        @JvmStatic
        fun success(worklogItem: MinimalWorklogItem): AddWorkItemResult {
            return AddWorkItemResult(success = true, worklogItem = worklogItem)
        }
    }

}