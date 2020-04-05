package becker.andy.userapp.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("mobile")
    String mobile;
    String password;
    @SerializedName("name")
    String name;
    @SerializedName("designation")
    String designation;
    @SerializedName("district")
    String district;
    @SerializedName("email")
    String email;
    @SerializedName("access_token")
    String access_token;
    @SerializedName("message")
    String message;
    @SerializedName("task")
    String task;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
