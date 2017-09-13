package org.cityu.mbos.fragchecker.asyntask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;


import org.cityu.mbos.fragchecker.conf.PublicParams;
import org.cityu.mbos.fragchecker.datastruce.Ext4Info;
import org.cityu.mbos.fragchecker.datastruce.Ext4StatInfo;
import org.cityu.mbos.fragchecker.shell.ShellContent;
import org.cityu.mbos.fragchecker.utils.AppClassifier;
import org.cityu.mbos.fragchecker.utils.CommonTool;
import org.cityu.mbos.fragchecker.utils.DataStatTool;
import org.cityu.mbos.fragchecker.utils.ExceptionTool;
import org.cityu.mbos.fragchecker.utils.JsonTool;
import org.cityu.mbos.fragchecker.utils.Logger;
import org.cityu.mbos.fragchecker.utils.TypeClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Hubery on 2017/6/13.
 */

public class E4DefragTask extends AsyncTask<String,Integer,Integer>{

    public boolean isRunning = false;
    private Handler handle;

    private Context context;

    private static final String DEFAULT_PATH = "/sdcard/e4defrag.txt";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public E4DefragTask(Context context, Handler handle) {
        this.context = context;
        this.handle = handle;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected Integer doInBackground(String... params) {

        Logger.info("开始执行e4defrag命令");
        updateTextView("starts to execute e4defrag");
        String target = null;
        int state = 1;
        long start = System.currentTimeMillis();
        new ShellContent("/data/find /data | /data/xargs -t -n 1 /data/e4defrag -c -v | grep -v '^\\[[[:digit:]]'| grep -v '^\\\"' > " + DEFAULT_PATH + " 2>/dev/null").execute(true);
        long end = System.currentTimeMillis();
        Logger.info("e4defrag 执行完毕， 耗时 " + (end - start) + " ms");

        updateTextView("e4defrag is over, costs about " + (end - start) + " ms, and waiting for uploading to server");

        // 指定默认情况下才统计
        if(state == 1){

            String date = SDF.format(new Date());

            ArrayList<Ext4Info> ext4Infos = parseString2(DEFAULT_PATH, PublicParams.UNIQUEID, date, UUID.randomUUID().toString().replace("-",""));


            updateTextView("collecting data...");

            Ext4StatInfo statInfo = initStatInfo(ext4Infos);

            DataStatTool.DataStat dataStat = DataStatTool.getDataStat();

            statInfo.setUsage(dataStat.usage);
            statInfo.setTime(date);

            String json = JsonTool.classToJson(statInfo);
            Logger.info("class to json = " + json);
            writeResultToFile(json,PublicParams.RESULTLOCATION);

            updatePieChart(statInfo);


        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isRunning = true;
        updateTextView("正在启动中...");
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        isRunning = false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    private void updateTextView(String content){
        Message message = handle.obtainMessage();
        message.what = PublicParams.UPDATE_TEXTVIEW;
        message.obj = content;
        handle.sendMessage(message);
    }

    private void updatePieChart(Ext4StatInfo ext4StatInfo){
        Message message = handle.obtainMessage();
        message.what = PublicParams.UPDATE_PIECHART;
        message.obj = ext4StatInfo;
        handle.sendMessage(message);
    }

    public static ArrayList<Ext4Info> parseString2(String path, String userid, String timestamp, String optid) {

        BufferedReader reader = null;
        ArrayList<Ext4Info> list = new ArrayList<Ext4Info>();

        try {

            reader = new BufferedReader(new FileReader(path));

            String line = "";

            while ((line = reader.readLine()) != null){

                if(line.equals("<File>")){

                    String ext;
                    if((ext = reader.readLine()).startsWith("[ext")){

                        Ext4Info ext4Info = new Ext4Info();
                        ext4Info.setUserid(userid);
                        ext4Info.setTimestamp(timestamp);
                        ext4Info.setOptid(optid);

                        ArrayList<Long> blklenList = new ArrayList<Long>();

                        String len = ext.substring(ext.lastIndexOf(" ") + 1);
                        blklenList.add(Long.valueOf(len));

                        while ((ext = reader.readLine()).startsWith("[ext")){//循环获取ext
                            String len2 = ext.substring(ext.lastIndexOf(" ") + 1);
                            blklenList.add(Long.valueOf(len2));
                        }


                        String total = reader.readLine();
                        String fragement = total.substring(total.lastIndexOf("\t") + 1);
                        long frgmentCount = Long.valueOf(fragement.split("/")[0]) ;
                        long totalCount = Long.valueOf(fragement.split("/")[1]);
                        ext4Info.setFragmentCount(frgmentCount);
                        ext4Info.setBlockCount(totalCount);



                        if(frgmentCount != totalCount){ // 只有在存在碎片的时候，才记录下来
                            long[] blksinfo = new long[blklenList.size()];
                            for (int i = 0; i < blklenList.size(); i++) {
                                blksinfo[i] = blklenList.get(i);
                            }
                            ext4Info.setFragmentBlockLen(blksinfo);
                        }

                        reader.readLine();
                        reader.readLine();
                        reader.readLine();
                        String fileextent = reader.readLine();
                        String fileName = fileextent.substring(fileextent.indexOf("(") + 1, fileextent.indexOf(")"));
                        ext4Info.setFileName(fileName);
                        ext4Info.setSuffix(getSuffix(fileName));
                        list.add(ext4Info);

                    }

                }
            }

        }catch (Exception e){

            Logger.error(ExceptionTool.getExceptionStacksMessage(e));

        }finally {
            try {
                reader.close();
            } catch (IOException e) {

            }
        }

        return list;
    }

    private static String getSuffix(String file){
        if(file == null || file.indexOf(".") == -1){
            return "unknown";
        }
        String suffix = file.substring(file.lastIndexOf(".") + 1);
        if(suffix.contains("/")){
            return "unknown";
        }else{
            return suffix;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private Ext4StatInfo initStatInfo(ArrayList<Ext4Info> ext4infos){

        final TreeMap<Integer, Long> retMap = new TreeMap<>();
        final TreeMap<String, Float> density = new TreeMap<>();
        final TreeMap<Integer, Float> aDof = new TreeMap<>();

        Ext4StatInfo statInfo = new Ext4StatInfo();

        long l1 = 0,l2 = 0,l3 = 0,l4 = 0,l5 = 0,l6 = 0,l7 = 0, total = 0;

        double executableTotal = 0, mediaTotal = 0, sqliteTotal = 0, unknownTotal = 0;

        long executableCount = 0, mediaCount = 0, sqliteCount = 0, unknownCount = 0;

        for(Ext4Info exi : ext4infos) {

            if(exi.getBlockCount() != 0){

                int dof = (int) (exi.getFragmentCount() / exi.getBlockCount());

                TypeClassifier.FileType fileType = TypeClassifier.getFileType(exi.getSuffix());

                if(fileType == TypeClassifier.FileType.EXECUTABLE){

                    if(AppClassifier.classify(exi.getFileName())){
                        executableCount++;
                        executableTotal += dof;
                        //writeResultToFile(exi.getFileName() + "," + dof, PublicParams.TEMPLOCATION);
                    }

                }else if (fileType == TypeClassifier.FileType.MEDIA){

                    mediaCount++;
                    mediaTotal += dof;

                }else if(fileType == TypeClassifier.FileType.SQLITE){

                    sqliteCount++;
                    sqliteTotal += dof;

                }else {

                    unknownCount++;
                    unknownTotal += dof;

                }


                if(dof >= 10){
                    dof = 10;
                }

                if(retMap.containsKey(new Integer(dof))){
                    long v = retMap.get(dof);
                    v++;
                    retMap.put(dof,v);
                }else {
                    retMap.put(dof,1L);
                }

                if(retMap.containsKey(new Integer(3999))){// 魔术数 用3999代替总体
                    long v = retMap.get(3999);
                    v++;
                    retMap.put(new Integer(3999),v);
                }else {
                    retMap.put(new Integer(3999),1L);
                }

                statInfo.setDofMap(retMap);

                if(exi.getBlockCount() != exi.getFragmentCount() && exi.getFragmentBlockLen() != null){

                    long[] blklen = exi.getFragmentBlockLen();
                    total += blklen.length;

                    for (int i = 0; i < blklen.length; i++) {
                        long v = blklen[i];
                        if(v < 4){
                            l1++;
                        }else if (v >= 4 && v < 8){
                            l2++;
                        }else if (v >= 8 && v < 16){
                            l3++;
                        }else if (v >= 16 && v < 32){
                            l4++;
                        }else if (v >= 32 && v < 64){
                            l5++;
                        }else if(v >= 64 && v < 128){
                            l6++;
                        }else {
                            l7++;
                        }

                    }

                }


            }

        }

        aDof.put(TypeClassifier.FileType.EXECUTABLE.getId(), (float) (executableTotal / executableCount));
        aDof.put(TypeClassifier.FileType.MEDIA.getId(), (float) (mediaTotal / mediaCount));
        aDof.put(TypeClassifier.FileType.SQLITE.getId(), (float) (sqliteTotal / sqliteCount));
        aDof.put(TypeClassifier.FileType.UNKNOWN.getId(), (float) (unknownTotal / unknownCount));
        aDof.put(TypeClassifier.FileType.ALL.getId(), (float)((executableTotal + mediaTotal + sqliteTotal + unknownTotal) / (executableCount + mediaCount + sqliteCount + unknownCount)));
        statInfo.setaDofMap(aDof);


        Logger.info("density total = " + total +
                ", l1 = " + l1 + ", l2 = " + l2 + ", l3 = " + l3 + "" +
                ", l4 = " + l4 + ", l5 = " + l5 + ", l6 = " + l6 + ", l7 = " + l7);

        density.put("level1",(float)l1 / total);
        density.put("level2",(float)l2 / total);
        density.put("level3",(float)l3 / total);
        density.put("level4",(float)l4 / total);
        density.put("level5",(float)l5 / total);
        density.put("level6",(float)l6 / total);
        density.put("level7",(float)l7 / total);
        density.put("total", (float) total);

        statInfo.setDensityMap(density);

        return statInfo;

    }

    private static void writeResultToFile(String content, String file){
        if(content == null || content.equals("")){
            Logger.error("write result is null ....");
            return;
        }
        Logger.info("writing to file");
        BufferedWriter bwriter = null;
        try {

            bwriter = new BufferedWriter(new FileWriter(file, true));
            bwriter.write(content);
            bwriter.newLine();
            bwriter.flush();

        } catch (IOException e) {
            Logger.error(ExceptionTool.getExceptionStacksMessage(e));
        }finally {
            try {
                if(bwriter != null){
                    bwriter.close();
                }
            }catch (IOException e){
                Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            }

        }

    }


}
