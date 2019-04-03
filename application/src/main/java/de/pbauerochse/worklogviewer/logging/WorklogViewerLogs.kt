package de.pbauerochse.worklogviewer.logging

object WorklogViewerLogs {

    private val pendingMessages = mutableListOf<String>()
    private val logMessageCache = LimitedLogMessageBuffer(3000)
    private val listeners = mutableListOf<LogMessageListener>(logMessageCache)

    @JvmStatic
    fun appendLogMessage(message: String) = pendingMessages.add(message)

    @JvmStatic
    fun setMaxLogLines(max: Int) = logMessageCache.setMaxLogLines(max)

    @JvmStatic
    fun addListener(listener: LogMessageListener) {
        if (listeners.contains(listener).not()) {
            listeners.add(listener)
        }
    }

    @JvmStatic
    fun removeListener(listener: LogMessageListener) = listeners.remove(listener)

    @JvmStatic
    fun getRecentLogMessages() : List<String> = logMessageCache.allMessages

    fun notifyListeners() {
        if (listeners.isNotEmpty() && pendingMessages.isNotEmpty()) {
            listeners.forEach { it.onLogMessage(pendingMessages.toList()) }
            pendingMessages.clear()
        }
    }

}