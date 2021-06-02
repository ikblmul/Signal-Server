package org.whispersystems.textsecuregcm.configuration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountsDynamoDbConfiguration extends DynamoDbConfiguration {

  @NotNull
  private String phoneNumberTableName;

  @NotEmpty
  @JsonProperty
  private String endpoint;

  @NotEmpty
  @JsonProperty
  private String accessKey;

  @NotEmpty
  @JsonProperty
  private String accessSecret;

  public String getPhoneNumberTableName() {
    return phoneNumberTableName;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public String getAccessSecret() {
    return accessSecret;
  }
}
