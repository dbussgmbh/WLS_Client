package de.dbuss.wls_client;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
        private static final Properties props = new Properties();

        static {
            try (InputStream in = ConfigLoader.class.getClassLoader()
                    .getResourceAsStream("config.properties")) {
                if (in == null) {
                    throw new RuntimeException("⚠️ config.properties nicht gefunden!");
                }
                props.load(in);
            } catch (Exception e) {
                throw new RuntimeException("⚠️ Fehler beim Laden der Konfigurationsdatei", e);
            }
        }

        public static String get(String key) {
            return props.getProperty(key);
        }
}
