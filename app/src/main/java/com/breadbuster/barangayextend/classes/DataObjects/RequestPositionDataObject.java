package com.breadbuster.barangayextend.classes.DataObjects;

public class RequestPositionDataObject {
    String requesterUserID,requestedBy,requesterUserType,requester_profilePicture,requestedPosition,requestDateAndTimeRegistered,requestDateAndTimeConfirmed,requestStatus;

    public RequestPositionDataObject(String requesterUserID, String requestedBy, String requesterUserType, String requester_profilePicture, String requestedPosition, String requestDateAndTimeRegistered, String requestDateAndTimeConfirmed, String requestStatus) {
        this.requesterUserID = requesterUserID;
        this.requestedBy = requestedBy;
        this.requesterUserType = requesterUserType;
        this.requester_profilePicture = requester_profilePicture;
        this.requestedPosition = requestedPosition;
        this.requestDateAndTimeRegistered = requestDateAndTimeRegistered;
        this.requestDateAndTimeConfirmed = requestDateAndTimeConfirmed;
        this.requestStatus = requestStatus;
    }

    public String getRequesterUserID() {
        return requesterUserID;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getRequesterUserType() {
        return requesterUserType;
    }

    public String getRequester_profilePicture() {
        return requester_profilePicture;
    }

    public String getRequestedPosition() {
        return requestedPosition;
    }

    public String getRequestDateAndTimeRegistered() {
        return requestDateAndTimeRegistered;
    }

    public String getRequestDateAndTimeConfirmed() {
        return requestDateAndTimeConfirmed;
    }

    public String getRequestStatus() {
        return requestStatus;
    }
}
