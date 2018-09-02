package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class PersonActivity extends AppCompatActivity {

    public static final String PERSON_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ID";
    public static final String PERSON_NAME = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_NAME";
    public static final String PERSON_DETAIL = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_DETAIL";
    public static final String PERSON_CALENDER = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_CALENDER";
    public static final String PERSON_ADDRESS = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ADDRESS";
    public static final String PERSON_IMAGE_URI = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_IMAGE_URI";

    int personId;
    String personName;
    Uri personImageUri;
    String personDetail;
    String personCalender;
    String personStatus;
    String personAddress;

    private Db db = new Db(this);
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        // get person info
        Intent intent = getIntent();
        personId = intent.getIntExtra(
                HomeActivity.PERSON_ID,
                0
        );
        personName = intent.getStringExtra(
                HomeActivity.PERSON_NAME
        );
        personImageUri = Uri.parse(intent.getStringExtra(HomeActivity.PERSON_IMAGE_URI));
        personDetail = db.getPersonDetail(personId);
        personCalender = db.getPersonCalender(personId);
        personAddress = db.getPersonAddress(personId);
        personStatus = db.getPersonStatus(personId);
        Log.d("PersonActivity", "選択された人: "+ String.valueOf(personId));

        // set person info
        TextView textViewPersonID = findViewById(R.id.textViewPersonName);
        textViewPersonID.setText(personName);
        TextView textViewPersonDetail = findViewById(R.id.textViewPersonDetail);
        textViewPersonDetail.setText(personDetail);
        ImageView imageViewPersonImage = findViewById(R.id.imageViewPerson);
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), personImageUri
            );
            imageViewPersonImage.setImageBitmap(bitmap);
        }catch (IOException e){
            e.printStackTrace();
            int memberImageIdInt = getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    getPackageName()
            );
            imageViewPersonImage.setImageResource(memberImageIdInt);
        }catch (SecurityException e){
            e.printStackTrace();
            int memberImageIdInt = getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    getPackageName()
            );
            imageViewPersonImage.setImageResource(memberImageIdInt);
        }

        // call button function
        Button dialogCall = findViewById(R.id.buttonCall);
        dialogCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CallDialogFlagment dialog = new CallDialogFlagment();
                Bundle args = new Bundle();
                args.putString("personName", personName);
                args.putString("personAddress", personAddress);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "call");
            }
        });

        // Its me (update status) button function
        Button dialogItsMe = findViewById(R.id.buttonItsMe);
        dialogItsMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpdateStatusDialogFlagment dialog = new UpdateStatusDialogFlagment();
                Bundle args = new Bundle();
                args.putInt("personId", personId);
                args.putString("personCalender", personCalender);
                args.putString("personStatus", personStatus);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "itsme");
            }
        });

        // for each 3 min after
        handler.postDelayed(r, 1000 * 60 * 3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decor = this.getWindow().getDecorView();

        // hide navigation bar & status bar
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    final Runnable r = new Runnable() {
        @Override
        public void run() {

            // Return HomeActivity and finish this activity
            Intent intent = new Intent(
                    getApplication(),
                    HomeActivity.class
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    };

    // set Menu on the Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personactivity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // process when menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.menuPersonEdit:
                Intent intent_edit = new Intent(
                        this,
                        PersonEditActivity.class
                );
                intent_edit.putExtra(PERSON_ID, personId);
                intent_edit.putExtra(PERSON_NAME, personName);
                intent_edit.putExtra(PERSON_DETAIL, personDetail);
                intent_edit.putExtra(PERSON_CALENDER, personCalender);
                intent_edit.putExtra(PERSON_ADDRESS, personAddress);
                intent_edit.putExtra(PERSON_IMAGE_URI, personImageUri.toString());
                startActivity(intent_edit);
                return true;
            case R.id.menuPersonDelete:
                DeletePersonFlagment dialog = new DeletePersonFlagment();
                Bundle args = new Bundle();
                args.putInt("personId", personId);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "delete_person");
                return true;
            case R.id.menuHelp:
                Intent intent_help = new Intent(
                        this,
                        HelpActivity.class
                );
                startActivity(intent_help);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
