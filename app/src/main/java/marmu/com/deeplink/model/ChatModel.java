package marmu.com.deeplink.model;

import java.io.Serializable;

/**
 * Created by azharuddin on 5/8/17.
 */

public class ChatModel implements Serializable {
    private String key;
    private Object chatListMap;

    public ChatModel(String key, Object chatListMap) {
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
