package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView memberImageView;
        TextView memberTextView;
    }

    private List<String> mMemberNames;
    private List<Integer> mThumbIds;
    private LayoutInflater mLayoutInflater;
    private int layoutId;

    MemberAdapter(
            Context context,
            int layoutId,
            List<String> memberList,
            List<Integer> imageList) {

        super();
        this.mLayoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        mThumbIds = imageList;
        mMemberNames = memberList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(
//                    R.layout.grid_item_member,
                    layoutId,
                    parent,
                    false
            );
            holder = new ViewHolder();
            holder.memberImageView = convertView.findViewById(
                    R.id.member_imageview
            );
            holder.memberTextView = convertView.findViewById(
                    R.id.member_textview
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.memberImageView.setImageResource(mThumbIds.get(position));
        holder.memberTextView.setText(mMemberNames.get(position));

        return convertView;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}