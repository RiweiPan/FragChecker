package org.cityu.mbos.fragchecker.service;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


import org.cityu.mbos.fragchecker.conf.PublicParams;
import org.cityu.mbos.fragchecker.shell.ShellContent;
import org.cityu.mbos.fragchecker.utils.Logger;

import java.io.IOException;

/**
 * Created by Hubery on 2017/6/21.
 */

public class InstallFindService extends InstallationService{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        String str = new ShellContent("ls " + PublicParams.DATAFIND).executeAndReturn(true);

        Logger.info("str = " + str);

        if(str.replace("/n","").equals(PublicParams.DATAFIND)){

            Logger.info("find 已经存在了，不需要重新安装");
            new ShellContent("chmod 775 " + PublicParams.DATAFIND).execute(true);
            String ret = new ShellContent("ls " + PublicParams.DATAFIND).executeAndReturn(true);
            Logger.info("new installation of find is " + ret);

        }else{

            Logger.info("find 不存在，需要重新安装");
            AssetManager assets = getAssets();
            try {
                installFromAssets(assets.open("find"), PublicParams.FINDLOCATION);
                new ShellContent("chmod 775 " + PublicParams.FINDLOCATION).execute(true);
                String ret = new ShellContent("ls " + PublicParams.FINDLOCATION).executeAndReturn(true);
                Logger.info("new installation of find is " + ret);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ShellContent.chainedExecute(true,
                    new ShellContent("mv " + PublicParams.FINDLOCATION + " " + "/data")
            );

        }


    }



}
