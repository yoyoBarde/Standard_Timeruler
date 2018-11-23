package com.geniihut.payrulerattendance.users;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by icti on 2/27/15.
 */
public class UserAdapter extends CursorAdapter {
    Activity context;
    public UserAdapter(Activity context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    public UserAdapter(Activity context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
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

        View view = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        com.geniihut.payrulerattendance.model.User user = UserContract.User.getUser(cursor);//TimeInContract.TimeIn.getTimeIn(cursor);
        viewHolder.tvCell1.setText(user.getIdno());
        viewHolder.tvCell2.setText(user.getFullName());
    }

    static class ViewHolder {
        @InjectView(R.id.tvCell1)
        TextView tvCell1;
        @InjectView(R.id.tvCell2)
        TextView tvCell2;
        @InjectView(R.id.btndelete)
        Button btnDelete;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /*
    *   Activity context;
    List<User> data;

    public UserAdapter(Activity context, List<User> data) {
        this.context = context;
        this.data = data;
//        this.inflater = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (position % 2 == 0) {
            view.setBackgroundResource(R.color.table_cell_one);
        } else {
            view.setBackgroundResource(R.color.table_cell_two);
        }

        final User item = getItem(position);
        if (holder != null && item != null) {
            if (holder.tvCell1 != null) {
                holder.tvCell1.setText(item.getIdno());
            }
            if (holder.tvCell2 != null) {
                holder.tvCell2.setText(item.getFullName());
            }
            if(holder.btnDelete!=null){
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(item); // Send to Home Activity Delete User
                    }
                });
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    * */
}
