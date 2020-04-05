package becker.andy.userapp.ui.leave;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class LeaveViewModel extends ViewModel {

    private SingleLiveEvent<String> mLeave = new SingleLiveEvent<>();
    private String reason;
    private MainRepository mainRepository = new MainRepository();

    public void onLeaveButton(View view){

        if(reason != null){
            mLeave.setValue("ok");
        }else {
            mLeave.setValue("Provide Reason");
        }

    }

    public void leaveUser(PrefConfig prefConfig, String date){
        mainRepository.leaveUser(prefConfig, date, reason);
    }

    public SingleLiveEvent<String> getmLeave() {
        return mLeave;
    }

    public SingleLiveEvent<String> getLeaveResponse() {
        return mainRepository.getLeaveResponse();
    }

    public void setmLeave(SingleLiveEvent<String> mLeave) {
        this.mLeave = mLeave;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}