package marmu.com.deeplink.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import marmu.com.deeplink.utils.Permissions;

public class LoginActivity extends AppCompatActivity implements Serializable {

    private ArrayList<SpinnerCountryModel> spinnerCountry = new ArrayList<>();
    private EditText countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        countryCode = (EditText) findViewById(R.id.et_login_country_code);
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
        final Spinner countryCodeSpinner = (Spinner) findViewById(R.id.sp_login_country);
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
        if (Permissions.SMS(LoginActivity.this)) {
            TextView phoneNumber = (TextView) findViewById(R.id.et_login_phone_number);
            String phone = phoneNumber.getText().toString();
            if (!phone.isEmpty()) {
                FirebasePhoneAuthentication.sendVerificationCode(LoginActivity.this, phone);
            } else {
                DialogUtils.appToastShort(getApplicationContext(), "Enter valid mobile number");
            }
        } else {
            Permissions.SMS(LoginActivity.this);
        }
    }
}
