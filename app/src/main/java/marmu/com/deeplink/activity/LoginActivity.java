package marmu.com.deeplink.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import marmu.com.deeplink.R;
import marmu.com.deeplink.adapter.SpinnerCountryAdapter;
import marmu.com.deeplink.model.SpinnerCountryModel;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.DialogUtils;
import marmu.com.deeplink.utils.FirebasePhoneAuthentication;

public class LoginActivity extends AppCompatActivity
        implements Serializable, PermissionListener, PermissionRequestErrorListener {

    private ArrayList<SpinnerCountryModel> spinnerCountry = new ArrayList<>();
    private EditText countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCode = findViewById(R.id.et_login_country_code);
        setCountry();
        countrySpinner();
    }


    private void setCountry() {
        try {
            String countries = Constants.countries;
            JSONArray countriesArray = new JSONArray(countries);
            for (int i = 0; i < countriesArray.length(); i++) {
                JSONObject country = countriesArray.getJSONObject(i);
                spinnerCountry.add(new SpinnerCountryModel(country.getString("name"),
                        country.getString("dial_code"),
                        country.getString("code")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void countrySpinner() {
        final Spinner countryCodeSpinner = findViewById(R.id.sp_login_country);
        SpinnerCountryAdapter adapter =
                new SpinnerCountryAdapter(this, R.layout.spinner_country, spinnerCountry);

        // Set adapter to spinner
        countryCodeSpinner.setAdapter(adapter);

        // Listener called when spinner item selected
        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                TextView code = v.findViewById(R.id.tv_country_dial_code);
                countryCode.setText(code.getText().toString().replace("+", "") + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void nextClick(View view) {
        Dexter.withActivity(this)
                .withPermission(
                        Manifest.permission.RECEIVE_SMS)
                .withListener(this)
                .check();
    }

    private void validatePhoneNumber() {
        TextView phoneNumber = findViewById(R.id.et_login_phone_number);
        String phone = "+91" + phoneNumber.getText().toString();
        if (!phone.isEmpty()) {
            FirebasePhoneAuthentication.sendVerificationCode(LoginActivity.this, phone);
        } else {
            DialogUtils.appToastShort(getApplicationContext(), "Enter valid mobile number");
        }
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        validatePhoneNumber();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        validatePhoneNumber();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                   PermissionToken token) {
        token.continuePermissionRequest();
    }

    @Override
    public void onError(DexterError error) {
        Log.e("Dexter", "There was an error: " + error.toString());
    }
}
