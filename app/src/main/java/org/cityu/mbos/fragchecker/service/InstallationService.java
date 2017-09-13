package org.cityu.mbos.fragchecker.service;

import android.app.Service;

import org.cityu.mbos.fragchecker.conf.PublicParams;
import org.cityu.mbos.fragchecker.shell.ShellContent;
import org.cityu.mbos.fragchecker.utils.CommonTool;
import org.cityu.mbos.fragchecker.utils.ExceptionTool;
import org.cityu.mbos.fragchecker.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hubery on 2017/6/21.
 */

public abstract class InstallationService extends Service{



    protected void installFromAssets(InputStream inputStream, String path){
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
    }

}
