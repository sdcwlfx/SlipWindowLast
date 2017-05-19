package com.example.slipwindow.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.slipwindow.service.FlowManageService;

public class AotuStartReceiver extends BroadcastReceiver {
    public AotuStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Intent intent1=new Intent(context, FlowManageService.class);
        intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startService(intent1);
        Toast.makeText(context,"开机自启",Toast.LENGTH_SHORT).show();
    }
}
