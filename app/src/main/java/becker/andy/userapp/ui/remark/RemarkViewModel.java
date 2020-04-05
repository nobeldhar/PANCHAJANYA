package becker.andy.userapp.ui.remark;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class RemarkViewModel extends ViewModel {

    private SingleLiveEvent<String> mRemark = new SingleLiveEvent<>();
    private String remark;
    private MainRepository mainRepository = new MainRepository();

    public void onRemarkButton(View view){

        if(remark != null){
            mRemark.setValue("ok");
        }else {
            mRemark.setValue("Provide Reason");
        }
    }

    public void remarkUser(PrefConfig prefs) {
        mainRepository.remarkUser(prefs, remark);
    }

    public SingleLiveEvent<String> getRemarkResponse() {
        return mainRepository.getRemarkResponse();
    }

    public SingleLiveEvent<String> getmRemark() {
        return mRemark;
    }

    public void setmRemark(SingleLiveEvent<String> mRemark) {
        this.mRemark = mRemark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public MainRepository getMainRepository() {
        return mainRepository;
    }

    public void setMainRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }


}