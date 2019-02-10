package com.breadbuster.barangayextend.classes.Beans;

public class RequestPositionBean {
    String requestedBy,requestStatus,requestedPosition,requestDateAndTimeRegistered,requestDateAndTimeConfirmed;

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestedPosition() {
        return requestedPosition;
    }

    public void setRequestedPosition(String requestedPosition) {
        this.requestedPosition = requestedPosition;
    }

    public String getRequestDateAndTimeRegistered() {
        return requestDateAndTimeRegistered;
    }

    public void setRequestDateAndTimeRegistered(String requestDateAndTimeRegistered) {
        this.requestDateAndTimeRegistered = requestDateAndTimeRegistered;
    }

    public String getRequestDateAndTimeConfirmed() {
        return requestDateAndTimeConfirmed;
    }

    public void setRequestDateAndTimeConfirmed(String requestDateAndTimeConfirmed) {
        this.requestDateAndTimeConfirmed = requestDateAndTimeConfirmed;
    }
}
