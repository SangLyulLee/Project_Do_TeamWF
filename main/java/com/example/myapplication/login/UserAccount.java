package com.example.myapplication.login;

// 사용자 계정 정보 모델 클래스

public class UserAccount {
    private String idToken;     // Firebase Uid (고유 토큰 정보)
    private String id;          // 아이디
    private String password;    // 비밀번호
    private int u_type;         // 장애 유형

    public int getU_type() { return u_type; }

    public void setU_type(int u_type) { this.u_type = u_type; }

    public UserAccount() { }

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
