package org.whispersystems.textsecuregcm.storage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.appconfig.AmazonAppConfig;
import com.amazonaws.services.appconfig.AmazonAppConfigClient;
import com.amazonaws.services.appconfig.model.GetConfigurationRequest;
import com.amazonaws.services.appconfig.model.GetConfigurationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.WhisperServerConfiguration;
import org.whispersystems.textsecuregcm.configuration.dynamic.DynamicConfiguration;
import org.whispersystems.textsecuregcm.util.Util;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicConfigurationManager {

  private WhisperServerConfiguration clientConfiguration;

  private final AtomicReference<DynamicConfiguration>   configuration    = new AtomicReference<>();
  private final Logger                                  logger           = LoggerFactory.getLogger(DynamicConfigurationManager.class);

  private GetConfigurationResult lastConfigResult;

  private boolean initialized = false;

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .registerModule(new JavaTimeModule());

  public DynamicConfigurationManager(WhisperServerConfiguration config) {
    clientConfiguration = config;
  }

  public DynamicConfiguration getConfiguration() {
//    synchronized (this) {
//      while (!initialized) Util.wait(this);
//    }

    return configuration.get();
  }

  public void start() {
    DynamicConfiguration dynamicConfiguration = new DynamicConfiguration();

  configuration.set(dynamicConfiguration);
  }
}
