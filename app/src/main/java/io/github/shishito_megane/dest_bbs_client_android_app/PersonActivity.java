package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class PersonActivity extends AppCompatActivity {

    int personId;
    String personName;
    int personImageId;
    String personDetail;
    String personCalender;
    String personStatus;

    private Db db = new Db(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        // get PERSON_ID
        Intent intent = getIntent();
        personId = intent.getIntExtra(
                HomeActivity.PERSON_ID,
                0
        );
        personName = intent.getStringExtra(
                HomeActivity.PERSON_NAME
        );
        personImageId = intent.getIntExtra(
                HomeActivity.PERSON_IMAGEID,
                0
        );
        Log.d("PersonActivity", "選択された人: "+ String.valueOf(personId));

        // set person image
        ImageView imageViewPersonImage = findViewById(R.id.imageViewPerson);
        imageViewPersonImage.setImageResource(personImageId);

        // set person name (person id)
        TextView textViewPersonID = findViewById(R.id.textViewPersonName);
        textViewPersonID.setText(personName);

        // get PERSON_DETAIL
        personDetail = db.getPersonDetail(personId);
        // set person name (person id)
        TextView textViewPersonDetail = findViewById(R.id.textViewPersonDetail);
        textViewPersonDetail.setText(personDetail);

        // get PERSON_CALENDER
        personCalender = db.getPersonCalender(personId);

        // get PERSON_STATUS
        personStatus = db.getPersonStatus(personId);

        // call button function
        Button dialogCall = findViewById(R.id.buttonCall);
        dialogCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CallDialogFlagment dialog = new CallDialogFlagment();
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
