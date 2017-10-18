package marmu.com.deeplink.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import marmu.com.deeplink.R;

/**
 * Created by azharuddin on 3/5/17.
 */

public class ImageUtils {

    public static void loadImageToViewByURL(final Context context, final ImageView view, final Uri imageUrl) {
        Picasso.with(context)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("Picasso", "Sender image from cache");
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUrl)
                                .error(R.drawable.people)
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.v("Picasso", "Image from firebase");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });
    }

    public static void loadCircleImageToViewByURL(final Context context, final ImageView view, final Uri imageUrl) {
        Picasso.with(context)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("Picasso", "Sender image from cache");
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUrl)
                                .error(R.mipmap.ic_launcher)
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.v("Picasso", "Image from firebase");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        /*CommonUtil commonUtil = new CommonUtil();
        return Uri.parse(commonUtil.compressImage(inContext, path));*/
        return Uri.parse(path);
    }

    public static String getRealPathFromURIForCamera(Uri uri, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURIForGallery(Uri uri, Context context) {
        String result = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        assert cursor != null;
        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            result = cursor.getString(columnIndex);
        }
        cursor.close();
        return result;
    }

    public static Bitmap capturedImage(Intent data) throws Exception {
        return (Bitmap) data.getExtras().get("data");
    }


    public static Bitmap selectedImageFromGallery(Activity activity, Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
