package br.ufmg.engsoft.reprova.model.difficulty;

import java.util.List;
import java.util.Arrays;

public class DifficultyGroup5 implements IDifficultyGroup{

  public int getDifficultyGroup(double avg){
    if (avg < 20) {return 0;}
    else if (avg < 40) {return 1;}
    else if (avg < 60) {return 2;}
    else if (avg < 80) {return 3;}
    return 4;
  };

  public List<String> getDifficulties(){
    String[] group = {"Very Hard", "Hard", "Average", "Easy", "Very Easy"};
    return Arrays.asList(group);
  };
}