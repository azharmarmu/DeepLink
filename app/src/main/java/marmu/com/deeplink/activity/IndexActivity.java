package marmu.com.deeplink.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import marmu.com.deeplink.R;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
    }

    public void getStarted(View view) {
        startActivity(new Intent(IndexActivity.this, LoginActivity.class));
        finish();
    }
}
