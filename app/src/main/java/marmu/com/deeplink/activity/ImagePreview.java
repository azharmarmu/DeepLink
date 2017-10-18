package marmu.com.deeplink.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import marmu.com.deeplink.R;
import marmu.com.deeplink.utils.Constants;

public class ImagePreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Bundle bundle = getIntent().getExtras();
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        if (bundle != null) {
            String imgUrl = bundle.getString(Constants.IMG_URL);
            if (bundle.getString(Constants.TYPE) != null) {
                Bitmap bmp = BitmapFactory.decodeFile(imgUrl);
                imageView.setImageBitmap(bmp);
            } else {
                Picasso.with(ImagePreview.this).load(imgUrl)
                        .placeholder(R.drawable.people)
                        .into(imageView);
            }
        } else {
            imageView.setImageBitmap(Settings.profilePhoto);
        }
    }
}
