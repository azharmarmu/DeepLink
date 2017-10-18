package marmu.com.deeplink.model;

/**
 * Created by azharuddin on 4/8/17.
 */

public class SpinnerCountryModel {

    private String name, dialCode, code;

    public SpinnerCountryModel(String name, String dialCode, String code) {
        this.name = name;
        this.dialCode = dialCode;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public String getCode() {
        return code;
    }
}
