package br.ufmg.engsoft.reprova.database;

import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;

import br.ufmg.engsoft.reprova.model.Environments;
import br.ufmg.engsoft.reprova.model.Question;

public class GetDocumentWithQuestionsHandler {
	public Document configureDocumentQuestions(Question question) {
		Map<String, Object> record = null;
		if (question.record != null) {
			record = question.record.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
		}
		Document doc = new Document().append("theme", question.theme).append("description", question.description)
				.append("statement", question.statement).append("record", record == null ? null : new Document(record))
				.append("pvt", question.pvt);
		if (Environments.getInstance().getEnableEstimatedTime()) {
			doc = doc.append("estimatedTime", question.estimatedTime);
		}
		if (Environments.getInstance().getDifficultyGroup() != 0) {
			doc = doc.append("difficulty", question.difficulty);
		}
		if (Environments.getInstance().getEnableMultipleChoice()) {
			doc = doc.append("choices", question.getChoices());
		}
		return doc;
	};
}
