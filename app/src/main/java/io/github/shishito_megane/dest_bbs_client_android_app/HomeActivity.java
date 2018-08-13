package io.github.shishito_megane.dest_bbs_client_android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decor = this.getWindow().getDecorView();

        // set welcome msg
        String lab_name = getString(R.string.lab_name);
        String welcome_msg = getString(R.string.welcome_msg, lab_name);

        // display welcome msg
        TextView textView = findViewById(R.id.textViewWelcomeMsg);
        textView.setText(welcome_msg);

        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
