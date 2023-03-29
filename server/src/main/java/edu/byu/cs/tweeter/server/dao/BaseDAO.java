package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class BaseDAO {
    protected AmazonDynamoDB client;
    protected DynamoDB dynamoDB;
    protected Table table;

    public void initializeDatabase() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDB = new DynamoDB(client);
    }

    public void switchTable(String tableName) {
        table = dynamoDB.getTable(tableName);
    }
}
