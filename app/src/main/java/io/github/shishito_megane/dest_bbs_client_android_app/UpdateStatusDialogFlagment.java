package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UpdateStatusDialogFlagment extends DialogFragment {

    StatusRegistrationCalenderTask register;

    int personId;
    String personCalender;
    String oldStatusCode;
    String newStatusCode;

    public Dialog onCreateDialog(Bundle savedInstanceState){

        int defaultItem = 0;
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get ags
        personId = getArguments().getInt("personId");
        personCalender = getArguments().getString("personCalender");
        oldStatusCode = getArguments().getString("personStatus");

        // if status code is "入室"
        if (oldStatusCode.equals("入室")){
            builder.setTitle(R.string.update_leaving_room_title);
            builder.setSingleChoiceItems(
                    R.array.status_leaving_room_list,
                    defaultItem,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    }
            );
        }
        else {
            builder.setTitle(R.string.update_entering_room_title);
            builder.setSingleChoiceItems(
                    R.array.status_entering_room_list,
                    defaultItem,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    }
            );
        }

        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // nothing checked err
                if (!checkedItems.isEmpty()) {
                    Log.d("checkedItem:", "" + checkedItems.get(0));

                    // get checked item
                    int checkedItemInt = checkedItems.get(0);

                    Resources res = getResources();
                    TypedArray array = res.obtainTypedArray(R.array.status_code_array);

                    // get new status code
                    if (oldStatusCode.equals("入室")){
                        // XXX: 入退室で項目の数が違う分，ずらす
                        newStatusCode = array.getString(checkedItemInt + 1);
                    }
                    else {
                        newStatusCode = array.getString(checkedItemInt);
                    }

                    array.recycle();

                    // add recording function
                    // DB
                    Db db = new Db(getActivity());
                    db.updatePersonStatus(
                            personId,
                            newStatusCode
                    );
                    // calender
                    register = new StatusRegistrationCalenderTask(
                            personCalender,
                            newStatusCode,
                            getActivity()
                    );
                    register.setListener(createListener());
                    register.execute();

                    // Return HomeActivity(new created) and finish this activity
                    Intent intent = new Intent(getActivity(),HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Log.w("checkedItem:", "なし");
                }
            }
        });

        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.cancelled_update_status,
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        });

        return builder.create();
    }

    private StatusRegistrationCalenderTask.Listener createListener() {

        return new StatusRegistrationCalenderTask.Listener() {

            @Override
            public void onSuccess() {

//                Log.d("カレンダー", "在室状況を記録" );
//
//                // display toast
//                Toast toast = Toast.makeText(
//                        getActivity(),
//                        R.string.finish_update_status,
//                        Toast.LENGTH_SHORT
//                );
//                toast.show();
            }

            @Override
            public void onCancelled() {

//                Log.d("カレンダー", "在室状況の記録をキャンセル" );
//
//                // display toast
//                Toast toast = Toast.makeText(
//                        getActivity(),
//                        R.string.cancelled_update_status,
//                        Toast.LENGTH_SHORT
//                );
//                toast.show();
            }
        };
    }
}
