package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CallDialogFlagment extends DialogFragment {

    SendMailTask sender;

    String personAddress;
    String personName;

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get msg and set
        builder.setTitle(R.string.call_dialog_title);
        builder.setMessage(R.string.call_dialog_msg);

        // get ags
        personAddress = getArguments().getString("personAddress");
        personName = getArguments().getString("personName");

        // ok button
        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                sender = new SendMailTask(
                        getActivity(),
                        personName,
                        personAddress
                );
                sender.execute();

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

                // Return HomeActivity and finish this activity
                Intent intent = new Intent(
                        getActivity(),
                        HomeActivity.class
                );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        return builder.create();
    }
}
