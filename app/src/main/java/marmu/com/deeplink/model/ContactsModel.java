package marmu.com.deeplink.model;

/**
 * Created by azharuddin on 5/8/17.
 */

public class ContactsModel {
    private String name, phone, key;

    public ContactsModel(String key, String name, String phone) {
        this.key = key;
        this.name = name;
        this.phone = phone;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
