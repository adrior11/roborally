package com.github.adrior.roborally.utility;

import com.github.adrior.roborally.exceptions.ConfigurationLoadException;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Singleton class for loading and providing configuration settings.
 *
 * <p> The configuration is loaded once and can be accessed globally throughout the application.
 * The class uses Gson for JSON deserialization and Lombok for getter methods.
 */
@Getter
public final class Config {
    private String protocolVersion;
    private String[] groups;
    private boolean isCheatsEnabled;
    private boolean isMusicEnabled;
    private boolean isSavingLog;

    private static final AtomicReference<Config> instance = new AtomicReference<>(); // Thread-safe instance

    // Private constructor to prevent instantiation.
    private Config() {}

    /**
     * Retrieves the singleton instance of the Config class.
     *
     * <p> This method ensures that the configuration is loaded only once using double-checked locking
     * for thread safety. If the configuration has not been loaded yet, it triggers the loading
     * process.
     *
     * @return the singleton instance of the Config class
     */
    @SneakyThrows
    public static Config getInstance() {
        if (null == instance.get()) {
            synchronized (Config.class) {
                if (null == instance.get()) {
                    loadConfig(ResourceFileUtil.getAbsolutePathFromResourcePath("/config.json"));
                }
            }
        }
        return instance.get();
    }

    /**
     * Loads the configuration from the specified JSON file.
     *
     * <p> This method uses Gson to deserialize the JSON data into a temporary Config object, then
     * sets the fields of the singleton Config instance based on the values from the temporary
     * object. If the file cannot be found or read, it logs the error and throws a runtime exception.
     *
     * @param filePath the path to the JSON configuration file
     */
    private static void loadConfig(@NonNull String filePath) {
        ServerCommunicationFacade.log("Loading config from " + filePath);
        try (FileReader reader = new FileReader(filePath)) {
            Config tempConfig = GsonUtil.getGson().fromJson(reader, Config.class);

            // Set the instance fields from the temporary Config object
            Config newInstance = new Config();

            newInstance.protocolVersion = tempConfig.getProtocolVersion();
            newInstance.groups = tempConfig.getGroups();
            newInstance.isCheatsEnabled = tempConfig.isCheatsEnabled();
            newInstance.isMusicEnabled = tempConfig.isMusicEnabled();
            newInstance.isSavingLog = tempConfig.isSavingLog();

            instance.set(newInstance);
        } catch (IOException e) {
            ServerCommunicationFacade.log("Failed to load configuration: " + e.getMessage());
            throw new ConfigurationLoadException("Failed to load configuration: " + e.getMessage(), e);
        }
    }
}
