package com.example.lamaoalpaga.Model;

public class Question {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private Animal answer;

    public Question(Animal answer) {
        this.answer = answer;
    }

    public Animal getAnswer() {
        return answer;
    }

    public void setAnswer(Animal answer) {
        this.answer = answer;
    }
}
