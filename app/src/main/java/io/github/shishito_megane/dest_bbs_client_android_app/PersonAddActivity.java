package io.github.shishito_megane.dest_bbs_client_android_app;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;

import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PersonAddActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential mCredential;
    private TextView textViewCalenderIdResult;
    private EditText editTextCalenderId;
    ProgressDialog mProgress;
    private String personName;
    private ImageView imageViewPersonImage;
    private TextView textViewPersonImageResult;

    static final int REQUEST_ACCOUNT_PICKER_ERR_CODE = 1000;
    static final int REQUEST_AUTHORIZATION_ERR_CODE = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS_ERR_CODE = 1003;

    static final int REQUEST_PICK_IMAGE_FILE = 2001;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};



    private Db db = new Db(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_add);

        // set default lab address
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String default_person_address = getString(R.string.pref_default_address);
        String address = prefs.getString("lab_address", default_person_address);

        // set default lab address
        final EditText editTextPersonAddress = findViewById(R.id.editTextPersonAddress);
        editTextPersonAddress.setText(address);

        // calender id
        final Button buttonCreateNewCalender = (Button)findViewById(R.id.buttonCreateNewCalender);
        editTextCalenderId = (EditText)findViewById(R.id.editTextCalenderId);
        textViewCalenderIdResult = (TextView)findViewById(R.id.textViewCalenderIdResult);
        final EditText editPersonName = (EditText)findViewById(R.id.editTextPersonName);

        // Google Calendar API を呼び出す
        buttonCreateNewCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonCreateNewCalender.setEnabled(false);
                textViewCalenderIdResult.setText("");
                editTextCalenderId.setText("");

                // get inputted personName
                personName = editPersonName.getText().toString();
                if (personName.length() < 1){
                    personName = getString(R.string.default_person_name);
                }

                // create new calender task
                // personName is calender title
                getResultsFromApi();

                buttonCreateNewCalender.setEnabled(true);
            }
        });

        // Google Calendar API の呼び出し中を表す ProgressDialog を準備する
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        // Google Calendar API の呼び出しのための認証情報を初期化する
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff());

        // サムネイル選択
        final Button buttonSelectImage = (Button)findViewById(R.id.buttonSelectImage);
        imageViewPersonImage = (ImageView)findViewById(R.id.imageViewPersonImage);
        textViewPersonImageResult = (TextView)findViewById(R.id.textViewPersonImageResult);

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
        EditText editTextCalenderId = (EditText)findViewById(R.id.editTextCalenderId);
        String personCalenderId = editTextCalenderId.getText().toString();
        String personThumbnail = textViewPersonImageResult.getText().toString();

        db.saveData(
                personName,
                personDetail,
                personThumbnail,
                personAddress,
                personCalenderId,
                getString(R.string.default_person_status)
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
     * Google Calendar API の呼び出しの事前条件を確認し、条件を満たしていればAPIを呼び出す。
     *
     * 事前条件：
     * - 有効な Google Play Services がインストールされていること
     * - 有効な Google アカウントが選択されていること
     * - 端末がインターネット接続可能であること
     *
     * 事前条件を満たしていない場合には、ユーザーに説明を表示する。
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            // Google Play Services が無効な場合
            acquireGooglePlayServices();
        }
        else if (mCredential.getSelectedAccountName() == null) {
            // 有効な Google アカウントが選択されていない場合
            chooseAccount();
        }
        else if (!isDeviceOnline()) {
            // 端末がインターネットに接続されていない場合
            textViewCalenderIdResult.setText(getString(R.string.no_internet_connection_msg));
        }
        else {
            new MakeRequestTask(mCredential, personName).execute();
        }
    }

    /**
     * 端末に Google Play Services がインストールされ、アップデートされているか否かを確認する。
     *
     * @return 利用可能な Google Play Services がインストールされ、アップデートされている場合にはtrueを、
     * そうでない場合にはfalseを返す。
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * ユーザーにダイアログを表示して、Google Play Services を利用可能な状態に設定するように促す。
     * ただし、ユーザーが解決できないようなエラーの場合には、ダイアログを表示しない。
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * 有効な Google Play Services が見つからないことをエラーダイアログで表示する。
     *
     * @param connectionStatusCode Google Play Services が無効であることを示すコード
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    /**
     * Google　Calendar API の認証情報を使用するGoogleアカウントを設定する。
     *
     * 既にGoogleアカウント名が保存されていればそれを使用し、保存されていなければ、
     * Googleアカウントの選択ダイアログを表示する。
     *
     * 認証情報を用いたGoogleアカウントの設定には、"GET_ACCOUNTS"パーミッションを
     * 必要とするため、必要に応じてユーザーに"GET_ACCOUNTS"パーミッションを要求する
     * ダイアログが表示する。
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS_ERR_CODE)
    private void chooseAccount() {
        // "GET_ACCOUNTS"パーミッションを取得済みか確認する
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            // SharedPreferencesから保存済みGoogleアカウントを取得する
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Googleアカウントの選択を表示する
                // GoogleAccountCredentialのアカウント選択画面を使用する
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER_ERR_CODE);
            }
        }
        else {
            // ダイアログを表示して、ユーザーに"GET_ACCOUNTS"パーミッションを要求する
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS_ERR_CODE,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * 呼び出し先のActivityから戻ってきた際に呼び出される。
     * REQUEST_GOOGLE_PLAY_SERVICES: google calender api
     * REQUEST_ACCOUNT_PICKER_ERR_CODE: google calender api
     * REQUEST_AUTHORIZATION_ERR_CODE: google calender api
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

            // google calender api
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    textViewCalenderIdResult.setText(
                            getString(R.string.install_google_play_service_msg)
                    );
                } else {
                    getResultsFromApi();
                }
                break;

            // google calender api
            case REQUEST_ACCOUNT_PICKER_ERR_CODE:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;

            // google calender api
            case REQUEST_AUTHORIZATION_ERR_CODE:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

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

    /**
     * Android 6.0 (API 23) 以降にて、実行時にパーミッションを要求した際の結果を受け取る。
     *
     * @param requestCode  requestPermissions(android.app.Activity, String, int, String[])
     *                     を呼び出した際に渡した　request code
     * @param permissions  要求したパーミッションの一覧
     * @param grantResults 要求したパーミッションに対する承諾結果の配列
     *                     PERMISSION_GRANTED または PERMISSION_DENIED　が格納される。
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 要求したパーミッションがユーザーに承諾された際に、EasyPermissionsライブラリから呼び出される。
     *
     * @param requestCode 要求したパーミッションに関連した request code
     * @param list        要求したパーミッションのリスト
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // 何もしない
    }

    /**
     * 要求したパーミッションがユーザーに拒否された際に、EasyPermissionsライブラリから呼び出される。
     *
     * @param requestCode 要求したパーミッションに関連した request code
     * @param list        要求したパーミッションのリスト
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 何もしない
    }

    /**
     * 現在、端末がネットワークに接続されているかを確認する。
     *
     * @return ネットワークに接続されている場合にはtrueを、そうでない場合にはfalseを返す。
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 非同期で　Google Calendar API の呼び出しを行うクラス。
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        String calenderTitle;

        public MakeRequestTask(
                GoogleAccountCredential credential,
                String calenderTitile
        ) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
            this.calenderTitle = calenderTitile;
        }

        /**
         * Google Calendar API を呼び出すバックグラウンド処理。
         *
         * @param params 引数は不要
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                return createCalendar();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * 選択されたGoogleアカウントに対して、新規にカレンダーを追加する。
         *
         * @return 作成したカレンダーのID
         * @throws IOException
         */
        private String createCalendar() throws IOException {
            // 新規にカレンダーを作成する
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();
            // カレンダーにタイトルを設定する
            calendar.setSummary(this.calenderTitle);
            // カレンダーにタイムゾーンを設定する
            calendar.setTimeZone("Asia/Tokyo");

            // 作成したカレンダーをGoogleカレンダーに追加する
            Calendar createdCalendar = mService.calendars().insert(calendar).execute();
            String calendarId = createdCalendar.getId();

            // カレンダー一覧から新規に作成したカレンダーのエントリを取得する
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // カレンダーのデフォルトの背景色を設定する
            calendarListEntry.setBackgroundColor("#ff0000");

            // カレンダーのデフォルトの背景色をGoogleカレンダーに反映させる
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // 新規に作成したカレンダーのIDを返却する
            return calendarId;
        }

        @Override
        protected void onPreExecute() {
            textViewCalenderIdResult.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            if (output == null || output.isEmpty()) {
                textViewCalenderIdResult.setText(getString(R.string.err_create_calender_msg));
            } else {
                textViewCalenderIdResult.setText(getString(R.string.result_create_calender));
                editTextCalenderId.setText(output);
            }
        }

        @Override
        protected void onCancelled() {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION_ERR_CODE);
                } else {
                    textViewCalenderIdResult.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                textViewCalenderIdResult.setText(getString(R.string.cancelled_create_calender));
            }
        }
    }
}
