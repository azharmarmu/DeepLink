package marmu.com.deeplink.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import marmu.com.deeplink.activity.MainActivity;
import marmu.com.deeplink.activity.OTPActivity;

/**
 * Created by azharuddin on 4/8/17.
 */

public class FirebasePhoneAuthentication {

    public static void sendVerificationCode(final Activity activity, final String phoneNumber) {
        DialogUtils.showProgressDialog(activity, "Loading...");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //Instant verification or Auto-retrieval.
                        Log.d("Success", "onVerificationCompleted:" + credential);
                        DialogUtils.dismissProgressDialog();
                        FirebasePhoneAuthentication
                                .signInWithPhoneAuthCredential(activity, credential, phoneNumber);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("Failed", "onVerificationFailed", e);
                        DialogUtils.dismissProgressDialog();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            DialogUtils.appToastLong(activity, "Invalid request:"+e);
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            DialogUtils.appToastLong(activity,
                                    "The SMS quota for the project has been exceeded");
                        } else {
                            DialogUtils.appToastLong(activity, e.getMessage());
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        DialogUtils.dismissProgressDialog();
                        Log.d("OTP Code", "onCodeSent:" + verificationId);
                        Intent otpActivity = new Intent(activity, OTPActivity.class);
                        otpActivity.putExtra(Constants.PHONE_NUMBER, phoneNumber);
                        otpActivity.putExtra(Constants.VERIFICATION_ID, verificationId);
                        Constants.OTP_RESEND_TOKEN = token;
                        activity.startActivity(otpActivity);
                        activity.finish();
                    }
                });
    }

    public static void resendVerificationCode(final Activity activity,
                                              final String phoneNumber,
                                              PhoneAuthProvider.ForceResendingToken token) {
        DialogUtils.showProgressDialog(activity, "Loading...");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //Instant verification or Auto-retrieval.
                        Log.d("Success", "onVerificationCompleted:" + credential);
                        FirebasePhoneAuthentication
                                .signInWithPhoneAuthCredential(activity, credential, phoneNumber);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("Failed", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            DialogUtils.appToastLong(activity, "Invalid request");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            DialogUtils.appToastLong(activity,
                                    "The SMS quota for the project has been exceeded");
                        } else {
                            DialogUtils.appToastLong(activity, e.getMessage());
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        DialogUtils.dismissProgressDialog();
                        Log.d("OTP Code", "onCodeSent:" + verificationId);
                        Intent otpActivity = new Intent(activity, OTPActivity.class);
                        otpActivity.putExtra(Constants.PHONE_NUMBER, phoneNumber);
                        otpActivity.putExtra(Constants.VERIFICATION_ID, verificationId);
                        Constants.OTP_RESEND_TOKEN = token;
                        activity.startActivity(otpActivity);
                        activity.finish();
                    }
                },
                token);
    }

    public static void signInWithPhoneAuthCredential(final Activity activity,
                                                     PhoneAuthCredential phoneAuthCredential,
                                                     final String phoneNumber) {
        Constants.AUTH.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener((new OnCompleteListener<AuthResult>() {
                    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithCredential:success");

                            HashMap<String, Object> map = new HashMap<>();
                            map.put(Constants.PHONE_NUMBER, phoneNumber);
                            map.put(Constants.DEVICE_TOKEN, SharePref.getToken(activity));

                            // Add user details to DB
                            Firebase.userListDBRef.child(Constants.AUTH.getCurrentUser().getUid()).updateChildren(map);

                            Intent mainActivity = new Intent(activity, MainActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(mainActivity);
                            activity.finish();

                        } else {
                            Log.w("Failed", "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                DialogUtils.appToastShort(activity, "Verification code is wrong");
                            } else {
                                DialogUtils.appToastShort(activity, task.getException().getMessage());
                            }
                        }
                    }
                }));
    }
}
