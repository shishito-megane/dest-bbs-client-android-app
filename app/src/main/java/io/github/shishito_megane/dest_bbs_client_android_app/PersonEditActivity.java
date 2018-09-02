package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PersonEditActivity extends AppCompatActivity {

    int personId;
    String personName;
    String personDetail;
    String personCalender;
    String personAddress;
//    int personImageId;

    private Db db = new Db(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        // get person info
        Intent intent = getIntent();
        personId= intent.getIntExtra(PersonActivity.PERSON_ID, 0);
        personName = intent.getStringExtra(PersonActivity.PERSON_NAME);
        personDetail = intent.getStringExtra(PersonActivity.PERSON_DETAIL);
        personCalender = intent.getStringExtra(PersonActivity.PERSON_CALENDER);
        personAddress = intent.getStringExtra(PersonActivity.PERSON_ADDRESS);
//        personImageId = intent.getIntExtra(PersonActivity.PERSON_IMAGEID, 0);

        // set person info
        EditText editPersonName = (EditText)findViewById(R.id.editTextPersonName);
        editPersonName.setText(personName);
        EditText editPersonDetail = (EditText)findViewById(R.id.editTextPersonDetail);
        editPersonDetail.setText(personDetail);
        TextView textViewPersonCalender = (TextView)findViewById(R.id.textViewTextCalenderId);
        textViewPersonCalender.setText(personCalender);
        EditText editPersonAddress = (EditText)findViewById(R.id.editTextPersonAddress);
        editPersonAddress.setText(personAddress);
//        TextView textViewSelectThumbnail = (TextView)findViewById(R.id.textViewSelectThumbnail);
//        textViewSelectThumbnail.setText(personImageId);



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personaddactivity_action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get person info
        EditText editPersonName = (EditText)findViewById(R.id.editTextPersonName);
        String personName = editPersonName.getText().toString();
        EditText editPersonAddress = (EditText)findViewById(R.id.editTextPersonAddress);
        String personAddress = editPersonAddress.getText().toString();
        EditText editTextPersonDetail = (EditText)findViewById(R.id.editTextPersonDetail);
        String personDetail = editTextPersonDetail.getText().toString();
        TextView textViewCalenderId = (TextView)findViewById(R.id.textViewTextCalenderId);
        String personCalenderId = textViewCalenderId.getText().toString();
//        TextView textViewSelectThumbnail = (TextView)findViewById(R.id.textViewSelectThumbnail);
//        String personImageId = textViewSelectThumbnail.getText().toString();

        db.updatePersonInfo(
                personId,
                personName,
                personDetail,
                "dog_1",
                personAddress,
                personCalenderId
        );

        // display toast
        Toast toast = Toast.makeText(
                this,
                R.string.finish_person_add_register,
                Toast.LENGTH_SHORT
        );
        toast.show();

        // Return HomeActivity and finish this activity
        Intent intent = new Intent(
                this,
                HomeActivity.class
        );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        return true;
    }
}
