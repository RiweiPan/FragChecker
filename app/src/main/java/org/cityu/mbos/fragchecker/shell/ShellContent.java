package org.cityu.mbos.fragchecker.shell;


import org.cityu.mbos.fragchecker.utils.ExceptionTool;
import org.cityu.mbos.fragchecker.utils.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @className ShellContent
 * @description
 * @author 潘日维
 * @version V1.0
 * @date 2017/6/13
 */
public class ShellContent {

    private String command;

    public ShellContent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean execute(boolean isRoot){
        Logger.info("执行 --> " + this.command);
        if(isRoot){
            return rootCommand(command);
        }else{
            return normalCommand(command);
        }
    }

    public String executeAndReturn(boolean isRoot){
        if(isRoot){
            return rootCommandReturn(command);
        }else{
            return normalCommandReturn(command);
        }
    }

    public static boolean chainedExecute(boolean isRoot, ShellContent ... commands){
        for(ShellContent cmd : commands){
            if(!cmd.execute(isRoot)){
                Logger.error("cmd error! , cmd = " + cmd.getCommand());
                return false;
            }
        }
        return true;
    }

    private static boolean normalCommand(String command){//用root的权限执行命令行
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return true;
        } catch (Exception e) {
            Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            return false;
        } finally {
            if(p != null){
                p.destroy();
            }
        }
    }

    private static String normalCommandReturn(String command){//用root的权限执行命令行
        String ret = "";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            ret = convertStreamToString(p.getInputStream());
        } catch (Exception e) {
            Logger.error(ExceptionTool.getExceptionStacksMessage(e));
        } finally {
            if(p != null){
                p.destroy();
            }
        }
        return ret;
    }

    private static boolean rootCommand(String command){//用root的权限执行命令行
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            new ReadInputSteamThread("sin",process.getInputStream()).start();
            new ReadInputSteamThread("ern",process.getErrorStream()).start();
            process.waitFor();
        } catch (Exception e) {
            Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            }
        }
        return true;
    }

    private static String rootCommandReturn(String command){//用root的权限执行命令行，然后获取返回结果
        Process process = null;
        DataOutputStream os = null;
        String ret = "";
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            InputStream in = process.getInputStream();
            ret = convertStreamToString(in);
        } catch (Exception e) {
            Logger.error(ExceptionTool.getExceptionStacksMessage(e));
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            }
        }
        return ret;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();



        String line = null;

        try {

            while ((line = reader.readLine()) != null) {

                sb.append(line + "/n");

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                is.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }



        return sb.toString();

    }

    static class ReadInputSteamThread extends Thread{

        String name;
        InputStream inputStream;

        public ReadInputSteamThread(String name, InputStream inputStream) {
            this.name = name;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            Logger.info("启动异步读线程");
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
            String line = "";
            try {

                while ((line = reader.readLine()) != null){
                    Logger.info("name : + " + name + " line = " + line);
                }

            }catch (Exception e){

                Logger.error(ExceptionTool.getExceptionStacksMessage(e));

            }finally {

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
