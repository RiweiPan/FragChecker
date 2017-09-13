package org.cityu.mbos.fragchecker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.cityu.mbos.fragchecker.shell.ShellContent;


/**
 * Created by Hubery on 2017/7/2.
 */

public class MkdirService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new ShellContent("mkdir /sdcard/e4sim/").execute(true);
    }

}
