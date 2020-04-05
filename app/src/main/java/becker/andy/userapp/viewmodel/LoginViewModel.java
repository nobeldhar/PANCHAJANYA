package becker.andy.userapp.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import becker.andy.userapp.models.User;
import becker.andy.userapp.repository.LoginRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class LoginViewModel extends ViewModel {
    public String mobile;
    public String password;
    private LoginRepository loginRepository = new LoginRepository();
    private SingleLiveEvent<String> progressbar = new SingleLiveEvent<>();

    public void onLoginButton(View view){

        progressbar.setValue("");

        if(mobile== null || password==null || password.length() <8 ){
            loginRepository.getLoginResponse().setValue("Invalid Credentials");
            return;
        }
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        loginRepository.loginUser(user);

    }

    public SingleLiveEvent<String> getProgressbar() {
        return progressbar;
    }

    public SingleLiveEvent<String> getLoginResponse() {
        return loginRepository.getLoginResponse();
    }

    public SingleLiveEvent<User> getUserResponse() {
        return loginRepository.getUserResponse();
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

    public void writeUser(User user, PrefConfig prefConfig) {
        prefConfig.writeUser(user);
    }
}
