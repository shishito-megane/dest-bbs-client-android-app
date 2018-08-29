package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Db {

    private Context context;

    public Db(
            Context context
    ){
        this.context = context;
    }

    /**
     * DBから登録されているMemberIDを取得します
     * @return list MemberID のリスト
     */
    public List<Integer> getMemberIdList() {

        List<Integer> memberIdList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID
        };

        Cursor cursor = writer.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーIDのレコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            int memberID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.ID)
            );
            memberIdList.add(memberID);
        }

        cursor.close();
        writer.close();
        mDbHelper.close();

        return memberIdList;
    }

    /**
     * DBから登録されているMemberNameを取得します
     * @return list MemberNameのリスト
     */
    public List<String> getMemberNameList() {

        List<String> memberNameList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_NAME
        };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーの名前のレコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_NAME)
            );
            memberNameList.add(memberName);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberNameList;
    }

    /**
     * DBから登録されているMemberImageを取得します
     * @return list MemberImageのリスト
     */
    public List<Integer> getMemberImageList() {

        List<Integer> memberImageList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_IMAGE,
        };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーの画像のレコード数:"+String.valueOf(cursor.getCount()));

        // convert memberID to R.drawable.XX and convert to int, register array
        // member名をR.drawable.名前としてintに変換してarrayに登録
        // Resource ID (Member Image ID)
        while(cursor.moveToNext()) {
            String memberImageIdString = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_IMAGE)
            );
            int memberImageIdInt = this.context.getResources().getIdentifier(
                    memberImageIdString,
                    "drawable",
                    this.context.getPackageName()
            );
            memberImageList.add(memberImageIdInt);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberImageList;
    }

    /**
     * 登録されているMemberStatusを取得します
     * @return list MemberStatusのリスト
     */
    public List<String> getMemberStatusList() {

        List<String> memberStatusList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_STATUS,
        };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーの在室状況のレコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberStatus = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_STATUS)
            );
            memberStatusList.add(memberStatus);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberStatusList;
    }

    /**
     * MemberStatusのリストを元に，対応する色のリストを作成します
     * @param memberStatusList getMemberStatusList()で作成したリスト
     * @return list 色のリスト
     */
    public List<Integer> generatorMemberStatusColorList(
            List<String> memberStatusList
    ){
        List<Integer> memberStatusListColor  = new ArrayList<>();

        int color;

        for (String status : memberStatusList) {
            if (status.equals("入室")){
                color = this.context.getResources().getColor(R.color.colorTypeA);
            }
            else{
                color = this.context.getResources().getColor(R.color.colorTypeB);
            }

            memberStatusListColor.add(color);
        }

        return memberStatusListColor;
    }

    /**
     * DBに新しいメンバーの情報を登録します
     * @param name メンバーの名前
     * @param detail メンバーの自己紹介文
     * @param image メンバーの画像のファイル名
     * @param address メンバーの連絡先
     * @param calender メンバーのGoogleCalenderID
     * @param status メンバーの在室状況
     */
    public void saveData(
            String name,
            String detail,
            String image,
            String address,
            String calender,
            String status
    ){
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("detail", detail);
        values.put("image", image);
        values.put("address", address);
        values.put("calender", calender);
        values.put("status", status);

        long newRowId = writer.insert("member", null, values);

        Log.d("DB", "挿入:"+name+String.valueOf(newRowId));

        writer.close();
        mDbHelper.close();
    }

    /**
     *   MemberTable.COLUMN_STATUS が 帰宅 になってる人を 遅刻 にします
     */
    public void updatePersonStatusLate() {

        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.MemberTable.COLUMN_STATUS, "遅刻");

        String selection = DbContract.MemberTable.COLUMN_STATUS + " = ?";
        String[] selectionArgs = { "帰宅" };

        writer.update(
                DbContract.MemberTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        writer.close();
        mDbHelper.close();
    }

    /**
     *  MemberTable.COLUMN_STATUS が 遅刻 になってる人を 欠席 にします
     */
    public void updatePersonStatusAbsence() {

        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.MemberTable.COLUMN_STATUS, "欠席");

        String selection = DbContract.MemberTable.COLUMN_STATUS + " = ?";
        String[] selectionArgs = { "遅刻" };

        writer.update(
                DbContract.MemberTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        writer.close();
        mDbHelper.close();
    }

    /**
     *  MemberTable.COLUMN_STATUS を 帰宅 にします
     */
    public void updatePersonStatusGohome() {

        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.MemberTable.COLUMN_STATUS, "帰宅");

        writer.update(
                DbContract.MemberTable.TABLE_NAME,
                values,
                null,
                null
        );

        writer.close();
        mDbHelper.close();
    }

    /**
     * 引数のIdで指定された人の自己紹介文を取得します
     * @param Id 指定された人のId
     * @return string 自己紹介
     */
    public String getPersonDetail(
            int Id
    ) {

        List<String> memberDetailList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_DETAIL,
        };
        String selection = DbContract.MemberTable.ID + " = ?";
        String[] selectionArgs = { String.valueOf(Id) };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // get total records value
        int contentValue = cursor.getCount();
        Log.d("DB", "該当メンバーの詳細のレコード数:"+String.valueOf(contentValue));

        // check
        if (contentValue == 1){
            Log.d("DB", "総数が1なので良い" );
        } else {
            Log.e("DB", "総数が1ではない" );
        }

        while(cursor.moveToNext()) {
            String memberDetail = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_DETAIL)
            );
            memberDetailList.add(memberDetail);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberDetailList.get(0);
    }

    /**
     * 引数のIdで指定された人のCalenderIdを取得します
     * @param Id 指定された人のId
     * @return string CalenderId
     */
    public String getPersonCalender(
            int Id
    ) {

        List<String> memberCalenderList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_CALENDAR,
        };
        String selection = DbContract.MemberTable.ID + " = ?";
        String[] selectionArgs = { String.valueOf(Id) };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // get total records value
        int contentValue = cursor.getCount();
        Log.d("DB", "該当メンバーのカレンダーのレコード数:"+String.valueOf(contentValue));

        // check
        if (contentValue == 1){
            Log.d("DB", "総数が1なので良い" );
        } else {
            Log.e("DB", "総数が1ではない" );
        }

        while(cursor.moveToNext()) {
            String memberCalender = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_CALENDAR)
            );
            memberCalenderList.add(memberCalender);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberCalenderList.get(0);
    }

    /**
     * 引数のIdで指定された人のStatusを取得します
     * @param Id 指定された人のId
     * @return string Status
     */
    public String getPersonStatus(
            int Id
    ) {

        List<String> memberStatusList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_STATUS,
        };
        String selection = DbContract.MemberTable.ID + " = ?";
        String[] selectionArgs = { String.valueOf(Id) };

        Cursor cursor = reader.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // get total records value
        int contentValue = cursor.getCount();
        Log.d("DB", "該当メンバーの在室状況のレコード数:"+String.valueOf(contentValue));

        // check
        if (contentValue == 1){
            Log.d("DB", "総数が1なので良い" );
        } else {
            Log.e("DB", "総数が1ではない" );
        }

        while(cursor.moveToNext()) {
            String memberCalender = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_STATUS)
            );
            memberStatusList.add(memberCalender);
        }

        cursor.close();
        reader.close();
        mDbHelper.close();

        return memberStatusList.get(0);
    }

    /**
     * 引数のIdで指定された人のStatusを更新します
     *@param Id 指定された人のId
     * @param statusCode 変更後のStatus
     */
    public void updatePersonStatus(
            int Id,
            String statusCode
    ) {

        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.MemberTable.COLUMN_STATUS, statusCode);

        String selection = DbContract.MemberTable.ID + " = ?";
        String[] selectionArgs = { String.valueOf(Id) };

        writer.update(
                DbContract.MemberTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        writer.close();
        mDbHelper.close();
    }

    /**
     * 引数のIdで指定された人を削除します
     * @param Id 指定された人のId
     */
    public void deletePerson(
            int Id
    ) {

        DbHelper mDbHelper = new DbHelper(this.context);
        SQLiteDatabase writer = mDbHelper.getWritableDatabase();

        String deleteSelection = DbContract.MemberTable.ID + " = ?";
        String[] deleteSelectionArgs = { String.valueOf(Id) };

        writer.delete(
                DbContract.MemberTable.TABLE_NAME,
                deleteSelection,
                deleteSelectionArgs
        );

        Log.d("DB", "メンバー削除 ID: "+String.valueOf(Id));

        writer.close();
        mDbHelper.close();
    }
}
