package org.cityu.mbos.fragchecker.utils;



import org.cityu.mbos.fragchecker.datastruce.Ext4Info;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hubery on 2017/6/12.
 */

public class CommonTool {

    //将字符串 写入到文件当中
    public static String writeFile(byte[] buf, String fileName){
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        String path = null;
        try
        {
            path = fileName;
            file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e){
            return null;
        } finally{
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    // 将 一个数组 按照 设定的大小 分割为多个数组
    public static ArrayList<Ext4Info[]> arraySpliter(Ext4Info[] buffer, int spsize){

        ArrayList<Ext4Info[]> array = new ArrayList<Ext4Info[]>();

        if(spsize >= buffer.length){
            array.add(buffer);
            return array;
        }

        int len;

        if( buffer.length % spsize != 0 ){
            len = buffer.length / spsize + 1;
        }else {
            len = buffer.length / spsize;
        }

        for (int i = 0; i < len - 1; i++) {
            Ext4Info[] ioStatDaos = new Ext4Info[spsize];
            System.arraycopy(buffer, i * spsize, ioStatDaos, 0, spsize);
            array.add(ioStatDaos);
        }

        int leftover_len = buffer.length - spsize * (len - 1);
        Ext4Info[] ioStatDaos = new Ext4Info[leftover_len];
        System.arraycopy(buffer , (len - 1) * spsize , ioStatDaos , 0 , leftover_len);
        array.add(ioStatDaos);

        return array;

    }




}
