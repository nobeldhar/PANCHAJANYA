package becker.andy.userapp.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class TaskViewModel extends ViewModel {

    private MainRepository mainRepository = new MainRepository();

    public void getTasks(PrefConfig prefs) {
        mainRepository.getTasks(prefs);
    }

    public MutableLiveData<String> getTaskResponse() {
        return mainRepository.getTaskResponse();
    }
    public MutableLiveData<List<String>> getTaskList() {
        return mainRepository.getTaskList();
    }
}