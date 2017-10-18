package marmu.com.deeplink.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import marmu.com.deeplink.R;
import marmu.com.deeplink.sms.SMSReceiver;
import marmu.com.deeplink.sms.SmsListener;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.DialogUtils;
import marmu.com.deeplink.utils.FirebasePhoneAuthentication;

public class OTPActivity extends AppCompatActivity {

    String verificationId, phoneNumber;
    PhoneAuthProvider.ForceResendingToken ForceResendingToken;
    EditText etOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOTP = (EditText) findViewById(R.id.et_otp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString(Constants.PHONE_NUMBER);
            verificationId = bundle.getString(Constants.VERIFICATION_ID);
            ForceResendingToken = Constants.OTP_RESEND_TOKEN;
        }
        autoFetchOTP();
    }

    private void autoFetchOTP() {
        SMSReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                etOTP.setText(messageText);
                String code = etOTP.getText().toString();
                etOTP.setSelection(code.length());
                verifyOTP(code);
            }
        });
    }

    public void resendOTP(View view) {
        FirebasePhoneAuthentication.resendVerificationCode(OTPActivity.this, phoneNumber, ForceResendingToken);
    }

    public void checkOTP(View view) {
        String code = etOTP.getText().toString();
        if (!code.isEmpty()) {
            verifyOTP(code);
        } else {
            DialogUtils.appToastShort(OTPActivity.this, "OTP cannot be empty");
        }
    }

    private void verifyOTP(String code) {
        FirebasePhoneAuthentication.signInWithPhoneAuthCredential(OTPActivity.this,
                new PhoneAuthCredential(verificationId, code),
                phoneNumber);
    }
}
