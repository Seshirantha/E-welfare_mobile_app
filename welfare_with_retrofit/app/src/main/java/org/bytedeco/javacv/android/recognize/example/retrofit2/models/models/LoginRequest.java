package org.bytedeco.javacv.android.recognize.example.retrofit2.models.models;

public class LoginRequest {

    private String student_no;
    private String password;

    public LoginRequest(String student_no, String password) {
        this.student_no = student_no;
        this.password = password;
    }
}
