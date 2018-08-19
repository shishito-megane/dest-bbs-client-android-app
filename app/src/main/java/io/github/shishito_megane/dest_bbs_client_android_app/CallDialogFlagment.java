package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CallDialogFlagment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get msg and set
        builder.setTitle(R.string.call_dialog_title);
        builder.setMessage(R.string.call_dialog_msg);

        // ok button
        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // todo; add calling function

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.finish_calling_toast,
                        Toast.LENGTH_SHORT
                );
                toast.show();

                // transition HomeActivity
                Intent intent = new Intent(
                        getActivity(),
                        HomeActivity.class
                );
                startActivity(intent);
            }
        });

        // cancel button
        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.cancelled_calling_toast,
                        Toast.LENGTH_SHORT
                );
                toast.show();

                // transition HomeActivity
                Intent intent = new Intent(
                        getActivity(),
                        HomeActivity.class
                );
                startActivity(intent);
            }
        });

        return builder.create();
    }
}
