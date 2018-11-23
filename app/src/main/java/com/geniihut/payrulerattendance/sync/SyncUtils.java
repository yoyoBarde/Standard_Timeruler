/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniihut.payrulerattendance.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geniihut.payrulerattendance.AppApplication;
import com.geniihut.payrulerattendance.R;
import com.geniihut.payrulerattendance.sync.services.GenericAccountService;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    public static final String TAG = SyncUtils.class.getSimpleName();
    private static final long SYNC_FREQUENCY = 60 * 60;  // 1 hour (in seconds)
    private static final String TIME_IN_CONTENT_AUTHORITY = AppApplication.getInstance().getString(R.string.sync_time_in_content_authority);
    private static final String USER_CONTENT_AUTHORITY = AppApplication.getInstance().getString(R.string.sync_user_content_authority);
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static final String EXTRAS_CANCEL_SYNC = "extras_cancel_sync";

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Log.e(TAG, "addAccountExplicitly");
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, TIME_IN_CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, TIME_IN_CONTENT_AUTHORITY, true);
            newAccount = true;
        }
        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            Log.e(TAG, "!setupComplete");
            requestSyncTimeIn(null);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        } else {
            requestSyncTimeIn(null);
        }
    }

    public static void requestSync(String authority, Bundle extras) {
        Bundle bundle = new Bundle();
        if (extras != null) {
            bundle.putAll(extras);
        }
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.getAccount(),      // Sync account
                authority, // Content authority
                bundle);
    }

    public static void requestSyncTimeIn(Bundle bundle) {
        requestSync(TIME_IN_CONTENT_AUTHORITY, bundle);
    }

    public static void requestSyncUser(Bundle bundle) {
        requestSync(USER_CONTENT_AUTHORITY, bundle);
    }

    public static boolean isManual(Bundle bundle) {
        return bundle != null && bundle.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
    }

    public static boolean isExpedited(Bundle bundle) {
        return bundle != null && bundle.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
    }

    public static boolean isSyncActive(String authority) {
        return ContentResolver.isSyncActive(GenericAccountService.getAccount(), authority);
    }

    public static boolean isSyncPending(String authority) {
        return ContentResolver.isSyncPending(GenericAccountService.getAccount(), authority);
    }
}
