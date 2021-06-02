package org.whispersystems.textsecuregcm.storage;

import com.amazonaws.auth.policy.Principal;
import java.util.UUID;
//import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactAccount {

  private UUID uuid;
  private String number;

  public UUID getUuid() {
    return uuid;
  }

  ContactAccount(UUID uuid,String number){
    this.uuid = uuid;
    this.number = number;
  }

 public static ContactAccount ContactAccountFactory(UUID uuid,String number){
    return new ContactAccount(uuid,number);
  }

  public void setUuid(final UUID Uuid) {
    this.uuid = Uuid;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(final String number) {
    this.number = number;
  }

}
