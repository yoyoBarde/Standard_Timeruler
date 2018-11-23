package com.geniihut.payrulerattendance.helpers;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.geniihut.payrulerattendance.AppApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AppUtils {

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds) {
        Date date = new Date();
        date.setTime(timestampInMilliSeconds);
        String formattedDate = new SimpleDateFormat(AppConstants.DATE_FORMAT_API_INPUT).format(date);
        return formattedDate;

    }

    public static String getFormattedDateFromTimestamp(String date) {
        return getDateInputOutput(date, "yyyy-MM-dd", AppConstants.DATE_FORMAT_API_INPUT);
    }

    public static long getDateStringToLong(String date, String format) {
        long newDate = 0;
        if (date != null && !date.equalsIgnoreCase("") && !date.equalsIgnoreCase("null")) {
            try {
                DateFormat inputFormat = new SimpleDateFormat(
                        format);
                Date parsed = null;

                parsed = inputFormat.parse(date);

                newDate = parsed.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newDate;
    }

    public static long getDateStringToLong(String date) {
        return getDateStringToLong(date, "yyyy-MM-dd");
    }

    public static String getDateLongToString(long date, String format) {
        String newDate = "";
        DateFormat inputFormat = new SimpleDateFormat(
                format);
        Date parsed = new Date(date);
        newDate = inputFormat.format(parsed);

        return newDate;
    }

    public static String getDateLongToString(long date) {
        return getDateLongToString(date, "MM/dd/yyyy");
    }

    public static String getDateDisplay(String date, String input) {
        return getDateInputOutput(date, input, AppConstants.DATE_FORMAT_DISPLAY);
    }

    public static String getDateInputOutput(String date, String input, String output) {
        String newDate = "";
        if (date != null && !date.equalsIgnoreCase("") && !date.equalsIgnoreCase("null")) {
            try {
                DateFormat inputFormat = new SimpleDateFormat(
                        input);
//            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                DateFormat outputFormat = new SimpleDateFormat(
                        output);
                Date parsed = null;

                parsed = inputFormat.parse(date);

                newDate = outputFormat.format(parsed);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return newDate;
    }

    public static String getDatePayperiod(String time) {
        return getDateInputOutput(time, "yyyy-MM-dd", "MMM dd, yyyy");
    }

    public static String getTimeWithTimeZone(String time) {
        return getDateInputOutput(time, "yyyy-MM-dd HH:mm:ssZZ", "HH:mm:ss");
    }

    public static String getDate(String time) {
        return getDateInputOutput(time, "MM/dd/yyyy", "MMM dd, yyyy");
    }

    public static String getDateWithTimeZone(String time) {
        return getDateInputOutput(time, "yyyy-MM-dd HH:mm:ssZZ", "HH:mm:ss MMM dd, yyyy");
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void isKeyboardActive(Context context, View myEditText,
                                        boolean isActive) {
        if (context == null || myEditText == null) {
            return;
        }
        if (isActive) {

            ((InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        } else {
            ((InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        }
    }

    public static void toastVolleyError(VolleyError error) {
        toastLong(getVolleyErrorMessage(error));
    }

    public static void toastLong(String message) {
        Toast.makeText(AppApplication.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(String message) {
        Toast.makeText(AppApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static String getVolleyErrorMessage(VolleyError error) {
        String errorMessage = "Something went wrong!";
        if (error != null) {
            if (error instanceof NetworkError) {
                errorMessage = "Please check your internet connection.";
            } else if (error instanceof NoConnectionError) {
                errorMessage = "No internet connection.";
            } else if (error instanceof TimeoutError) {
                errorMessage = "Slow connection. Please try again.";
            } else if (error instanceof ServerError) {
                errorMessage = "Something went wrong on our end.";
            } else if (error instanceof AuthFailureError) {
                errorMessage = "Authentication failed. Please re-login.";
            }
        }
        return errorMessage;
    }

    public static void getVolleyErrorTextViewMessage(VolleyError error,
                                                     TextView tv) {
        String errorMessage = AppUtils.getVolleyErrorMessage(error);
        tv.setText(errorMessage);
    }

    public static String millisecondsDateUTCToLocal(long ms) {//needs another implementation
        Date date = new Date(ms);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT_LOCAL_TIME, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text = simpleDateFormat.format(date);
        try {
            Date myDate = simpleDateFormat.parse(text);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            return simpleDateFormat.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static AlertDialog showGpsDisabledDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please turn on GPS to find current location and time.";

        builder.setMessage(message)
                .setPositiveButton("SETTINGS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                context.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        return builder.show();
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        if(bitmap == null) return  null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getBitmapFromByteArray(byte[] bitmap) {
        if(bitmap == null) return  null;
        ByteArrayInputStream imageStream = new ByteArrayInputStream(bitmap);
        return BitmapFactory.decodeStream(imageStream);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    public static void logE(Object object,String message){
        Log.e(object.getClass().getSimpleName(),message);
    }
}
