package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PersonEditActivity extends AppCompatActivity {

    int personId;
    String personName;
    String personDetail;
    String personCalender;
    String personAddress;
    Uri personImageUri;
    private ImageView imageViewPersonImage;
    private TextView textViewPersonImageResult;

    static final int REQUEST_PICK_IMAGE_FILE = 2001;

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
        personImageUri = Uri.parse(intent.getStringExtra(PersonActivity.PERSON_IMAGE_URI));

        // set person info
        EditText editPersonName = (EditText)findViewById(R.id.editTextPersonName);
        editPersonName.setText(personName);
        EditText editPersonDetail = (EditText)findViewById(R.id.editTextPersonDetail);
        editPersonDetail.setText(personDetail);
        TextView textViewPersonCalender = (TextView)findViewById(R.id.textViewTextCalenderId);
        textViewPersonCalender.setText(personCalender);
        EditText editPersonAddress = (EditText)findViewById(R.id.editTextPersonAddress);
        editPersonAddress.setText(personAddress);
        imageViewPersonImage = (ImageView)findViewById(R.id.imageViewPersonImage);
        textViewPersonImageResult = (TextView)findViewById(R.id.textViewPersonImageResult);
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), personImageUri
            );
            imageViewPersonImage.setImageBitmap(bitmap);

            textViewPersonImageResult.setText(personImageUri.toString());
        }catch (IOException e){
            e.printStackTrace();
            int memberImageIdInt = getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    getPackageName()
            );
            imageViewPersonImage.setImageResource(memberImageIdInt);
            textViewPersonImageResult.setText(R.string.cannot_find_thumbnail_msg);
        }catch (SecurityException e){
            e.printStackTrace();
            int memberImageIdInt = getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    getPackageName()
            );
            imageViewPersonImage.setImageResource(memberImageIdInt);
            textViewPersonImageResult.setText(R.string.cannot_find_thumbnail_msg);
        }

        // サムネイル選択
        final Button buttonSelectImage = (Button)findViewById(R.id.buttonSelectImage);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                textViewPersonImageResult.setText("");

                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_PICK_IMAGE_FILE);
            }
        });

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
        String personImageUri = textViewPersonImageResult.getText().toString();

        db.updatePersonInfo(
                personId,
                personName,
                personDetail,
                personImageUri,
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
    /**
     * 呼び出し先のActivityから戻ってきた際に呼び出される。
     * REQUEST_PICK_IMAGE_FILE: person image (thumbnail) select
     *
     * @param requestCode Activityの呼び出し時に指定したコード
     * @param resultCode  呼び出し先のActivityでの処理結果を表すコード
     * @param data        呼び出し先のActivityでの処理結果のデータ
     */
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // person image
            case REQUEST_PICK_IMAGE_FILE:
                if (resultCode == Activity.RESULT_OK && data.getData() != null) {
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageViewPersonImage.setImageBitmap(bitmap);
                        textViewPersonImageResult.setText(uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
