package becker.andy.userapp.viewmodel;

import androidx.lifecycle.ViewModel;

import becker.andy.userapp.repository.LoginRepository;
import becker.andy.userapp.utils.HomeInterface;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class HomeActivityViewModel extends ViewModel {

    private LoginRepository loginRepository = new LoginRepository();
    private SingleLiveEvent<Boolean> onLogoutClicked = new SingleLiveEvent<>();
    private SingleLiveEvent <Boolean> onLogoutEnd = new SingleLiveEvent<>();
    public void logoutUser(PrefConfig prefs) {
        loginRepository.logoutUser(prefs);
    }

    public SingleLiveEvent<String> getLogoutResponse() {
        return loginRepository.getLogoutResponse();
    }

    public void onLogoutClicked(boolean clicked){
        onLogoutClicked.setValue(clicked);
    }

    public void onLogoutEnd(boolean b){
        onLogoutEnd.setValue(b);
    }

    public SingleLiveEvent<Boolean> getOnLogoutEnd() {
        return onLogoutEnd;
    }

    public SingleLiveEvent<Boolean> getOnLogoutClicked() {
        return onLogoutClicked;
    }

    public void setOnLogoutClicked(SingleLiveEvent<Boolean> onLogoutClicked) {
        this.onLogoutClicked = onLogoutClicked;
    }
}
