package br.ufmg.engsoft.reprova.model.generator;

import br.ufmg.engsoft.reprova.model.Questionnaire;

public class GeneratorValuesChecker {
	public int checkEstimatedTimeAndReturnValidValue(int totalEstimatedTime) {
		if (totalEstimatedTime == 0) {
			return  Questionnaire.DEFAULT_ESTIMATED_TIME_MINUTES;
		}
		return totalEstimatedTime;
	}

	public int checkQuestionsCountAndReturnValidValue(int questionsCount) {
		if (questionsCount == 0) {
			return Questionnaire.DEFAULT_QUESTIONS_COUNT;
		}
		return questionsCount;
	}
}
