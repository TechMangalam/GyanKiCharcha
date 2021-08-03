package com.bitaam.gyankicharcha.modals;

import java.io.Serializable;

public class ChatFriendModel implements Serializable {

    String friendName ,friendNo,recentMsg;

    public ChatFriendModel() {
    }

    public ChatFriendModel(String friendName, String friendNo, String recentMsg) {
        this.friendName = friendName;
        this.friendNo = friendNo;
        this.recentMsg = recentMsg;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendNo() {
        return friendNo;
    }

    public void setFriendNo(String friendNo) {
        this.friendNo = friendNo;
    }

    public String getRecentMsg() {
        return recentMsg;
    }

    public void setRecentMsg(String recentMsg) {
        this.recentMsg = recentMsg;
    }
}
