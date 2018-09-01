package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


public class SendMailTask extends AsyncTask<Void, Void, Void> {

    private String personAddress;
    private String personName;
    private Context context;

    SendMailTask(
            Context context,
            String personName,
            String personAddress
    ){
        super();
        this.context = context;
        this.personName = personName;
        this.personAddress = personAddress;
    }

    // 非同期処理
    @Override
    protected Void doInBackground(Void... params) {

        sendMail();

        return null;
    }

    private void sendMail() {

        // XXX: この方法だとメール送信結果がわからず，送信完了後の画面遷移もできない．

        // メールの内容設定
        String recipient = this.personAddress;
        String appName = this.context.getString(R.string.app_name);
        // note: ここのエラーはLinerがうまく認識できていないだけなので無視してよい
        String subject = this.context.getString(R.string.email_subject, appName, this.personName);
        String body = this.context.getString(R.string.email_body);

        Intent emailIntent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts(
                        "mailto",
                        recipient,
                        null
                )
        );
        // The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        this.context.startActivity(emailIntent);
    }

    @Override
    protected void onPreExecute() {
        Log.d("Mail", "タスクを開始");
    }

    @Override
    protected void onPostExecute(Void tmp) {

        if (listener != null) {
            listener.onSuccess();
        }

        Log.d("Mail", "タスクを終了" );

    }

    @Override
    protected void onCancelled() {

        if (listener != null) {
            listener.onCancelled();
        }

        Log.d("Mail", "タスクを中止" );
    }

    private Listener listener;

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess();
        void onCancelled();
    }
}