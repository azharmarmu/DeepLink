package marmu.com.deeplink.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import marmu.com.deeplink.activity.ChatScreenActivity;

/**
 * Created by azharuddin on 4/8/17.
 */

@SuppressWarnings("ConstantConditions")
public class Firebase {

    private static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private static final DatabaseReference ENVIRONMENT = DATABASE.getReference(Constants.ENV);
    public static final DatabaseReference userListDBRef = ENVIRONMENT.child(Constants.USER);
    //public static final DatabaseReference groupListDBRef = ENVIRONMENT.child(Constants.GROUP);
    public static final DatabaseReference messageDBRef = ENVIRONMENT.child(Constants.MESSAGE);
    //public static final DatabaseReference messageStatusDBRef = DATABASE.getReference(Constants.MESSAGE_STATUS);

    private static StorageReference mStorageReference =
            FirebaseStorage.getInstance().getReference().child(Constants.ENV);


    /*Image url*/
    private static Uri downloadUri;

    //Storing attachments to FireBase
    @SuppressWarnings("VisibleForTests")
    public static void storeInFirebase(Uri uri,
                                       final String type,
                                       final String fileName,
                                       final ProgressBar progressBar,
                                       final String uuid) {
        StorageReference imgRef;
        if (type.equalsIgnoreCase(Constants.PROFILE_PIC))
            imgRef = mStorageReference.child("profile_images").child(fileName);
        else
            imgRef = mStorageReference.child("chat_documents").child(fileName)
                    .child(String.valueOf(System.currentTimeMillis()));

        imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUri = taskSnapshot.getDownloadUrl();

                assert downloadUri != null;
                if (type.equalsIgnoreCase(Constants.PROFILE_PIC)) {
                    userListDBRef.child(Constants.AUTH.getCurrentUser().getUid())
                            .child(Constants.IMG_URL).setValue(downloadUri.toString());
                } else if (type.equalsIgnoreCase(Constants.CHAT_FILE)) {
                    new ChatScreenActivity().updateImageURL(downloadUri.toString(), fileName, uuid);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());

                Log.e("Progress", String.valueOf(progress));

                if (type.equalsIgnoreCase(Constants.PROFILE_PIC)) {
                    progressBar.setProgress(progress);
                    progressBar.setVisibility(View.VISIBLE);
                    if (progress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }/* else if (type.equalsIgnoreCase(Constants.CHAT_FILE)) {
                }*/

            }
        });
    }

    //todo
    public static void storeInFirebaseAudio(Uri uri,
                                            final String type,
                                            final String fileName,
                                            final ProgressBar progressBar,
                                            final String uuid) {

        StorageReference imgRef;
        if (type.equalsIgnoreCase(Constants.PROFILE_PIC))
            imgRef = mStorageReference.child("profile_images").child(fileName);
        else
            imgRef = mStorageReference.child("chat_documents").child(fileName)
                    .child(String.valueOf(System.currentTimeMillis()));

        imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUri = taskSnapshot.getDownloadUrl();

                assert downloadUri != null;
                if (type.equalsIgnoreCase(Constants.PROFILE_PIC)) {
                    userListDBRef.child(Constants.AUTH.getCurrentUser().getUid())
                            .child(Constants.IMG_URL).setValue(downloadUri.toString());
                } else if (type.equalsIgnoreCase(Constants.CHAT_FILE)) {
                    new ChatScreenActivity().updateImageURL(downloadUri.toString(), fileName, uuid);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());

                Log.e("Progress", String.valueOf(progress));

                if (type.equalsIgnoreCase(Constants.PROFILE_PIC)) {
                    progressBar.setProgress(progress);
                    progressBar.setVisibility(View.VISIBLE);
                    if (progress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }/* else if (type.equalsIgnoreCase(Constants.CHAT_FILE)) {
                }*/

            }
        });
    }


    public static void deleteInFirebase(final String fileName) {
        StorageReference imgRef = mStorageReference.child("profile_images").child(fileName);
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userListDBRef.child(fileName)
                        .child(Constants.IMG_URL)
                        .removeValue();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error", e.getMessage());
            }
        });
    }
}
