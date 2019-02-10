package com.breadbuster.barangayextend.classes;

public class urls {
    private String ipAddress = "http://tristanrosales.x10.mx/Barangay%20Extend/";
    //String ipAddress = "http://192.168.1.8/Barangay%20Extend/";

    private String insertUserUrl = ipAddress + "insert/insertUser.php";
    private String showAllUsersUrl = ipAddress + "show_or_get/showAllUsers.php";
    private String loginUserUrl = ipAddress + "loginUser.php";
    private String infoOfLoggedInUserUrl = ipAddress + "show_or_get/getInfoOfLoggedInUser.php?emailAddressOrUsername=";
    private String postSomethingUrl = ipAddress + "insert/postSomething.php";
    private String showAllPostUrl = ipAddress + "show_or_get/showAllPost.php";
    private String updateInfoOfLoggedInUserUrl = ipAddress + "update/updateInfoOfLoggedInUser.php?userID=";
    private String showAllPostOfLoggedInUserUrl = ipAddress + "show_or_get/showAllPostOfLoggedInUser.php?userID=";
    private String addBarangayUrl = ipAddress + "insert/addBarangay.php";
    private String showAllBarangayUrl = ipAddress + "show_or_get/showAllBarangay.php";
    private String insertQuestionForFAQUrl = ipAddress + "insert/addQuestionWithAnswer.php";
    private String showAllFAQsUrl = ipAddress + "show_or_get/showAllFAQs.php";
    private String getAllAvailableBarangayUrl = ipAddress + "show_or_get/getAllAvailableBarangay.php";
    private String addQuestionInRegistrationUrl = ipAddress + "insert/addQuestionInRegistration.php";
    private String getAllAvailableSecurityQuestionsUrl = ipAddress + "show_or_get/getAllAvailableSecurityQuestions.php";
    private String showAllPostBasedOnUserBarangayUrl = ipAddress + "show_or_get/showAllPostBasedOnUserBarangay.php";
    private String checkIfEmailExistsUrl = ipAddress + "show_or_get/checkIfEmailExists.php?emailAddress=";
    private String updatePasswordUrl = ipAddress + "update/updatePassword.php?emailAddress=";
    private String addRequestedPositionUrl = ipAddress + "insert/addRequestedPosition.php";
    private String showAllRequestBasedOnStatus = ipAddress + "show_or_get/showAllRequestBasedOnStatus.php?requestStatus=";
    private String updateStatusOfUser = ipAddress + "update/updateStatusOfUser.php?requestedBy=";
    private String deleteRequestForPosition = ipAddress + "delete/deleteRequestForPosition.php?requestedBy=";
    private String deletePost = ipAddress + "delete/deletePost.php?post_id=";
    private String deleteSelectedUser = ipAddress + "delete/deleteSelectedUser.php?user_emailAddress=";
    private String deleteSelectedBarangay = ipAddress + "delete/deleteSelectedBarangay.php?barangayID=";
    private String updateSelectedBarangay = ipAddress + "update/updateSelectedBarangay.php?barangayID=";
    private String insertFeedbackAndHelpUrl = ipAddress + "insert/insertFeedbackAndHelp.php";
    private String insertCommentToAPostUrl = ipAddress + "insert/addCommentToAPost.php";
    private String showAllCommentUrl = ipAddress + "show_or_get/showAllComments.php?postID=";
    private String deleteCommentUrl = ipAddress + "delete/deleteComment.php?commentID=";
    private String updateCommentUrl = ipAddress + "update/updateComment.php?commentID=";
    private String showAllFeedbacksBasedOnFeedbackTypeUrl = ipAddress + "show_or_get/showAllFeedbacksBasedOnFeedbackType.php?feedbackType=";

    public String getInsertUserUrl() {
        return insertUserUrl;
    }

    public String getShowAllUsersUrl() {
        return showAllUsersUrl;
    }

    public String getLoginUserUrl() {
        return loginUserUrl;
    }

    public String getInfoOfLoggedInUserUrl() {
        return infoOfLoggedInUserUrl;
    }

    public String getPostSomethingUrl() {
        return postSomethingUrl;
    }

    public String getShowAllPostUrl() {
        return showAllPostUrl;
    }

    public String getUpdateInfoOfLoggedInUserUrl() {
        return updateInfoOfLoggedInUserUrl;
    }

    public String getShowAllPostOfLoggedInUserUrl() {
        return showAllPostOfLoggedInUserUrl;
    }

    public String getAddBarangayUrl() {
        return addBarangayUrl;
    }

    public String getShowAllBarangayUrl() {
        return showAllBarangayUrl;
    }

    public String getInsertQuestionForFAQUrl() {
        return insertQuestionForFAQUrl;
    }

    public String getShowAllFAQsUrl() {
        return showAllFAQsUrl;
    }

    public String getGetAllAvailableBarangayUrl() {
        return getAllAvailableBarangayUrl;
    }

    public String getAddQuestionInRegistrationUrl() {
        return addQuestionInRegistrationUrl;
    }

    public String getGetAllAvailableSecurityQuestionsUrl() {
        return getAllAvailableSecurityQuestionsUrl;
    }

    public String getShowAllPostBasedOnUserBarangayUrl() {
        return showAllPostBasedOnUserBarangayUrl;
    }

    public String getCheckIfEmailExistsUrl() {
        return checkIfEmailExistsUrl;
    }

    public String getUpdatePasswordUrl() {
        return updatePasswordUrl;
    }

    public String getAddRequestedPositionUrl() {
        return addRequestedPositionUrl;
    }

    public String getShowAllRequestBasedOnStatus() {
        return showAllRequestBasedOnStatus;
    }

    public String getUpdateStatusOfUser() {
        return updateStatusOfUser;
    }

    public String getDeleteRequestForPosition() {
        return deleteRequestForPosition;
    }

    public String getDeletePost() {
        return deletePost;
    }

    public String getDeleteSelectedUser() {
        return deleteSelectedUser;
    }

    public String getDeleteSelectedBarangay() {
        return deleteSelectedBarangay;
    }

    public String getUpdateSelectedBarangay() {
        return updateSelectedBarangay;
    }

    public String getInsertFeedbackAndHelpUrl() {
        return insertFeedbackAndHelpUrl;
    }

    public String getInsertCommentToAPostUrl() {
        return insertCommentToAPostUrl;
    }

    public String getShowAllCommentUrl() {
        return showAllCommentUrl;
    }

    public String getDeleteCommentUrl() {
        return deleteCommentUrl;
    }

    public String getUpdateCommentUrl() {
        return updateCommentUrl;
    }

    public String getShowAllFeedbacksBasedOnFeedbackTypeUrl() {
        return showAllFeedbacksBasedOnFeedbackTypeUrl;
    }
}
