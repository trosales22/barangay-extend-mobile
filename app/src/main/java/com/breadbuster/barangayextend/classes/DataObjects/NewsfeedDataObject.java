package com.breadbuster.barangayextend.classes.DataObjects;

public class NewsfeedDataObject {
    String topicPostID,topicPosterUserID,topic_postedBy,topic_postType,topic_dateAndTimePosted,topic_locationAddress,topicTitle,
            topicDescription,topicPostedBy_profilePicture,topicImage,topicPosterUserBarangay,topicPosterUserType;

    public NewsfeedDataObject(String topicPostID, String topicPosterUserID, String topic_postedBy, String topic_postType, String topic_dateAndTimePosted, String topic_locationAddress, String topicTitle, String topicDescription, String topicPostedBy_profilePicture, String topicImage, String topicPosterUserBarangay, String topicPosterUserType) {
        this.topicPostID = topicPostID;
        this.topicPosterUserID = topicPosterUserID;
        this.topic_postedBy = topic_postedBy;
        this.topic_postType = topic_postType;
        this.topic_dateAndTimePosted = topic_dateAndTimePosted;
        this.topic_locationAddress = topic_locationAddress;
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.topicPostedBy_profilePicture = topicPostedBy_profilePicture;
        this.topicImage = topicImage;
        this.topicPosterUserBarangay = topicPosterUserBarangay;
        this.topicPosterUserType = topicPosterUserType;
    }

    public String getTopicPostID() {
        return topicPostID;
    }

    public String getTopicPosterUserID() {
        return topicPosterUserID;
    }

    public String getTopic_postedBy() {
        return topic_postedBy;
    }

    public String getTopic_postType() {
        return topic_postType;
    }

    public String getTopic_dateAndTimePosted() {
        return topic_dateAndTimePosted;
    }

    public String getTopic_locationAddress() {
        return topic_locationAddress;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public String getTopicPostedBy_profilePicture() {
        return topicPostedBy_profilePicture;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public String getTopicPosterUserBarangay() {
        return topicPosterUserBarangay;
    }

    public String getTopicPosterUserType() {
        return topicPosterUserType;
    }
}
