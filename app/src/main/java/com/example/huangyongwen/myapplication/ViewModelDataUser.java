package com.example.huangyongwen.myapplication;

import com.example.huangyongwen.myapplication.model.User;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Administrator on 2020/5/15.
 */

public class ViewModelDataUser<U> extends ViewModel {
    private MutableLiveData<User> user;
    private int number;

    public void setUser(MutableLiveData<User> user) {
        this.user = user;
    }

    public MutableLiveData<? extends Object> getUser() {
        return user == null ? new MutableLiveData<>() : user;
    }

    public void setNumber(int number) {
        this.number = number;
    }

//    public MutableLiveData<User> getUser() {
//        if (user == null) {
//            user = new MutableLiveData();
//            user.setValue(new User());
//        }
//        return user;
//
//    }

    public int getNumber() {
        return number++;
    }
}
