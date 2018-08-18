package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String PERSON_IMAGE_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_IMAGE_ID";
    public static final String PERSON_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ID";

    // member ID (person name) （no extension）
    private String members[] = {
            "sample_0",
            "sample_1",
            "sample_2",
            "sample_3",
            "sample_4",
            "sample_5",
            "sample_6",
            "sample_7",
    };

    // Resource ID (Member Image ID)
    private List<Integer> imgList = new ArrayList<>();

    // Member ID
    private List<String> memberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hide navigation var
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // set welcome msg
        String lab_name = getString(R.string.lab_name);
        String welcome_msg = getString(R.string.welcome_msg, lab_name);

        // display welcome msg
        TextView textView = findViewById(R.id.textViewWelcomeMsg);
        textView.setText(welcome_msg);

        // for-each, convert memberID to R.drawable.XX and convert to int, register array
        // for-each, member名をR.drawable.名前としてintに変換してarrayに登録
        for (String member : members) {
            int imageId = getResources().getIdentifier(
                    member, "drawable", getPackageName());
            imgList.add(imageId);
        }
        // for-each, registration memberID to array
        for (String member : members) {
            String memberId = member;
            memberList.add(memberId);
        }

        // generation GridView instance
        GridView gridview = findViewById(R.id.gridViewMember);

        // generation GridAdapter instance, inherited BaseAdapter
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
        MemberAdapter adapter = new MemberAdapter(
                this.getApplicationContext(),
                R.layout.grid_item_member,
                imgList,
                members
        );

        // set gridView adapter
        gridview.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(
                    AdapterView parent,
                    View v,
                    int position,
                    long id
            ) {
                Intent intent = new Intent(getApplication(), PersonActivity.class);
                intent.putExtra(PERSON_IMAGE_ID, imgList.get(position));
                intent.putExtra(PERSON_ID, memberList.get(position));
                startActivity( intent );
            }
        };

        gridview.setOnItemClickListener(mMessageClickedHandler);

    }

    // メニューをActivity上に設置する
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.menuSetting:
                // transition HomeActivity
                Intent intent_setting = new Intent(
                        this,
                        SettingActivity.class
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
}


