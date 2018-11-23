package com.geniihut.payrulerattendance.logs;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.events.SyncEvent;
import com.geniihut.payrulerattendance.helpers.tablecolumns.TableFourColumns;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.SelectionBuilder;
import com.geniihut.payrulerattendance.sync.SyncUtils;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ViewAllLogsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @InjectView(R.id.app_bar)
    protected Toolbar mToolbar;
    @InjectView(R.id.stickyListView)
    protected ExpandableStickyListHeadersListView mListView;

    protected ViewAllLogsAdapter mAdapter;
    private boolean mAllowRefresh;
    private ProgressDialog mProgressDialog;

    private String mSearchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_logs);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TableFourColumns tableTwoColumns = new TableFourColumns(this);
        tableTwoColumns.setHeaderTitles("ID No.", "NAME", "Time", "IN/OUT");
        setProgressDialog();
        mAdapter = new ViewAllLogsAdapter(this, null, 0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor c = (Cursor) mAdapter.getItem(position);
                final com.geniihut.payrulerattendance.model.TimeIn timeIn = TimeInContract.TimeIn.getTimeIn(c);
                final User user = UserContract.User.getUser(c);
                    ViewLogsDialog dialog = new ViewLogsDialog(ViewAllLogsActivity.this, timeIn, user, true);
                dialog.show();
            }
        });
        mListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mListView.isHeaderCollapsed(headerId)) {
                    mListView.expand(headerId);
                } else {
                    mListView.collapse(headerId);
                }
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
        getMenuInflater().inflate(R.menu.menu_view_all_logs, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                AppUtils.logE(ViewAllLogsActivity.this, "Text " + newText);
                if (newText.isEmpty()) {
                    mSearchId = null;
                    getSupportLoaderManager().restartLoader(0, null, ViewAllLogsActivity.this);
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                AppUtils.logE(ViewAllLogsActivity.this, "Text Submit " + query);
                mSearchId = query;
                if (!query.isEmpty()) mSearchId += "%";
                else mSearchId = null;
                getSupportLoaderManager().restartLoader(0, null, ViewAllLogsActivity.this);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
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
            return true;
        } else if (id == R.id.action_sync) {
            SyncUtils.requestSyncTimeIn(null);
        }

        return super.onOptionsItemSelected(item);
    }

    /*******************
     * LoaderManager.LoaderCallbacks<Cursor>
     ******************/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mAllowRefresh = true;
        return getCursorLoader(this, null, mSearchId, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAllowRefresh) {
            AppUtils.logE(this, "Data count " + data.getCount());
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

    public CursorLoader getCursorLoader(Context context, Uri uri, String search, String sortOrder) {
        SelectionBuilder selectionBuilder = new SelectionBuilder();
        if (search != null) {
            selectionBuilder.where(TimeInContract.TimeIn.TABLE + "." + User.IDNO + " LIKE ? OR " + User.FNAME + " LIKE ? OR " + User.LNAME + " LIKE ?", search, search, search);
        }
        AppUtils.logE(this, selectionBuilder.getSelection());
        sortOrder = sortOrder == null ? TimeInContract.TimeInUser.DEFAULT_SORT_ORDER : sortOrder;
        return new CursorLoader(context, uri == null ? TimeInContract.TimeInUser.CONTENT_URI : uri, TimeInContract.TimeInUser.PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
    }


    private void refreshListView() {
        mAllowRefresh = true;
        getContentResolver().notifyChange(TimeInContract.TimeIn.CONTENT_URI, null, true);
    }

    public void onEvent(SyncEvent event) {
        if (event.isSyncInProgress()) {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            AppUtils.logE(this, "onEvent SyncEvent = " + event.toString());
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

    private void renderData() {

    }
}
