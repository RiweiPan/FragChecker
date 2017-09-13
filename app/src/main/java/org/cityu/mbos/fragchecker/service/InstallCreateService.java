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
 * Created by Hubery on 2017/6/15.
 */

public class InstallCreateService extends InstallationService{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCreate();
    }

   /* private void installCreate(InputStream inputStream, String path){
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

    private void initCreate(){

        String str = new ShellContent("ls " + PublicParams.DATACREATE).executeAndReturn(true);

        Logger.info("str = " + str);

        if(str.replace("/n","").equals(PublicParams.DATACREATE)){

            Logger.info("create 已经存在了，不需要重新安装");
            new ShellContent("chmod 775 " + PublicParams.DATACREATE).execute(true);
            String ret = new ShellContent("ls " + PublicParams.DATACREATE).executeAndReturn(true);
            Logger.info("new installation of create is " + ret);

        }else{

            Logger.info("create不存在，需要重新安装");
            AssetManager assets = getAssets();
            try {
                installFromAssets(assets.open("create"), PublicParams.CREATELOCATION);
                new ShellContent("chmod 775 " + PublicParams.CREATELOCATION).execute(true);
                String ret = new ShellContent("ls " + PublicParams.CREATELOCATION).executeAndReturn(true);
                Logger.info("new installation of create is " + ret);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ShellContent.chainedExecute(true,
                    new ShellContent("mkdir /data/test"),
                    new ShellContent("mv " + PublicParams.CREATELOCATION + " " + "/data/test"),
                    new ShellContent("mount -o rw,remount /system"),
                    new ShellContent("ln -s /proc/mounts /etc/mtab")
            );

        }



    }


}
