package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        int imageId = intent.getIntExtra(HomeActivity.IMAGE_ID, 0);

        ImageView imageView = findViewById(R.id.image_person_view);
        imageView.setImageResource(imageId);

    }
}
