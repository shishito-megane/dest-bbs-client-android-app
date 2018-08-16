package io.github.shishito_megane.dest_bbs_client_android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // 表示する画像の名前（拡張子無し）
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

    // Resource IDを格納するarray
    private List<Integer> imgList = new ArrayList<>();


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

        // hide navigation var
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // for-each member名をR.drawable.名前としてintに変換してarrayに登録
        for (String member : members) {
            int imageId = getResources().getIdentifier(
                    member, "drawable", getPackageName());
            imgList.add(imageId);
        }

        // GridViewのインスタンスを生成
        GridView gridview = findViewById(R.id.gridViewMember);
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
        MemberAdapter adapter = new MemberAdapter(
                this.getApplicationContext(),
                R.layout.grid_item_member,
                imgList,
                members
        );

        // gridViewにadapterをセット
        gridview.setAdapter(adapter);

//        // set grid view
//        GridView gridView = (GridView) findViewById(R.id.gridViewMember);
//        gridView.setAdapter(new MemberAdapter(this));

    }


}


