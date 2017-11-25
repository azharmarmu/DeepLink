package marmu.com.deeplink.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import marmu.com.deeplink.R;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.Firebase;
import marmu.com.deeplink.utils.ImageUtils;

import static marmu.com.deeplink.utils.Constants.REQUEST_CAMERA;
import static marmu.com.deeplink.utils.Constants.REQUEST_GALLERY;
import static marmu.com.deeplink.utils.Firebase.userListDBRef;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class Settings extends AppCompatActivity {

    TextView profileName;
    ImageView profilePic;
    ProgressBar progressBar;

    public static Bitmap profilePhoto;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileName = findViewById(R.id.tv_name);
        profilePic = findViewById(R.id.iv_profile_pic);
        progressBar = findViewById(R.id.progress_bar);

        loadUI();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ImagePreview.class);
                intent.putExtra(Constants.IMG_URL, imageUrl);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(Settings.this,
                                profilePic,
                                getString(R.string.app_name));

                startActivity(intent, options.toBundle());
            }
        });
    }

    public static HashMap<String, Object> user = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    private void loadUI() {
        userListDBRef.child(Constants.AUTH.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            user = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (user.get(Constants.MY_NAME) == null) {
                                profileName.setText(getString(R.string.anonymous));
                            } else {
                                profileName.setText("" + user.get(Constants.MY_NAME));
                            }
                            if (user.containsKey(Constants.IMG_URL)) {
                                imageUrl = String.valueOf(user.get(Constants.IMG_URL));
                            } else {
                                imageUrl = "";
                            }
                            if (profilePhoto != null) {
                                profilePic.setImageBitmap(profilePhoto);
                            } else {
                                ImageUtils.loadImageToViewByURL(Settings.this, profilePic, Uri.parse(imageUrl));
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });
    }

    public void editName(View view) {
        alertDialog(profileName.getText().toString());
    }

    public void viewImage(View view) {
    }

    public void chooseImage(View view) {
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_image_settings);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;

        // set the custom dialog components - text and button
        TextView gallery = dialog.findViewById(R.id.dialog_gallery);
        TextView camera = dialog.findViewById(R.id.dialog_camera);
        TextView remove = dialog.findViewById(R.id.dialog_remove);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
                dialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageUrl.isEmpty()) {
                    Firebase.deleteInFirebase(Constants.AUTH.getCurrentUser().getUid());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    Uri imageUri;

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap thumbnails = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    profilePic.setImageBitmap(thumbnails);
                    Firebase.storeInFirebase(ImageUtils.getImageUri(Settings.this, thumbnails),
                            Constants.PROFILE_PIC,
                            Constants.AUTH.getCurrentUser().getUid(),
                            progressBar,
                            null);

                } else if (requestCode == REQUEST_GALLERY) {
                    profilePhoto = ImageUtils
                            .selectedImageFromGallery(Settings.this, data);
                    Firebase.storeInFirebase(data.getData(),
                            Constants.PROFILE_PIC,
                            Constants.AUTH.getCurrentUser().getUid(),
                            progressBar,
                            null);
                    profilePic.setImageBitmap(profilePhoto);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    @SuppressLint("InflateParams")
    private void alertDialog(String name) {
        if (name.equalsIgnoreCase(getString(R.string.anonymous))) {
            name = "";
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.this);
        LayoutInflater inflater = (LayoutInflater) Settings.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_name, null);
        dialogBuilder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.et_my_name);
        etName.setText(name);
        etName.setSelection(name.length());

        dialogBuilder.setMessage("Edit Name");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            public void onClick(DialogInterface dialog, int whichButton) {
                String myName = etName.getText().toString();
                if (!myName.isEmpty()) {
                    userListDBRef.child(Constants.AUTH.getCurrentUser().getUid())
                            .child(Constants.MY_NAME)
                            .setValue(myName);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
