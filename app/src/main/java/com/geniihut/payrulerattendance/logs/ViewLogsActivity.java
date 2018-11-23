package com.geniihut.payrulerattendance.logs;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.geniihut.payrulerattendance.BaseGPSActivity;
import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.events.SyncEvent;
import com.geniihut.payrulerattendance.helpers.tablecolumns.TableTwoColumns;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.DBHelper;
import com.geniihut.payrulerattendance.sync.SyncUtils;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;
import com.geniihut.payrulerattendance.sync.services.GenericAccountService;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by macmini3 on 8/12/15.
 */
public class ViewLogsActivity extends BaseGPSActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @InjectView(R.id.tvID)
    protected TextView mTvID;
    @InjectView(R.id.tvName)
    protected TextView mTvName;

    private static final String TAG = ViewLogsActivity.class.getSimpleName();

    protected ViewLogsAdapter mAdapter;
    protected List<com.geniihut.payrulerattendance.model.TimeIn> mListItems;
    protected User mUser;
    protected String mSortOrder;
    protected String mFilter;
    private boolean mAllowRefresh;
    private ProgressDialog mProgressDialog;

    @Override
    public int getLayoutResources() {
        return R.layout.activity_time_in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mListItems = new ArrayList<com.geniihut.payrulerattendance.model.TimeIn>();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if(mToolbar != null) {
            getSupportActionBar().setTitle("Logs");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TableTwoColumns tableTwoColumns = new TableTwoColumns(activityView);
        tableTwoColumns.setHeaderTitles("IN/OUT", "Date and Time");

        setProgressDialog();

        if (getIntent() != null && getIntent().getExtras() != null) {
            mUser = (User) getIntent().getExtras().getSerializable(UserContract.User.TABLE);
        }
        if (mUser == null) finish();

        mTvID.setText(mUser.getIdno());
        mTvName.setText(mUser.getFullName());

        mSortOrder = DBHelper.ORDER_DESC;
        mFilter = "ALL";
        mAdapter = new ViewLogsAdapter(this, null, 0);
        if(mAdapter != null)
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor c = (Cursor) mAdapter.getItem(position);
                final com.geniihut.payrulerattendance.model.TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(c);
                ViewLogsDialog dialog = new ViewLogsDialog(ViewLogsActivity.this, timeIn, mUser, false);
                dialog.show();
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);

        if (ContentResolver.isSyncActive(GenericAccountService.getAccount(), TimeInContract.CONTENT_AUTHORITY)) {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Watch for sync state changes
        boolean isActive = SyncUtils.isSyncActive(TimeInContract.CONTENT_AUTHORITY);
        if (isActive) {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //to prevent window leak
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //to prevent window leak
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_in, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.getItem(1).getSubMenu().size(); i++) {
            MenuItem item = menu.getItem(1).getSubMenu().getItem(i);
            if (mFilter.equalsIgnoreCase(item.getTitle().toString())) {
                item.setCheckable(true);
                item.setChecked(true);
            } else {
                item.setCheckable(false);
                item.setChecked(false);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_sync) {
            Bundle bundle = new Bundle();
            bundle.putString(TimeInContract.TimeIn.EXTRAS_IDNO, mUser.getIdno());
            SyncUtils.requestSyncTimeIn(bundle);
        } else if (id == R.id.filter_all) {
            mFilter = "ALL";
            refreshListView();
            invalidateOptionsMenu();
        } else if (id == R.id.filter_in) {
            mFilter = "IN";
            refreshListView();
            invalidateOptionsMenu();
        } else if (id == R.id.filter_out) {
            mFilter = "OUT";
            refreshListView();
            invalidateOptionsMenu();
        } else if (id == R.id.action_sort) {
            if (mSortOrder.equals(DBHelper.ORDER_DESC)) {
                mSortOrder = DBHelper.ORDER_ASC;
                item.setTitle("Sort Descending");
            } else {
                mSortOrder = DBHelper.ORDER_DESC;
                item.setTitle("Sort Ascending");
            }
            refreshListView();
        }


        return super.onOptionsItemSelected(item);
    }

    /*******************
     * LoaderManager.LoaderCallbacks<Cursor>
     ******************/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "onCreateLoader");
        mAllowRefresh = true;
        return TimeInContract.TimeIn.getCursorLoader(this, null, mUser.getIdno(), mFilter, getSortOrder());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAllowRefresh) {
            Log.e(TAG, "onLoadFinished");
            mAllowRefresh = false;
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    /*******************
     * LoaderManager.LoaderCallbacks<Cursor> end
     ******************/

    @OnClick(R.id.btnIn)
    public synchronized void OnClickBtnIn() {
//        TimeInOutDialog dialog = new TimeInOutDialog();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(TimeInOutDialog.USER, mUser);
//        bundle.putString(TimeInOutDialog.ACTION, "IN");
//        dialog.setArguments(bundle);
//        dialog.show(getSupportFragmentManager(), "TimeInOutDialog");
    }

    @OnClick(R.id.btnOut)
    public void OnClickBtnOut() {
//        TimeInOutDialog dialog = new TimeInOutDialog();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(TimeInOutDialog.USER, mUser);
//        bundle.putString(TimeInOutDialog.ACTION, "OUT");
//        dialog.setArguments(bundle);
//        dialog.show(getSupportFragmentManager(), "TimeInOutDialog");
    }

    public String getRequestTag() {
        return "TimeInRequest";
    }

    @Override
    public String getActivityTitle() {
        return " Time Sheet";
    }

    private String getSortOrder() {
        return TimeInContract.TimeIn._ID + " " + mSortOrder;
    }

    private void setProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Syncing...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Stop", (DialogInterface.OnClickListener) null);
        mProgressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Return to Settings", (DialogInterface.OnClickListener) null);
        mProgressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnStop = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
                btnStop.setEnabled(true);
                btnStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setEnabled(false);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SyncUtils.EXTRAS_CANCEL_SYNC, true);
                        SyncUtils.requestSyncTimeIn(bundle);
                    }
                });

                Button btnReturn = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
                btnReturn.setEnabled(true);
                btnReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        });
    }

    private void refreshListView() {
        mAllowRefresh = true;
//        getContentResolver().notifyChange(TimeInContract.TimeIn.CONTENT_URI, null, true);
        getSupportLoaderManager().restartLoader(0, null, this);
//        if (mAdapter != null) {
//            mAdapter.notifyDataSetChanged();
//        }
    }

    public void onEvent(SyncEvent event) {
        if (event.isSyncInProgress()) {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
//            Log.e(TAG, "onEvent SyncEvent = " + event.toString());
            mProgressDialog.setMax(event.getMax());
            mProgressDialog.setProgress(event.getProgress());
        } else {
            if (mProgressDialog.isShowing()) {
                AppUtils.logE(this, "isSyncInProgress:false");
                refreshListView();
                mProgressDialog.dismiss();
            }
        }
    }

    public void onEvent(final com.geniihut.payrulerattendance.model.TimeIn event) {
//                AppUtils.toastShort("event sssss");
        int index = mSortOrder.equalsIgnoreCase(DBHelper.ORDER_ASC) ? mListItems.size() : 0;
//            event.set_id(mTimeInDao.insert(event));//TODO
        if (event.getIdno().equalsIgnoreCase(mFilter) || mFilter.equalsIgnoreCase("ALL"))
            mListItems.add(index, event);
    }
}
