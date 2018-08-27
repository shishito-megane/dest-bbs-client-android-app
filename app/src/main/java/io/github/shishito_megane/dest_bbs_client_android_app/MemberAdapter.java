package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MemberAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView imageViewMemberImage;
        TextView textViewMemberName;
        TextView textViewMemberStatus;
    }

    private List<String> mMemberNames;
    private List<Integer> mThumbIds;
    private List<String> mMemberStatus;
    private List<Integer> mMemberStatusColor;
    private LayoutInflater mLayoutInflater;
    private int layoutId;

    MemberAdapter(
            Context context,
            int layoutId,
            List<String> memberList,
            List<Integer> imageList,
            List<String> memberStatus,
            List<Integer> memberColorStatus
    ) {

        super();
        this.mLayoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        mThumbIds = imageList;
        mMemberNames = memberList;
        mMemberStatus = memberStatus;
        mMemberStatusColor = memberColorStatus;
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

        holder.imageViewMemberImage.setImageResource(mThumbIds.get(position));
        holder.textViewMemberName.setText(mMemberNames.get(position));
        holder.textViewMemberStatus.setText(mMemberStatus.get(position));
        holder.textViewMemberStatus.setBackgroundColor(mMemberStatusColor.get(position));

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