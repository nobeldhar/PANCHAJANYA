package becker.andy.userapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    @SerializedName("tasks")
    List<String> task_list;

    public List<String> getTask_list() {
        return task_list;
    }

    public void setTask_list(List<String> task_list) {
        this.task_list = task_list;
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
