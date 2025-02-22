package br.ufmg.engsoft.reprova.database;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.engsoft.reprova.mime.json.Json;
import br.ufmg.engsoft.reprova.model.Environments;
import br.ufmg.engsoft.reprova.model.Questionnaire;


/**
 * DAO for Questionnaire class on mongodb.
 */
public class QuestionnairesDAO {
  /**
   * Logger instance.
   */
  protected static final Logger logger = LoggerFactory.getLogger(QuestionnairesDAO.class);

  /**
   * Json formatter.
   */
  protected final Json json;

  /**
   * Questionnaires collection.
   */
  protected final MongoCollection<Document> collection;


  /**
   * Basic constructor.
   * @param db    the database, mustn't be null
   * @param json  the json formatter for the database's documents, mustn't be null
   * @throws IllegalArgumentException  if any parameter is null
   */
  public QuestionnairesDAO(Mongo db, Json json) {
    if (db == null)
      throw new IllegalArgumentException("db mustn't be null");

    if (json == null)
      throw new IllegalArgumentException("json mustn't be null");

    this.collection = db.getCollection("questionnaires");

    this.json = json;
  }



  /**
   * Parse the given document.
   * @param document  the question document, mustn't be null
   * @throws IllegalArgumentException  if any parameter is null
   * @throws IllegalArgumentException  if the given document is an invalid Questionnaire
   */
  protected Questionnaire parseDoc(Document document) {
    if (document == null)
      throw new IllegalArgumentException("document mustn't be null");

    var doc = document.toJson();

    logger.info("Fetched questionnaire: " + doc);

    try {
      var questionnaire = json
        .parse(doc, Questionnaire.Builder.class)
        .build();

      logger.info("Parsed questionnaire: " + questionnaire);

      return questionnaire;
    }
    catch (Exception e) {
      logger.error("Invalid document in database!", e);
      throw new IllegalArgumentException(e);
    }
  }


  /**
   * Get the questionnaire with the given id.
   * @param id  the questionnaire's id in the database.
   * @return The questionnaire, or null if no such questionnaire.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public Questionnaire get(String id) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");

    var questionnaire = this.collection
      .find(eq(new ObjectId(id)))
      .map(this::parseDoc)
      .first();

    if (questionnaire == null)
      logger.info("No such questionnaire " + id);

    return questionnaire;
  }

  /**
   * List all the questionnaires that match the given non-null parameters.
   * The questionnaire's statement is ommited.
   * @return The questionnaires in the collection that match the given parameters, possibly
   *         empty.
   * @throws IllegalArgumentException  if there is an invalid Questionnaire
   */
  public Collection<Questionnaire> list() {
    var doc = this.collection.find();

    var result = new ArrayList<Questionnaire>();

    doc.projection(fields())
      .map(this::parseDoc)
      .into(result);

    return result;
  }

  /**
   * Adds or updates the given questionnaire in the database.
   * If the given questionnaire has an id, update, otherwise add.
   * @param questionnaire  the questionnaire to be stored
   * @return Whether the questionnaire was successfully added.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public boolean add(Questionnaire questionnaire) {
    if (questionnaire == null){
      throw new IllegalArgumentException("questionnaire mustn't be null");
    }

    Document doc = configureQuestionnaireDocument(questionnaire);
	var id = questionnaire.id;
    if (id != null) {
      var result = this.collection.replaceOne(
        eq(new ObjectId(id)),
        doc
      );

      if (!result.wasAcknowledged()) {
        logger.warn("Failed to replace questionnaire " + id);
        return false;
      }
    }
    else
      this.collection.insertOne(doc);
    logger.info("Stored questionnaire " + doc.get("_id"));
    return true;
  }



private Document configureQuestionnaireDocument(Questionnaire questionnaire) {
	ArrayList<Document> questions = new ArrayList<Document>();
	GetDocumentWithQuestionsHandler handler = new GetDocumentWithQuestionsHandler();

	for (var question : questionnaire.questions) {
		Document doc = handler.configureDocumentQuestions(question);
		questions.add(doc);
	}
	Document doc = new Document().append("averageDifficulty", questionnaire.averageDifficulty).append("questions",
			questions);
	if (Environments.getInstance().getEnableEstimatedTime()) {
		doc = doc.append("totalEstimatedTime", questionnaire.totalEstimatedTime);
	}
	return doc;
}


  /**
   * Remove the questionnaire with the given id from the collection.
   * @param id  the questionnaire id
   * @return Whether the given questionnaire was removed.
   * @throws IllegalArgumentException  if any parameter is null
   */
  public boolean remove(String id) {
    if (id == null)
      throw new IllegalArgumentException("id mustn't be null");

    var result = this.collection.deleteOne(
      eq(new ObjectId(id))
    ).wasAcknowledged();

    if (result)
      logger.info("Deleted questionnaire " + id);
    else
      logger.warn("Failed to delete questionnaire " + id);

    return result;
  }
}
