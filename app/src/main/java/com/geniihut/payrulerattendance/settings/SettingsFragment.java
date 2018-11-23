package com.geniihut.payrulerattendance.settings;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.apirequest.APIRequestManager;
import com.geniihut.payrulerattendance.apirequest.RequestResponseListener;
import com.geniihut.payrulerattendance.helpers.AppConstants;
import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.helpers.dialogs.LoginDialog;
import com.geniihut.payrulerattendance.logs.ViewAllLogsActivity;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;
import com.geniihut.payrulerattendance.logs.ViewLogsActivity;
import com.geniihut.payrulerattendance.sync.services.GenericAccountService;
import com.geniihut.payrulerattendance.users.UsersActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by macmini3 on 8/26/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @InjectView(R.id.app_bar)
    protected Toolbar mToolbar;
    @InjectView(android.R.id.list)
    protected ListView mListView;

    public static final String TAG = SettingsFragment.class.getSimpleName();


    private User mUser;
    //sync
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Syncing...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        ButterKnife.inject(this, view);
        view.setBackgroundColor(getResources().getColor(R.color.windowBackground));
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setTitle(getString(R.string.title_fragment_settings));
        mToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.primaryTextColor));
        mToolbar.setLogo(R.drawable.ic_logo);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mListView.setVisibility(ListView.INVISIBLE);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoginView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.pref_key_system_time))) {

        } else if (key.equalsIgnoreCase(getString(R.string.pref_key_lock_app))) {

        } else if (key.equalsIgnoreCase(getString(R.string.pref_key_stay_awake))) {

        }else if (key.equalsIgnoreCase(getString(R.string.pref_key_auto_sync_logs))) {
            Log.e(TAG, "isAutoSyncLogs:" + Settings.getInstance().isAutoSyncLogs());
            ContentResolver.setSyncAutomatically(GenericAccountService.getAccount(), TimeInContract.CONTENT_AUTHORITY, Settings.getInstance().isAutoSyncLogs());
        }
    }

    private void onBackPressed() {
        getActivity().getFragmentManager().popBackStack();
    }


    private void showLoginView() {
        final LoginDialog loginDialog = new LoginDialog(getActivity(), "Enter Credentials");
        final EditText etUserName = loginDialog.etUserName;
        final EditText etPassword = loginDialog.etPassword;
        loginDialog.setCanceledOnTouchOutside(false);
        final RequestResponseListener loginRequestListener = new RequestResponseListener() {
            ProgressDialog progressDialog;

            @Override
            public void requestStarted() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        AppApplication.getInstance().cancelPendingRequests(AppConstants.REQUEST_TAG_LOGIN);
                    }
                });
                progressDialog.setMessage("Getting credentials...");
                progressDialog.show();

            }

            @Override
            public void requestCompleted(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("1")) {
                        //TODO
                        User user = User.create(response);
                        onCredentialsValid(user);
                        loginDialog.dismiss();
                    } else {
                        AppUtils.toastShort(response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                Log.e(TAG, error.toString());
                AppUtils.toastVolleyError(error);
                progressDialog.dismiss();
            }
        };
        loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(SettingsFragment.this !=null)
                    onBackPressed();
            }
        });
        loginDialog.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (etUserName.getText().toString().isEmpty()) {
                    flag = false;
                    etUserName.setError("Required.");
                }
                if (etPassword.getText().toString().isEmpty()) {
                    flag = false;
                    etPassword.setError("Required.");
                }
                if (flag) {
                    String idno = etUserName.getText().toString().trim();
                    String password = etPassword.getText().toString();
                    User user = UserContract.User.getUser(getActivity(), idno);
                    if (user != null) {
                        if (user.getPin().equals(password)) {
                            //success
                            onCredentialsValid(user);
                            loginDialog.dismiss();
                        } else {
                            etPassword.setError("Incorrect Password");
                        }
                    } else {
                        if (AppUtils.isNetworkAvailable(getActivity())) {
                            APIRequestManager.postSyncConfirmationAndLogin(idno, password, loginRequestListener, AppConstants.REQUEST_TAG_LOGIN);
                        } else {
                            etUserName.setError("Invalid username and password");
                            etPassword.setError("Invalid username and password");
                        }
                    }
                }
            }
        });
        loginDialog.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
            }
        });

        //user
//        etUserName.setText("000001");
//        etPassword.setText("854870");
        //system user
//        etUserName.setText("1604192");
//        etPassword.setText("342352");

        loginDialog.show();
    }

    private void onCredentialsValid(User user) {
        mUser = user;
        mListView.setVisibility(ListView.VISIBLE);
        setSettingsView();
    }

    private void setSettingsView() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory admin = (PreferenceCategory) findPreference(getString(R.string.pref_key_admin));
        PreferenceCategory sync = (PreferenceCategory) findPreference(getString(R.string.pref_key_sync));
        PreferenceCategory user = (PreferenceCategory) findPreference(getString(R.string.pref_key_user));
        Preference viewLogs = user.findPreference(getString(R.string.pref_key_view_logs));

        viewLogs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mUser != null) {
                    Intent intent = new Intent(getActivity(), ViewLogsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(UserContract.User.TABLE, mUser);
                    bundle.putBoolean("all", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            }
        });

        if (mUser.getSystemUser().equalsIgnoreCase("t")) {//ADMIN
            Preference viewAllUsers = admin.findPreference(getString(R.string.pref_key_view_all_users));
            Preference viewAllLogs = admin.findPreference(getString(R.string.pref_key_view_all_logs));
            viewAllUsers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), UsersActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
            viewAllLogs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), ViewAllLogsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        } else {//USER
            preferenceScreen.removePreference(admin);
            preferenceScreen.removePreference(sync);
        }
    }
}
