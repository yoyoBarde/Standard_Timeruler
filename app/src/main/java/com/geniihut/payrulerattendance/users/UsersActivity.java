package com.geniihut.payrulerattendance.users;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
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
import android.widget.ListView;

import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.helpers.tablecolumns.TableTwoColumns;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.SelectionBuilder;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;
import com.geniihut.payrulerattendance.logs.ViewLogsActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UsersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @InjectView(R.id.app_bar)
    protected Toolbar mToolbar;
    @InjectView(R.id.listview)
    protected ListView mListView;

    protected UserAdapter mAdapter;

    private static final String TAG = UsersActivity.class.getSimpleName();
    String mSearchId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TableTwoColumns tableTwoColumns = new TableTwoColumns(this);
        tableTwoColumns.setHeaderTitles("ID No.", "NAME");
        mAdapter = new UserAdapter(this,null,0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mAdapter.getItem(position);
                final User user = UserContract.User.getUser(c);
                String msg = String.format("ID No.: %s\n" +
                                "Name : %s",
                        user.getIdno(),
                        user.getFullName());
                AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                builder.setTitle("Details")
                        .setMessage(msg)
                        .setPositiveButton("View Logs", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(UsersActivity.this, ViewLogsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(UserContract.User.TABLE, user);
                                        bundle.putBoolean("all", false);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
                        )
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserContract.User.delete(UsersActivity.this, null, user.getIdno());
                            }
                        })
                        .setNeutralButton("Close", null)
                        .create().show();
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_users, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String newText)
            {
                Log.e(TAG, "Text " + newText);
                if(newText.isEmpty()){
                    mSearchId = null;
                    getSupportLoaderManager().restartLoader(0, null, UsersActivity.this);
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query)
            {
                Log.e(TAG, "Text Submit " + query);
                mSearchId = query;
                if(!query.isEmpty()) mSearchId += "%";
                else mSearchId = null;
                getSupportLoaderManager().restartLoader(0, null, UsersActivity.this);
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
        }
        if (id == R.id.action_search) {
        }
        return super.onOptionsItemSelected(item);
    }
    /*******************
     * LoaderManager.LoaderCallbacks<Cursor>
     ******************/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG,"Cursor ");
        return getCursorLoader(this, null, mSearchId, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG," "+data.getCount());
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public CursorLoader getCursorLoader(Context context, Uri uri, String search, String sortOrder) {
        SelectionBuilder selectionBuilder = new SelectionBuilder();
        if (search != null) {
            selectionBuilder.where(User.IDNO + " LIKE ? OR "+ User.FNAME+ " LIKE ? OR " + User.LNAME+ " LIKE ?", search,search,search);
        }
        sortOrder = sortOrder == null ? UserContract.User.DEFAULT_SORT_ORDER : sortOrder;
        return new CursorLoader(context, uri == null ?  UserContract.User.CONTENT_URI : uri,  UserContract.User.PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
    }
    /*******************LoaderManager.LoaderCallbacks<Cursor> end******************/

}
