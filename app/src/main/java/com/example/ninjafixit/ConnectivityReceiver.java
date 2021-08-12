package com.example.ninjafixit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityRecieverListner connectivityRecieverListner;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        boolean isConnected=activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

        if(connectivityRecieverListner!=null){
            connectivityRecieverListner.onNetworkConnectionChanged(isConnected);
        }

    }

    public static boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) MyApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        return activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

    }
    public interface ConnectivityRecieverListner{
        void onNetworkConnectionChanged(Boolean isConnected);
    }
}
