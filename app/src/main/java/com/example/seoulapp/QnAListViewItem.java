package com.example.seoulapp;

public class QnAListViewItem {

    private String QnATitle;
    private String QnAUserNickname;
    private String QnAProduction;
    private String QnAQuestion;
    private String QnAAnswer;
    private String QnASize;
    private String QnACEO;
    private String QnAAnswerExist;

    public void setQnAAnswerExist(String answer) { this.QnAAnswerExist = answer; }

    public void setQnATitle(String title) { this.QnATitle = title; }

    public void setQnAUserNickname(String nickname) {
        this.QnAUserNickname = nickname;
    }

    public void setQnAProduction(String production) {
        this.QnAProduction = production;
    }

    public void setQnAQuestion(String question) {
        this.QnAQuestion = question;
    }

    public void setQnAAnswer(String answer) {
        this.QnAAnswer = answer;
    }

    public void setQnASize(String size) {
        this.QnASize = size;
    }

    public void setQnACEO(String ceo) {
        this.QnACEO = ceo;
    }

    public String getQnATitle() { return this.QnATitle; }

    public String getQnAUserNickname() {
        return this.QnAUserNickname;
    }

    public String getQnAProduction() {
        return this.QnAProduction;
    }

    public String getQnAQuestion() {
        return this.QnAQuestion;
    }

    public String getQnAAnswer() {
        return this.QnAAnswer;
    }

    public String getQnASize() {
        return this.QnASize;
    }

    public String getQnACEO() {
        return this.QnACEO;
    }

    public String getQnAAnswerExist() {
        return this.QnAAnswerExist;
    }
}
