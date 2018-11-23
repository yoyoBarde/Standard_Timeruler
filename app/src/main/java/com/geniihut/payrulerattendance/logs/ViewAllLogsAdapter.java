package com.geniihut.payrulerattendance.logs;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import java.sql.Time;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by icti on 2/27/15.
 */
public class ViewAllLogsAdapter extends CursorAdapter implements
        StickyListHeadersAdapter {
    Activity context;
    private LayoutInflater mInflater;

    public ViewAllLogsAdapter(Activity context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    public ViewAllLogsAdapter(Activity context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = super.getView(position, view, parent);
        if (position % 2 == 0) {
            view.setBackgroundResource(R.color.table_cell_one);
        } else {
            view.setBackgroundResource(R.color.table_cell_two);
        }

        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_view_all_logs, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        User user = UserContract.User.getUser(cursor);//TimeInContract.TimeIn.getTimeIn(cursor);
        TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(cursor);
        viewHolder.tvCell1.setText(user.getIdno());
        viewHolder.tvCell2.setText(user.getFullName());
        viewHolder.tvCell3.setText(timeIn.getTime());
        viewHolder.tvCell4.setText(timeIn.getInout());
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        TimeIn timeIn = TimeInContract.TimeIn.getTimeIn((Cursor)getItem(position));
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.header_view_all_logs, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        if(holder.tvHeader !=null) {
            holder.tvHeader.setText(AppUtils.getDateDisplay(timeIn.getDateTime(), AppConstants.DATE_FORMAT_LOCAL_TIME));
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        TimeIn timeIn = TimeInContract.TimeIn.getTimeIn((Cursor)getItem(position));
        Cursor cursor = (Cursor)getItem(position);
        String date = cursor.getString(cursor.getColumnIndex(TimeIn.DATE));
        return AppUtils.getDateStringToLong(date, "MM/dd/yy");
//        return 0;
    }

    static class ViewHolder {
        @InjectView(R.id.tvCell1)
        TextView tvCell1;
        @InjectView(R.id.tvCell2)
        TextView tvCell2;
        @InjectView(R.id.tvCell3)
        TextView tvCell3;
        @InjectView(R.id.tvCell4)
        TextView tvCell4;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class HeaderViewHolder {
        @InjectView(R.id.tvHeader)
        TextView tvHeader;


        public HeaderViewHolder(View view){
            ButterKnife.inject(this,view);
        }

    }
}
