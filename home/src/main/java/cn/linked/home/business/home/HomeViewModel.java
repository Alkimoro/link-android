package cn.linked.home.business.home;

import androidx.lifecycle.MutableLiveData;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.AppNetwork;
import cn.linked.baselib.common.ChatManager;
import cn.linked.baselib.repository.entry.ChatRepository;
import cn.linked.baselib.repository.entry.UserRepository;
import lombok.Getter;

public class HomeViewModel {

    private final ChatManager chatManager;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Getter
    private MutableLiveData<String> userStateLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        chatRepository = LinkApplication.getInstance().getAndCreateInstance(ChatRepository.class);
        userRepository = LinkApplication.getInstance().getAndCreateInstance(UserRepository.class);
        chatManager = LinkApplication.getInstance().getChatManager();
        init();
        setUserState();
    }

    private void setUserState() {
        AppNetwork.NetworkState networkState = AppNetwork.getNetworkState();
        String stateText;
        switch (networkState) {
            case WIFI: stateText = "WIFI在线";break;
            case NET_2G: stateText = "2G在线";break;
            case NET_3G: stateText = "3G在线";break;
            case NET_4G: stateText = "4G在线";break;
            case NET_5G: stateText = "5G在线";break;
            case UNKNOWN: stateText = "在线";break;
            default: stateText = "离线";
        }
        userStateLiveData.postValue(stateText);
    }

    private void init() {
        chatManager.addChannelActiveStateListener(currentState -> {
            setUserState();
        });
    }

}
