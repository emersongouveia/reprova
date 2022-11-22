package br.ufmg.engsoft.reprova.model.generator;

import java.util.ArrayList;
import java.util.Collections;

import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.model.Questionnaire;
import br.ufmg.engsoft.reprova.database.QuestionsDAO;

public class DefaultGenerator implements IQuestionnaireGenerator{

  public Questionnaire generate(QuestionsDAO questionsDAO, String averageDifficulty, int questionsCount, int totalEstimatedTime){
	  GeneratorValuesChecker checker = new GeneratorValuesChecker();
	  
    totalEstimatedTime = checker.checkEstimatedTimeAndReturnValidValue(totalEstimatedTime);
	questionsCount = checker.checkQuestionsCountAndReturnValidValue(questionsCount);

    ArrayList<Question> questions = new ArrayList<Question>();
    ArrayList<Question> allQuestions = new ArrayList<Question>(questionsDAO.list(null, null));

    Collections.shuffle(allQuestions);
    for (int i = 0; i < questionsCount; i++){
      if (i >=  allQuestions.size()){
        break;
      }

      questions.add(allQuestions.get(i));
    }

    return new Questionnaire.Builder()
                .averageDifficulty(averageDifficulty)
                .totalEstimatedTime(totalEstimatedTime)
                .questions(questions)
                .build();
  };
}