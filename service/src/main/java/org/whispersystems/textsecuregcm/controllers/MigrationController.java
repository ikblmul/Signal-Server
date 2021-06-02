package org.whispersystems.textsecuregcm.controllers;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TimeToLiveSpecification;
import com.amazonaws.services.dynamodbv2.model.UpdateTimeToLiveRequest;
import org.whispersystems.textsecuregcm.storage.ReportMessageDynamoDb;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("v1/migration")
public class MigrationController {
  DynamoDB dynamoDB;
  public MigrationController(DynamoDB dynamoDB){
    this.dynamoDB = dynamoDB;
  }
  @GET
  @Path("migrate")
  public Response migrationCtrl(){
    for(var app: dynamoDB.listTables()){
      System.out.println("app is" + app.getTableName());

    }

    try{
      migrationReport();
      migrationKeys();
      migrationMessage();
    }catch (Exception e){
      return Response.status(400).build();
    };

    return Response.status(200).build();
  }

  private void migrationReport(){
    //table created now enabling TTL

    final String KEY_HASH = "H";
    final String ATTR_TTL = "E";
    final String table_name = "report";

    final CreateTableRequest createTableRequest = new CreateTableRequest()
        .withTableName(table_name)

        .withKeySchema(new KeySchemaElement(KEY_HASH, "HASH"))
        .withAttributeDefinitions(new AttributeDefinition(KEY_HASH, ScalarAttributeType.B))
        .withProvisionedThroughput(new ProvisionedThroughput(20L, 20L));

    dynamoDB.createTable(createTableRequest);


    final UpdateTimeToLiveRequest req = new UpdateTimeToLiveRequest();
    req.setTableName(table_name);

    final TimeToLiveSpecification ttlSpec = new TimeToLiveSpecification();
    ttlSpec.setAttributeName(ATTR_TTL);
    ttlSpec.setEnabled(true);
    req.withTimeToLiveSpecification(ttlSpec);


  }
  private void migrationKeys(){

    String KEY_ACCOUNT_UUID = "U";
    String KEY_DEVICE_ID_KEY_ID = "DK";
    String KEY_PUBLIC_KEY = "P";


    final CreateTableRequest createTableRequest = new CreateTableRequest()
        .withTableName("keys")
        .withKeySchema(new KeySchemaElement(KEY_ACCOUNT_UUID, "HASH"),
            new KeySchemaElement(KEY_DEVICE_ID_KEY_ID, "RANGE"))
        .withAttributeDefinitions(new AttributeDefinition(KEY_ACCOUNT_UUID, ScalarAttributeType.B),
            new AttributeDefinition(KEY_DEVICE_ID_KEY_ID, ScalarAttributeType.B))
        .withProvisionedThroughput(new ProvisionedThroughput(20L, 20L));

   dynamoDB.createTable(createTableRequest);
  }

  private void migrationMessage(){

    CreateTableRequest createTableRequest = new CreateTableRequest()
        .withTableName("message")
        .withKeySchema(new KeySchemaElement("H", "HASH"),
            new KeySchemaElement("S", "RANGE"))
        .withAttributeDefinitions(new AttributeDefinition("H", ScalarAttributeType.B),
            new AttributeDefinition("S", ScalarAttributeType.B),
            new AttributeDefinition("U", ScalarAttributeType.B))
        .withProvisionedThroughput(new ProvisionedThroughput(20L, 20L))
        .withLocalSecondaryIndexes(new LocalSecondaryIndex().withIndexName("Message_UUID_Index")
            .withKeySchema(new KeySchemaElement("H", "HASH"),
                new KeySchemaElement("U", "RANGE"))
            .withProjection(new Projection().withProjectionType(
                ProjectionType.KEYS_ONLY)));
    dynamoDB.createTable(createTableRequest);

  }
}
