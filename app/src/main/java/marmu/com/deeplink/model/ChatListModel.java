package marmu.com.deeplink.model;

import java.io.Serializable;

/**
 * Created by azharuddin on 3/8/17.
 */

public class ChatListModel implements Serializable {
    private String key;
    private Object chatListMap;

    public ChatListModel(String key, Object chatListMap) {
        this.key = key;
        this.chatListMap = chatListMap;
    }

    public String getKey() {
        return key;
    }

    public Object getChatListMap() {
        return chatListMap;
    }
}
