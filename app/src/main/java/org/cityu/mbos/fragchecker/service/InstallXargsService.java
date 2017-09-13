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
 * Created by Hubery on 2017/6/22.
 */

public class InstallXargsService extends InstallationService{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        String str = new ShellContent("ls " + PublicParams.DATAXARGS).executeAndReturn(true);

        Logger.info("str = " + str);

        if(str.replace("/n","").equals(PublicParams.DATAXARGS)){

            Logger.info("XARGS 已经存在了，不需要重新安装");
            new ShellContent("chmod 775 " + PublicParams.DATAXARGS).execute(true);
            String ret = new ShellContent("ls " + PublicParams.DATAXARGS).executeAndReturn(true);
            Logger.info("new installation of xargs is " + ret);

        }else{

            Logger.info("XARGS 不存在，需要重新安装");
            AssetManager assets = getAssets();
            try {
                installFromAssets(assets.open("xargs"), PublicParams.XARGSLOCATION);
                new ShellContent("chmod 775 " + PublicParams.XARGSLOCATION).execute(true);
                String ret = new ShellContent("ls " + PublicParams.DATAXARGS).executeAndReturn(true);
                Logger.info("new installation of XARGS is " + ret);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ShellContent.chainedExecute(true,
                    new ShellContent("mv " + PublicParams.XARGSLOCATION + " " + "/data")
            );

        }

    }

}
