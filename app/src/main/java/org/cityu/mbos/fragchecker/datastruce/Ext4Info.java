package org.cityu.mbos.fragchecker.datastruce;

import java.util.Arrays;

/**
 * Created by Hubery on 2017/6/13.
 */

public class Ext4Info {


    private String timestamp;
    private String userid;
    private String optid;
    private String fileName;
    private long fragmentCount;
    private long blockCount;
    private long[] fragmentBlockLen = null;
    private String suffix;


    public Ext4Info() {
    }

    public Ext4Info(String fileName, long fragmentCount, long blockCount, long[] fragmentBlockLen, String suffix) {
        this.fileName = fileName;
        this.fragmentCount = fragmentCount;
        this.blockCount = blockCount;
        this.fragmentBlockLen = fragmentBlockLen;
        this.suffix = suffix;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOptid() {
        return optid;
    }

    public void setOptid(String optid) {
        this.optid = optid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFragmentCount() {
        return fragmentCount;
    }

    public void setFragmentCount(long fragmentCount) {
        this.fragmentCount = fragmentCount;
    }

    public long getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(long blockCount) {
        this.blockCount = blockCount;
    }

    public long[] getFragmentBlockLen() {
        return fragmentBlockLen;
    }

    public void setFragmentBlockLen(long[] fragmentBlockLen) {
        this.fragmentBlockLen = fragmentBlockLen;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "Ext4Info{" +
                "timestamp='" + timestamp + '\'' +
                ", userid='" + userid + '\'' +
                ", optid='" + optid + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fragmentCount=" + fragmentCount +
                ", blockCount=" + blockCount +
                ", fragmentBlockLen=" + Arrays.toString(fragmentBlockLen) +
                ", suffix='" + suffix + '\'' +
                '}';
    }

}
