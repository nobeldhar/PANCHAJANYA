package becker.andy.userapp.ui.leave;

import android.app.DatePickerDialog;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class LeaveViewModel extends ViewModel {

    private SingleLiveEvent<String> mLeave = new SingleLiveEvent<>();
    private SingleLiveEvent<View> onDateClick = new SingleLiveEvent<>();
    public String reason;
    public String from;
    public String to;
    public String no_of_days;
    private MainRepository mainRepository = new MainRepository();

    public void onLeaveButton(View view){

        if(reason != null && from != null && to != null && no_of_days != null){
            mLeave.setValue("ok");
        }else {
            mLeave.setValue("Provide all information");
        }
    }

    public void onFromClick(View view){
        onDateClick.setValue(view);
    }
    public void onToClick(View view){
        onDateClick.setValue(view);
    }

    public SingleLiveEvent<View> getOnDateClick() {
        return onDateClick;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNo_of_days() {
        return no_of_days;
    }

    public void setNo_of_days(String no_of_days) {
        this.no_of_days = no_of_days;
    }

    public void leaveUser(PrefConfig prefConfig){
        mainRepository.leaveUser(prefConfig, from, to, no_of_days, reason);
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