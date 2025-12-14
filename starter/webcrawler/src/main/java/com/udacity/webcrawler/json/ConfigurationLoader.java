package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() throws Exception {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return read(reader);
    }
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    // This prevents the Jackson library from closing the input Reader (which is closed in ConfigurationLoader.load()).
    objectMapper.disable(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE);

    return objectMapper.readValue(reader, CrawlerConfiguration.class);
  }
}
