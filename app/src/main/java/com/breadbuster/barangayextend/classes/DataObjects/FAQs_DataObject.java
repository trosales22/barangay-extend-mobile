package com.breadbuster.barangayextend.classes.DataObjects;

public class FAQs_DataObject {
    String question,answer;

    public FAQs_DataObject(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
