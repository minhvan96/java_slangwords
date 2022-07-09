package main.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SlangGuessDefinitionPuzzle {
    public SlangWordEntity answer;
    Collection<String> wrongAnswers;
    private String answerByWord;
    public SlangGuessDefinitionPuzzle(){
        wrongAnswers = new ArrayList<>();
    }
    public SlangGuessDefinitionPuzzle(SlangWordEntity answer, Collection<String> wrongAnswers){
        this.answer = answer;
        this.wrongAnswers = wrongAnswers;
    }
    public void addWrongAnswer(String wrongAnswer){
        wrongAnswers.add(wrongAnswer);
    }

    public void setAnswer(SlangWordEntity answer) {
        this.answer = answer;
    }
    public void printPuzzle(){

        ArrayList answers = (ArrayList<String>)wrongAnswers;
        answers.add(answer.getMeaning());
        Collections.shuffle(answers);
        if(answer.getMeaning() == answers.get(0))
            answerByWord = "A";
        else if(answer.getMeaning() == answers.get(1))
            answerByWord = "B";
        else if(answer.getMeaning() == answers.get(2))
            answerByWord = "C";
        else if(answer.getMeaning() == answers.get(3))
            answerByWord = "D";
        System.out.println("A: " + answers.get(0));
        System.out.println("B: " + answers.get(1));
        System.out.println("C: " + answers.get(2));
        System.out.println("D: " + answers.get(3));
    }
    public String getAnswerByWord(){
        return answerByWord;
    }
}
