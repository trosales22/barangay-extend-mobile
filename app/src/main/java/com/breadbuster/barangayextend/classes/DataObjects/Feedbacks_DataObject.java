package com.breadbuster.barangayextend.classes.DataObjects;

public class Feedbacks_DataObject {
    private String feedBackBy,feedbackBy_profilePicture,feedback,feedbackBy_dateAndTimeSubmitted;

    public Feedbacks_DataObject(String feedBackBy, String feedbackBy_profilePicture, String feedback, String feedbackBy_dateAndTimeSubmitted) {
        this.feedBackBy = feedBackBy;
        this.feedbackBy_profilePicture = feedbackBy_profilePicture;
        this.feedback = feedback;
        this.feedbackBy_dateAndTimeSubmitted = feedbackBy_dateAndTimeSubmitted;
    }

    public String getFeedBackBy() {
        return feedBackBy;
    }

    public String getFeedbackBy_profilePicture() {
        return feedbackBy_profilePicture;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getFeedbackBy_dateAndTimeSubmitted() {
        return feedbackBy_dateAndTimeSubmitted;
    }
}
