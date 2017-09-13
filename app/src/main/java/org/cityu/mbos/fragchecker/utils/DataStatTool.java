package org.cityu.mbos.fragchecker.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;

import java.io.File;

/**
 * Created by Hubery on 2017/6/15.
 */

public class DataStatTool {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static DataStat getDataStat(){
        File data= Environment.getDataDirectory();
        StatFs data_stat=new StatFs(data.getPath()); //创建StatFs对象
        long data_blockSize=data_stat.getBlockSizeLong(); //获取block的size
        long data_totalBlocks=data_stat.getBlockCountLong();//获取block的个数
        long data_sizeInB = data_blockSize*data_totalBlocks;//计算总容量
        long data_availableBlocks = data_stat.getAvailableBlocksLong(); //获取可用block的个数
        long remaindata_sizeInB = data_availableBlocks*data_blockSize;
        double percentage = ((double)data_sizeInB - remaindata_sizeInB) / data_sizeInB;
        return new DataStat(data_blockSize,data_totalBlocks,data_sizeInB,data_availableBlocks,remaindata_sizeInB,percentage);
    }

    public static class DataStat{

        public DataStat(long blockSize, long totalBlocks, long totoalSize, long availableBlocks, long availableSize, double usage) {
            this.blockSize = blockSize;
            this.totalBlocks = totalBlocks;
            this.totoalSize = totoalSize;
            this.availableBlocks = availableBlocks;
            this.availableSize = availableSize;
            this.usage = usage;
        }

        public long blockSize;
        public long totalBlocks;
        public long totoalSize;
        public long availableBlocks;
        public long availableSize;
        public double usage;

        @Override
        public String toString() {
            return "DataStat{" +
                    "blockSize=" + blockSize +
                    ", totalBlocks=" + totalBlocks +
                    ", totoalSize=" + totoalSize +
                    ", availableBlocks=" + availableBlocks +
                    ", availableSize=" + availableSize +
                    ", usage=" + usage +
                    '}';
        }

    }

}
