package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DeletePersonFlagment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get msg and set
        builder.setTitle(R.string.delete_person_dialog_title);
        builder.setMessage(R.string.delete_person_dialog_msg);

        // get person id
        final int personId = getArguments().getInt("personId");

        // ok button
        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // todo;
                        deletePerson(personId);

                        // display toast
                        Toast toast = Toast.makeText(
                                getActivity(),
                                R.string.finish_delete_person_toast,
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
                                R.string.cancelled_delete_person_toast,
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

    // DB
    public void deletePerson(
            int Id
    ) {

        DbHelper mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Log.d("DB", "ID:"+String.valueOf(Id));

        String deleteSelection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] deleteSelectionArgs = { String.valueOf(Id) };
        db.delete(
                DbContract.MemberTable.TABLE_NAME,
                deleteSelection,
                deleteSelectionArgs
        );

        Log.d("DB", "メンバー削除 ID:"+String.valueOf(Id));

        db.close();
        mDbHelper.close();
    }


}
