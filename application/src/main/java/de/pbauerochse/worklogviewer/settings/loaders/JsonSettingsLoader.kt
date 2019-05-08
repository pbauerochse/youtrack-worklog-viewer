package de.pbauerochse.worklogviewer.settings.loaders

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pbauerochse.worklogviewer.settings.Settings
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import org.slf4j.LoggerFactory
import java.io.*

/**
 * New loader, that reads the settings
 * from a JSON file
 */
class JsonSettingsLoader(private val jsonFile: File) {

    companion object {
        private val LOG = LoggerFactory.getLogger(JsonSettingsLoader::class.java)
        private val MAPPER = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    }

    fun load(): Settings {
        if (!jsonFile.exists()) {
            return Settings()
        }

        try {
            LOG.debug("Loading settings from {}", jsonFile.absolutePath)
            return MAPPER.readValue(jsonFile, Settings::class.java)
        } catch (e: IOException) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.read", e, jsonFile.absolutePath)
        }

    }

    fun save(settings: Settings) {
        LOG.debug("Saving settings to {}", jsonFile.absolutePath)
        ensureFileExists()

        try {
            OutputStreamWriter(FileOutputStream(jsonFile)).use { writer -> write(settings, writer) }
        } catch (e: IOException) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.write", e, jsonFile.absolutePath)
        }

    }

    private fun ensureFileExists() {
        try {
            val jsonFileDirectory = jsonFile.parentFile
            if (!jsonFileDirectory.exists() && !jsonFileDirectory.mkdirs()) {
                throw IOException("Could not create directory " + jsonFileDirectory.absolutePath)
            }

            if (!jsonFile.exists() && !jsonFile.createNewFile()) {
                throw IOException("Could not create settings file " + jsonFile.absolutePath)
            }
        } catch (e: IOException) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.create", e, jsonFile.absolutePath)
        }

    }

    internal fun write(settings: Settings, writer: Writer) {
        try {
            MAPPER.writeValue(writer, settings)
        } catch (e: IOException) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.write", e, jsonFile.absolutePath)
        }

    }

}
