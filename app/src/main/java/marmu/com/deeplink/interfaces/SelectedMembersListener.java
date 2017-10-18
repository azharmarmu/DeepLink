package marmu.com.deeplink.interfaces;

import marmu.com.deeplink.model.ContactsModel;

/**
 * Created by azharuddin on 17/10/17.
 */

public interface SelectedMembersListener {
    public void onSelectedMember(ContactsModel member);
    public void onRemovedMember(ContactsModel member);
}
