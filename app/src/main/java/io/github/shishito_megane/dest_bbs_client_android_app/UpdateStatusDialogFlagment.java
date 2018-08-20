package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UpdateStatusDialogFlagment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){

        int defaultItem = 0;
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.update_enter_room_title);

        builder.setSingleChoiceItems(
                R.array.status_entering_room_array,
                defaultItem,
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                checkedItems.clear();
                checkedItems.add(which);
                // The 'which' argument contains the index position
                // of the selected item
            }
        });

        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // nothing checked err
                if (!checkedItems.isEmpty()) {
                    Log.d("checkedItem:", "" + checkedItems.get(0));
                }

                // TODO; add recording function

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.finish_update_status,
                        Toast.LENGTH_SHORT
                );
                toast.show();

                // transition HomeActivity
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                startActivity(intent);
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

                // transition HomeActivity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });

        builder.show();

        return builder.create();
    }
}
