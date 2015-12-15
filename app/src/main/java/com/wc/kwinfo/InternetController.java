package com.wc.kwinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by wang on 2015/10/18.
 */
public class InternetController {
    private static final InternetController ourInstance = new InternetController();

    public static InternetController getInstance() {
        return ourInstance;
    }

    private InternetController() {
    }

    private final String TAG = InternetController.class.getSimpleName();

    private Context mContext = null;
    private boolean isConnected = false;

    public boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isNetworkConnected = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isNetworkConnected = true;
        } else {
            isNetworkConnected = false;
        }
        return isNetworkConnected;
    }

    public boolean isWifi(){
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiOn = networkInfo.isConnected();
        return isWifiOn;
    }

    public boolean checkNetwork(){
        boolean isWifiOnly =  mContext.getSharedPreferences("main_settings", Context.MODE_PRIVATE)
                .getBoolean("wifiOnly", false);
        if (isWifiOnly){
            return isWifi();
        } else {
            return isOnline();
        }
    }

    /**
     * setContext
     * @param context
     */
    public void setContext(Context context){
        this.mContext = context;
    }

    /**
     * Sometimes even if the network is connected, it cannot access to the Internet,
     * so we need ping
     */
    public void ping() {
        isConnected = false;
        if (checkConnection()) {
            pingHostBySocket("74.125.224.72");
            pingHostByPing("baidu.com");
        }
    }

    /**
     * checkConnection
     * @return if the device is connected to a network
     */
    public synchronized boolean checkConnection() {
        Log.d(TAG, "checkConnection started!");

        boolean connection = false;
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            // 获取代表联网状态的NetWorkInfo对象
            NetworkInfo[] networkInfo = connManager.getAllNetworkInfo();
            if (networkInfo != null) {
                for (NetworkInfo netInfo : networkInfo) {
                    if (netInfo.isConnected()) {
                        Log.d(TAG, "Connection is " + netInfo.getTypeName());
                        connection = true;
                        break;
                    }
                }
            }
        }
        return connection;
    }

    /**
     * start a thread to execute socket
     * @param thisIp ip to be executed on
     */
    public void pingHostBySocket(final String thisIp) {
        Log.d(TAG, "pingHostBySocket: " + thisIp);

        new Thread(new Runnable() {
            @Override
            public void run() {
                executeSocket(thisIp);
            }
        }).start();
    }

    /**
     * check if socket connection is valid
     * @param host
     */
    public void executeSocket(String host) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, 80), 2000);
            if (!isConnected) {
                isConnected = true;
                Log.d(TAG, host + " connect ok.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ping
     * @param thisIp
     */
    public void pingHostByPing(final String thisIp) {
        Log.d(TAG, "pingHostByPing: " + thisIp);

        new Thread(new Runnable() {
            @Override
            public void run() {
                executePing(thisIp);
            }
        }).start();
    }

    /**
     * execute ping
     * @param thisIP
     */
    public void executePing(String thisIP) {
        Process proc = null;
        BufferedReader buf = null;
        try {
            proc = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -f " + thisIP);
            buf = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            String line = "";

            for (int i = 0; i < 5; i++) {
                if (buf != null) {
                    line = buf.readLine();
                }
            }
            if (line != null && line.contains("rtt")) {
                Log.d(TAG, line);
                if (!isConnected) {
                    isConnected = true;
                    Log.d(TAG, thisIP + "ping ok.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (proc != null) {
                // waitFor before destroy，in case kill failed
                try {
                    proc.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                proc.destroy();
                proc = null;
            }
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buf = null;
            }
        }
    }

    public boolean getNetStatus(){
        return isConnected;
    }

}
