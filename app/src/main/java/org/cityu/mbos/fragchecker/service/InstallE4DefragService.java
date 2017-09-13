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
 * Created by Hubery on 2017/6/12.
 */

public class InstallE4DefragService extends InstallationService{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initE4Defrag();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initE4Defrag(){

        String str = new ShellContent("ls " + PublicParams.DATAE4DEFRAG).executeAndReturn(true);

        Logger.info("str = " + str);

        if(str.replace("/n","").equals(PublicParams.DATAE4DEFRAG)){

            Logger.info("E4Defrag 已经存在了，不需要重新安装");
            new ShellContent("chmod 775 " + PublicParams.DATAE4DEFRAG).execute(true);
            String ret = new ShellContent("ls " + PublicParams.DATAE4DEFRAG).executeAndReturn(true);
            Logger.info("new installation of e4defrag is " + ret);

        }else{

            Logger.info("E4Defrag不存在了，需要重新安装");
            AssetManager assets = getAssets();
            try {
                installFromAssets(assets.open("e4defrag"), PublicParams.E4DEFRAGLOCATION);
                new ShellContent("chmod 775 " + PublicParams.E4DEFRAGLOCATION).execute(true);
                String ret = new ShellContent("ls " + PublicParams.E4DEFRAGLOCATION).executeAndReturn(true);
                Logger.info("new installation of e4defrag is " + ret);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ShellContent.chainedExecute(true,
                    new ShellContent("mv " + PublicParams.E4DEFRAGLOCATION + " " + "/data"),
                    new ShellContent("mount -o rw,remount /system"),
                    new ShellContent("ln -s /proc/mounts /etc/mtab")
            );

        }


    }

   /* private void installE4Defrag(InputStream inputStream, String path){
        ByteArrayOutputStream bos = null;
        byte[] data = null;
        try {

            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024]; //buff用于存放循环读取的临时数据
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
                bos.write(buff, 0, rc);
            }
            data = bos.toByteArray(); //in_b为转换之后的结果

            CommonTool.writeFile(data, path);


        }catch (IOException e){

            Logger.error(ExceptionTool.getExceptionStacksMessage(e));

        }finally {
            try {
                inputStream.close();
                bos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }*/



}
