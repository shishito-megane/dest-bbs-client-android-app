package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String PERSON_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ID";
    public static final String PERSON_NAME = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_NAME";
    public static final String PERSON_IMAGEID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_IMAGEID";

    private Db db = new Db(this);
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set welcome msg
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String default_lab_name = getString(R.string.pref_default_lab_name);
        String lab_name = prefs.getString("lab_name", default_lab_name);
        String welcome_msg = getString(R.string.welcome_msg, lab_name);

        // display welcome msg
        TextView textView = findViewById(R.id.textViewWelcomeMsg);
        textView.setText(welcome_msg);

        // test calender
        preCheckCalender();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // hide navigation bar & status bar
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        handler.post(r);
    }

    // set menu on the Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homeactivity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // process when menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.menuPersonAdd:
                Intent intent_person_add = new Intent(
                        this,
                        PersonAddActivity.class
                );
                startActivity(intent_person_add);
                return true;
            case R.id.menuSetting:
                // transition HomeActivity
                Intent intent_setting = new Intent(
                        this,
                        SettingsActivity.class
                );
                startActivity(intent_setting);
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

    // run for each reload Activity
    final Runnable r = new Runnable() {
        @Override
        public void run() {

            // get now hour
            int  nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            // check status and update
            // 11時になっても帰宅中の人は遅刻にする
            if( nowHour == 11 ){
                db.updatePersonStatusLate();
            }
            // 15時になったら遅刻は帰宅にする
            else if (nowHour == 15){
                db.updatePersonStatusAbsence();
            }
            // 4時になったら全員帰宅にする
            else if (nowHour == 4){
                db.updatePersonStatusGohome();
            }

            // get member ids
            List<Integer> memberIdListTmp = db.getMemberIdList();
            if (memberIdListTmp.size() == 0){
                db.saveData(
                        getString(R.string.default_person_name),
                        getString(R.string.default_person_detail),
                        getString(R.string.default_person_image_id),
                        getString(R.string.pref_default_address),
                        getString(R.string.default_person_calendar),
                        getString(R.string.default_person_status)
                );
                memberIdListTmp = db.getMemberIdList();
            }
            final List<Integer> memberIdList = memberIdListTmp;

            // get member info
            final List<String> memberNameList = db.getMemberNameList();
            final List<Integer> memberImageList = db.getMemberImageList();
            final List<String> memberStatusList = db.getMemberStatusList();

            // generate member status color list
            final List<Integer> memberStatusColorList = db.generatorMemberStatusColorList(
                    memberStatusList
            );

            // generation GridView instance
            GridView gridview = findViewById(R.id.gridViewMember);

            // reload this grid view, MemberAdapter
            MemberAdapter adapter = new MemberAdapter(
                    getBaseContext(),
                    R.layout.grid_item_member,
                    memberNameList,
                    memberImageList,
                    memberStatusList,
                    memberStatusColorList
            );
            gridview.setAdapter(adapter);

            // create a message handling object as an anonymous class.
            AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
                public void onItemClick(
                        AdapterView parent,
                        View v,
                        int position,
                        long id
                ) {
                    Intent intent = new Intent(getApplication(), PersonActivity.class);
                    intent.putExtra(PERSON_ID, memberIdList.get(position));
                    intent.putExtra(PERSON_NAME, memberNameList.get(position));
                    intent.putExtra(PERSON_IMAGEID, memberImageList.get(position));
                    startActivity( intent );
                }
            };

            gridview.setOnItemClickListener(mMessageClickedHandler);

            // for each 10 min
            handler.postDelayed(this, 1000 * 60 * 10);
        }
    };

    // calender
    private void preCheckCalender() {

        // No Internet connection
        if (!isDeviceOnline()) {
            // display toast
            Toast toast = Toast.makeText(
                    this,
                    R.string.no_internet_connection_msg,
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
        else {
            GetCalenderListTask calender = new GetCalenderListTask(this);
            calender.execute();
        }
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
}


