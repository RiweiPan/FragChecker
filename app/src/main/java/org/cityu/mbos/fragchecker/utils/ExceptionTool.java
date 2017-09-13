package org.cityu.mbos.fragchecker.utils;

/**
 * @className ExceptionTool
 * @description 异常信息封装，能够较为详细地显示异常信息
 * @author 潘日维
 * @version V1.0
 * @date 2016/12/28
 */
public class ExceptionTool {

    public static String getExceptionStacksMessage(Exception e){
        String msg = "";
        if(e != null) {
            msg += "Exception in thread \"" + Thread.currentThread().getName() + "\" " + e.toString() + "\n";
            final StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                msg += element.toString() + "\n";
            }
        }
        return msg;
    }

    public static String getExceptionStacksMessage(Throwable e){
        String msg = "";
        if(e != null) {
            msg += "Exception in thread \"" + Thread.currentThread().getName() + "\" " + e.toString() + "\n";
            final StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                msg += element.toString() + "\n";
            }
        }
        return msg;
    }


}
