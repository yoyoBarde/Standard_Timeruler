package com.geniihut.payrulerattendance.ntp;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by macmini4 on 9/7/15.
 */
public class NTPClient {
    public interface NTPClientCallBack {
        void onReceive(long ms);
    }

    public static final String TAG = NTPClient.class.getSimpleName();
    private static final String DEFAULT_NTP_SERVER = "pool.ntp.org";
    private static final int SNTP_PORT = 123;
    private SNTPClient sntpClient;
    private NTPClientCallBack mCallBack;

    public void getRealTime(NTPClientCallBack callBack) {
        mCallBack = callBack;
        sntpClient = new SNTPClient();
//        sntpClient.execute(DEFAULT_NTP_SERVER);
        sntpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,DEFAULT_NTP_SERVER);
    }

    public void cancel() {
        if (sntpClient != null) {
            sntpClient.cancel(false);
            sntpClient = null;
        }
    }

    private double retrieveSNTPTime(String... params) throws SocketException, UnknownHostException, IOException {
        String serverName = params[0];
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(DEFAULT_NTP_SERVER);
        byte[] buffer = new NtpMessage().toByteArray();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SNTP_PORT);

        NtpMessage.encodeTimestamp(packet.getData(), 40,
                (System.currentTimeMillis() / 1000.0) + 2208988800.0);

        socket.send(packet);

        packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        // Process response
        NtpMessage message = new NtpMessage(packet.getData());

        // Display response
//        AppLog.logString("NTP server: " + serverName);
//        AppLog.logString(message.toString());

        socket.close();

        return message.transmitTimestamp;
    }

    private class SNTPClient extends AsyncTask<String, Void, Integer> {
        //        private ProgressDialog progress = null;
        private double ntpTime = 0;

        @Override
        protected Integer doInBackground(String... params) {
            try {
                ntpTime = retrieveSNTPTime(params);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
//            TextView textSystemTime = (TextView)findViewById(R.id.system_time);
//            TextView textNtpTime = (TextView)findViewById(R.id.ntp_time);
            double utc = ntpTime - (2208988800.0);

            // milliseconds
            long ms = (long) (utc * 1000.0);
            Log.e(TAG, "NTP Time" + ntpTime + "\nUTC " + utc + "\nMS " + ms);
            if (ntpTime == 0) {
                Log.e(TAG, "Zero NTP");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sntpClient = null;
                        getRealTime(mCallBack);
                    }
                }, 2000);
                return;
            }

            if (mCallBack != null) {
                mCallBack.onReceive(ms);
            }

            // date/time
//            String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date(ms));

            // fraction
//            double fraction = ntpTime - ((long) ntpTime);
//            String fractionSting = new DecimalFormat(".000000").format(fraction);

//            textSystemTime.setText("System Time:\n" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.S").format(new Date()));
//            textNtpTime.setText("NTP Time:\n" + date + fractionSting);

//            progress.dismiss();
//            progress = null;

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
//            progress = new ProgressDialog(MainActivity.this);
//
//            progress.setMessage("Retrieving timestamp...");
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//
//            progress.show();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
