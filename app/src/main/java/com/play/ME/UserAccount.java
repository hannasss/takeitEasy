package com.play.ME;

/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {
    private String idToken;//firebase Uid 고유 토큰 정보
    private String userName;
    private String emailId;
    private String password;

    public UserAccount(){}

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
