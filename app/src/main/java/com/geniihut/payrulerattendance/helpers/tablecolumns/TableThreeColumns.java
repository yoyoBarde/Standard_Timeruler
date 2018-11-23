package com.geniihut.payrulerattendance.helpers.tablecolumns;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by macmini3 on 3/5/15.
 */
public class TableThreeColumns {
    @InjectView(R.id.listview)
    protected ListView mListView;
    @InjectView(R.id.tvHeader1)
    protected TextView mTvHeader1;
    @InjectView(R.id.tvHeader2)
    protected TextView mTvHeader2;
    @InjectView(R.id.tvHeader3)
    protected TextView mTvHeader3;

    private static final int RES_LAYOUT = R.layout.table_three_columns;

    public TableThreeColumns(View view){
        ButterKnife.inject(this, view);
    }

    public ListView getListView() {
        return mListView;
    }

    public TextView getTvHeader1() {
        return mTvHeader1;
    }

    public TextView getTvHeader3() {
        return mTvHeader3;
    }

    public TextView getTvHeader2() {
        return mTvHeader2;
    }

    public void setHeaderTitles(String title1, String title2, String title3) {
        mTvHeader1.setText(title1);
        mTvHeader2.setText(title2);
        mTvHeader3.setText(title3);
    }
}
