package com.breadbuster.barangayextend.classes.DataObjects;

public class CommentsDataObject {
    private String commentID,postID,commentBy,commentByID,commentBy_profilePicture,comment,dateAndTimeCommented;

    public CommentsDataObject(String commentID, String postID, String commentBy, String commentByID, String commentBy_profilePicture, String comment, String dateAndTimeCommented) {
        this.commentID = commentID;
        this.postID = postID;
        this.commentBy = commentBy;
        this.commentByID = commentByID;
        this.commentBy_profilePicture = commentBy_profilePicture;
        this.comment = comment;
        this.dateAndTimeCommented = dateAndTimeCommented;
    }

    public String getCommentID() {
        return commentID;
    }

    public String getPostID() {
        return postID;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public String getCommentByID() {
        return commentByID;
    }

    public String getCommentBy_profilePicture() {
        return commentBy_profilePicture;
    }

    public String getComment() {
        return comment;
    }

    public String getDateAndTimeCommented() {
        return dateAndTimeCommented;
    }
}
