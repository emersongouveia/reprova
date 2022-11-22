package br.ufmg.engsoft.reprova.model.difficulty;

import java.util.List;
import java.util.Arrays;

public class DifficultyGroup3 implements IDifficultyGroup{

  public int getDifficultyGroup(double avg){
    if (avg < 33.3) {return 0;}
    else if (avg < 66.6) {return 1;}
    else {return 2;}
  };

  public List<String> getDifficulties(){
    String[] group = {"Hard", "Average", "Easy"};
    return Arrays.asList(group);
  };
}