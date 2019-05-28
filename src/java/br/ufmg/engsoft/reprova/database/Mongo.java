package br.ufmg.engsoft.reprova.database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Mongo {
  protected static final String endpoint = "mongodb://localhost:27017";

  protected final MongoDatabase db;

  protected static Logger logger = LoggerFactory.getLogger(Mongo.class);



  public Mongo(String db) {
    this.db = MongoClients
      .create(Mongo.endpoint)
      .getDatabase(db);

    logger.info("connected to db '" + db + "'");
  }


  public MongoCollection<Document> getCollection(String name) {
    return db.getCollection(name);
  }
}