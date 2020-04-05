package becker.andy.userapp.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private AtomicBoolean mPending = new AtomicBoolean(false);
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if(mPending.compareAndSet(true,false)){
                    observer.onChanged(t);
                }
            }
        });
    }

    @Override
    public void setValue(T value) {
        mPending.set(true);
        super.setValue(value);
    }

}
