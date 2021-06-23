package cn.linked.link.business.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.repository.entry.UserRepository;

public class LoginViewModel {

    private MutableLiveData<Object> loginStateLiveData = new MutableLiveData<>();

    public void login(@NonNull Long userId, @NonNull String password) {
        LinkApplication.getInstance().getAndCreateInstance(UserRepository.class).login(userId, password)
                .then(value -> {
                    loginStateLiveData.postValue(value);
                    return null;
                }, error -> {
                    loginStateLiveData.postValue(error.getMessage());
                    return null;
                });
    }

    public MutableLiveData<Object> getLoginStateLiveData() {
        return this.loginStateLiveData;
    }
}
