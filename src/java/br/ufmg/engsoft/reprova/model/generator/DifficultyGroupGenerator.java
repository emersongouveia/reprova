package br.ufmg.engsoft.reprova.model.generator;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

import br.ufmg.engsoft.reprova.model.Environments;
import br.ufmg.engsoft.reprova.model.Question;
import br.ufmg.engsoft.reprova.model.Questionnaire;
import br.ufmg.engsoft.reprova.database.QuestionsDAO;
import br.ufmg.engsoft.reprova.model.difficulty.DifficultyFactory;

public class DifficultyGroupGenerator implements IQuestionnaireGenerator{

  /**
   * Auxiliary function that returns a list of questions with the given difficulty.
   * Attempts to fill the returned list with the given count of questions.
   */
  private List<Question> getQuestionsOfDifficulty(ArrayList<Question> allQuestions, int count, String difficulty){
    List<Question> questions = new ArrayList<Question>();
    List<Question> questionsOfDifficulty = allQuestions.stream()
                                        .filter(q -> q.difficulty.equals(difficulty))
                                        .collect(Collectors.toList());

    Collections.shuffle(questionsOfDifficulty);
    for (int i = 0; i < count; i++){
      if (i >= questionsOfDifficulty.size()){
        break;
      }
      questions.add(questionsOfDifficulty.get(i));
    }

    return questions;
  }

  /**
   * Generate a new Quesitonnaire based on the parameters.
   * Selects a collection of questions the best fit the parameters.
   * Calls the Questionnaire's Builder.
   */
  public Questionnaire generate(QuestionsDAO questionsDAO, String averageDifficulty, int questionsCount, int totalEstimatedTime){
    Environments environments = Environments.getInstance();
    int valueDifficultyGroup = environments.getDifficultyGroup();
    List<String> difficultyGroup = new DifficultyFactory()
                                .getDifficulty(valueDifficultyGroup)
                                .getDifficulties();
    GeneratorValuesChecker checker = new GeneratorValuesChecker();

    if (averageDifficulty == null){
      averageDifficulty = "Average";
    } else {
      if (!difficultyGroup.contains(averageDifficulty)){
        throw new IllegalArgumentException("invalid average difficulty");
      }
    }
    totalEstimatedTime = checker.checkEstimatedTimeAndReturnValidValue(totalEstimatedTime);
	questionsCount = checker.checkQuestionsCountAndReturnValidValue(questionsCount);
	ArrayList<Question> questions = new ArrayList<Question>();
    ArrayList<Question> allQuestions = new ArrayList<Question>(questionsDAO.list(null, null));

    if (allQuestions.size() <= questionsCount){
      for(Question question : allQuestions){
        questions.add(question);
      }
    } else {
      int averageQuestionsCount = (int)Math.ceil(questionsCount * 0.5);
      List<Question> averageQuestions = getQuestionsOfDifficulty(allQuestions, averageQuestionsCount, averageDifficulty);
      questions.addAll(averageQuestions);

      int remainingQuestionsCount = questionsCount - questions.size();
      int easierQuestionsCount = remainingQuestionsCount % 2 == 1 ? remainingQuestionsCount/2 + 1 : remainingQuestionsCount/2;
      int harderQuestionsCount = remainingQuestionsCount - easierQuestionsCount;
      int easierDifficultyIndex = difficultyGroup.indexOf(averageDifficulty);
      int harderDifficultyIndex = easierDifficultyIndex;

      while (remainingQuestionsCount > 0){
        checkIndexAndConfigureQuestionCounter(harderDifficultyIndex, harderQuestionsCount, easierQuestionsCount, -1, 0);
        checkIndexAndConfigureQuestionCounter(easierDifficultyIndex, easierQuestionsCount, harderQuestionsCount, 1, difficultyGroup.size()-1);

        questionsCounterSetter(allQuestions, harderQuestionsCount, difficultyGroup, harderDifficultyIndex, remainingQuestionsCount, questions);
        questionsCounterSetter(allQuestions, easierQuestionsCount, difficultyGroup, easierDifficultyIndex, remainingQuestionsCount, questions);
      }
    }

    return new Questionnaire.Builder()
                .averageDifficulty(averageDifficulty)
                .totalEstimatedTime(totalEstimatedTime)
                .questions(questions)
                .build();
  }

  private void checkIndexAndConfigureQuestionCounter(int selectedDifficultyIndex, int questionsCounter, int otherQuestionCounter,
		  int elseSumValue, int comparedValue) {
	  if (selectedDifficultyIndex == comparedValue){
		  otherQuestionCounter += questionsCounter;
		  questionsCounter = -1;
          selectedDifficultyIndex = -1;
        } else {
        	selectedDifficultyIndex = selectedDifficultyIndex + elseSumValue;
        }
	}

  private void questionsCounterSetter(ArrayList<Question> allQuestions, int questionsCounter, List<String> difficultyGroup,
		  int selectedDifficultyIndex, int remainingQuestionsCount, ArrayList<Question> questions) {
		if (questionsCounter != -1){
	          List<Question> selectedLevelQuestions = getQuestionsOfDifficulty(allQuestions, questionsCounter, difficultyGroup.get(selectedDifficultyIndex));
	          questionsCounter -= selectedLevelQuestions.size();
	          remainingQuestionsCount -= selectedLevelQuestions.size();
	          questions.addAll(selectedLevelQuestions);
	        }
	}
}