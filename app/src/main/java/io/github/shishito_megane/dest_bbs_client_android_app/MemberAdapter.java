package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MemberAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView imageViewMemberImage;
        TextView textViewMemberName;
        TextView textViewMemberStatus;
    }

    private List<String> mMemberNames;
    private List<Uri> mThumbUris;
    private List<String> mMemberStatus;
    private List<Integer> mMemberStatusColor;
    private LayoutInflater mLayoutInflater;
    private int layoutId;
    private Context context;

    MemberAdapter(
            Context context,
            int layoutId,
            List<String> memberList,
            List<Uri> imageList,
            List<String> memberStatus,
            List<Integer> memberColorStatus
    ) {

        super();
        this.mLayoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.context = context;
        mThumbUris = imageList;
        mMemberNames = memberList;
        mMemberStatus = memberStatus;
        mMemberStatusColor = memberColorStatus;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(
                    layoutId,
                    parent,
                    false
            );
            holder = new ViewHolder();
            holder.imageViewMemberImage = convertView.findViewById(
                    R.id.textViewMemberImage
            );
            holder.textViewMemberName = convertView.findViewById(
                    R.id.textViewMemberName
            );
            holder.textViewMemberStatus = convertView.findViewById(
                    R.id.textViewMemberStatus
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // set person info
        holder.textViewMemberName.setText(mMemberNames.get(position));
        holder.textViewMemberStatus.setText(mMemberStatus.get(position));
        holder.textViewMemberStatus.setBackgroundColor(mMemberStatusColor.get(position));
        // set person image (thumbnail)
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    this.context.getContentResolver(), mThumbUris.get(position)
            );
            holder.imageViewMemberImage.setImageBitmap(bitmap);
        }catch (IOException e){
            e.printStackTrace();
            int memberImageIdInt = this.context.getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    this.context.getPackageName()
            );
            holder.imageViewMemberImage.setImageResource(memberImageIdInt);
        }catch (SecurityException e){
            e.printStackTrace();
            int memberImageIdInt = this.context.getResources().getIdentifier(
                    "dog_1",
                    "drawable",
                    this.context.getPackageName()
            );
            holder.imageViewMemberImage.setImageResource(memberImageIdInt);
        }

        return convertView;
    }

    public int getCount() {
        return mThumbUris.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}