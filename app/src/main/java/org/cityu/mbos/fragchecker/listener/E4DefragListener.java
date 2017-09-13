package org.cityu.mbos.fragchecker.listener;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import org.cityu.mbos.fragchecker.asyntask.E4DefragTask;
import org.cityu.mbos.fragchecker.utils.Logger;


/**
 * Created by Hubery on 2017/6/12.
 */

public class E4DefragListener implements View.OnClickListener{

    private Context context;

    private Handler handler;

    public E4DefragListener(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        E4DefragTask task = new E4DefragTask(context, handler);
        if(!task.isRunning){
            task.execute("");
        }else {
            Logger.info("正在运行中...请等待运行结束");
        }
    }



}
