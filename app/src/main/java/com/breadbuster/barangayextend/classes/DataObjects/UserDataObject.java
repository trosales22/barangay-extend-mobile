package com.breadbuster.barangayextend.classes.DataObjects;

public class UserDataObject {
    String firstname,lastname,emailAddress,username,phoneNumber,gender,userType,barangay,imgProfilePicture;

    public UserDataObject(String firstname, String lastname, String emailAddress, String username, String phoneNumber, String gender, String userType, String barangay, String imgProfilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.userType = userType;
        this.barangay = barangay;
        this.imgProfilePicture = imgProfilePicture;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getUserType() {
        return userType;
    }

    public String getBarangay() {
        return barangay;
    }

    public String getImgProfilePicture() {
        return imgProfilePicture;
    }
}
