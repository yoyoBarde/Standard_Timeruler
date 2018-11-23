package com.geniihut.payrulerattendance.helpers.tablecolumns;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by macmini3 on 3/5/15.
 */
public class TableFourColumns {
    @InjectView(R.id.listview)
    protected ListView mListView;
    @InjectView(R.id.tvHeader1)
    protected TextView mTvHeader1;
    @InjectView(R.id.tvHeader2)
    protected TextView mTvHeader2;
    @InjectView(R.id.tvHeader3)
    protected TextView mTvHeader3;
    @InjectView(R.id.tvHeader4)
    protected TextView mTvHeader4;

    private static final int RES_LAYOUT = R.layout.table_four_columns;

    public TableFourColumns(Activity view) {
        ButterKnife.inject(this, view);
    }

    public TableFourColumns(View view) {
        ButterKnife.inject(this, view);
    }

    public ListView getListView() {
        return mListView;
    }

    public TextView getTvHeader1() {
        return mTvHeader1;
    }

    public TextView getTvHeader2() {
        return mTvHeader2;
    }

    public TextView getTvHeader3() {
        return mTvHeader3;
    }

    public TextView getTvHeader4() {
        return mTvHeader4;
    }

    public void setHeaderTitles(String title1, String title2, String title3, String title4) {
        mTvHeader1.setText(title1);
        mTvHeader2.setText(title2);
        mTvHeader3.setText(title3);
        mTvHeader4.setText(title4);
    }
}
