package becker.andy.userapp.viewmodel;

import androidx.lifecycle.ViewModel;

import becker.andy.userapp.repository.LoginRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class HomeActivityViewModel extends ViewModel {

    private LoginRepository loginRepository = new LoginRepository();
    public void logoutUser(PrefConfig prefs) {
        loginRepository.logoutUser(prefs);
    }

    public SingleLiveEvent<String> getLogoutResponse() {
        return loginRepository.getLogoutResponse();
    }
}
