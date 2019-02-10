package com.breadbuster.barangayextend.classes.Beans;

public class CommentBean {
    private String commentID,postID,commentBy,commentByID,commentBy_profilePicture,comment,dateAndTimeCommented;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public String getCommentByID() {
        return commentByID;
    }

    public void setCommentByID(String commentByID) {
        this.commentByID = commentByID;
    }

    public String getCommentBy_profilePicture() {
        return commentBy_profilePicture;
    }

    public void setCommentBy_profilePicture(String commentBy_profilePicture) {
        this.commentBy_profilePicture = commentBy_profilePicture;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateAndTimeCommented() {
        return dateAndTimeCommented;
    }

    public void setDateAndTimeCommented(String dateAndTimeCommented) {
        this.dateAndTimeCommented = dateAndTimeCommented;
    }
}
