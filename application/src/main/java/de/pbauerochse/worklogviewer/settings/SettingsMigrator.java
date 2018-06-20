package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.settings.loaders.JsonSettingsLoader;
import de.pbauerochse.worklogviewer.settings.loaders.PropertiesSettingsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Migrates the old style settings properties file
 * to the new json file
 */
class SettingsMigrator {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsMigrator.class);
    private final File oldPropertiesFile;

    SettingsMigrator(File oldPropertiesFile) {
        this.oldPropertiesFile = oldPropertiesFile;
    }

    void migrateTo(File newSettingsJsonFile) {
        LOG.debug("Migrating old settings format from {} to {}", oldPropertiesFile.getAbsolutePath(), newSettingsJsonFile.getAbsolutePath());
        Settings fromProperties = new PropertiesSettingsLoader(oldPropertiesFile).load();
        new JsonSettingsLoader(newSettingsJsonFile).save(fromProperties);
        LOG.debug("Migration done");
    }
}
