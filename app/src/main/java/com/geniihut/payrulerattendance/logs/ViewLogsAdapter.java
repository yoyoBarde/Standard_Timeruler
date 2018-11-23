package com.geniihut.payrulerattendance.logs;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by icti on 2/27/15.
 */
public class ViewLogsAdapter extends CursorAdapter {
    public static final String TAG = ViewLogsAdapter.class.getSimpleName();

    public ViewLogsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public ViewLogsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
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

        View view = inflater.inflate(R.layout.table_item_two_columns, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        com.geniihut.payrulerattendance.model.TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(cursor);
//        viewHolder.tvInOut.setText(timeIn.get_id() + " " + timeIn.getInout());
        viewHolder.tvInOut.setText(timeIn.getInout());
        viewHolder.tvDateTime.setText(timeIn.getDateTime());
    }

    static class ViewHolder {
        @InjectView(R.id.tvCell1)
        TextView tvInOut;
        @InjectView(R.id.tvCell2)
        TextView tvDateTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
