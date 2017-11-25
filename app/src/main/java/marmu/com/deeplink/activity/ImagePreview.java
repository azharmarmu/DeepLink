package marmu.com.deeplink.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import marmu.com.deeplink.R;
import marmu.com.deeplink.utils.Constants;
import marmu.com.deeplink.utils.ImageUtils;

import static marmu.com.deeplink.activity.Settings.profilePhoto;

public class ImagePreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        ImageView imageView = findViewById(R.id.image_view);

        if (profilePhoto != null) {
            imageView.setImageBitmap(profilePhoto);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                ImageUtils.loadImageToViewByURL(this,
                        imageView,
                        Uri.parse(bundle.getString(Constants.IMG_URL)));
            }
        }
    }
}
