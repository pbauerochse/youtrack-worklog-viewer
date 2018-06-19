package de.pbauerochse.worklogviewer.settings.loaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * New loader, that reads the settings
 * from a JSON file
 */
public class JsonSettingsLoader {

    private static final Logger LOG = LoggerFactory.getLogger(JsonSettingsLoader.class);

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final File jsonFile;

    public JsonSettingsLoader(@NotNull File jsonFile) {
        this.jsonFile = jsonFile;
    }

    public Settings load() {
        if (!jsonFile.exists()) {
            return new Settings();
        }

        try {
            LOG.debug("Loading settings from {}", jsonFile.getAbsolutePath());
            return objectMapper.readValue(jsonFile, Settings.class);
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.read", e, jsonFile.getAbsolutePath());
        }
    }

    public void save(@NotNull Settings settings) {
        LOG.debug("Saving settings to {}", jsonFile.getAbsolutePath());
        ensureFileExists();

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile))) {
            write(settings, writer);
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.write", e, jsonFile.getAbsolutePath());
        }
    }

    private void ensureFileExists() {
        try {
            FileUtils.forceMkdirParent(jsonFile);
            FileUtils.touch(jsonFile);
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.create", e, jsonFile.getAbsolutePath());
        }
    }

    void write(@NotNull Settings settings, Writer writer) {
        try {
            objectMapper.writeValue(writer, settings);
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.write", e, jsonFile.getAbsolutePath());
        }
    }

}
