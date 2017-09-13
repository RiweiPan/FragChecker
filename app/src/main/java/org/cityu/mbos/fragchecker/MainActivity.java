package org.cityu.mbos.fragchecker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import org.cityu.mbos.fragchecker.conf.PublicParams;
import org.cityu.mbos.fragchecker.datastruce.Ext4StatInfo;
import org.cityu.mbos.fragchecker.listener.E4DefragListener;
import org.cityu.mbos.fragchecker.service.InstallCreateService;
import org.cityu.mbos.fragchecker.service.InstallE4DefragService;
import org.cityu.mbos.fragchecker.service.InstallFindService;
import org.cityu.mbos.fragchecker.service.InstallXargsService;
import org.cityu.mbos.fragchecker.service.MkdirService;
import org.cityu.mbos.fragchecker.utils.ExceptionTool;
import org.cityu.mbos.fragchecker.utils.Logger;
import org.cityu.mbos.fragchecker.utils.TypeClassifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_CODE = 1001; //权限通知回调标识位

    private TextView textView;

    private Button e4defragButton;



    private PieChart pieChart;

    private BarChart barChart;

    private BarChart averageBarChart;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == PublicParams.UPDATE_TEXTVIEW){
                String content = (String) msg.obj;
                textView.setText(content);
            }
            if(msg.what == PublicParams.UPDATE_PIECHART){
                Ext4StatInfo ext4StatInfo = (Ext4StatInfo) msg.obj;
                ext4StatInfo.getDensityMap().remove("total");
                setPieChartData(pieChart, ext4StatInfo.getDensityMap());
                setBarChartData(barChart, ext4StatInfo.getDofMap());
                setBarChartData2(averageBarChart, ext4StatInfo.getaDofMap());
                textView.setText("usage = " + String.valueOf(ext4StatInfo.getUsage()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);

        textView = (TextView) findViewById(R.id.loginfo);
        e4defragButton = (Button) findViewById(R.id.button_e4defrage);
        pieChart = (PieChart) findViewById(R.id.pie_chart_1);
        barChart = (BarChart) findViewById(R.id.barchart);
        averageBarChart = (BarChart) findViewById(R.id.barchart2);

        initPieChart(pieChart);
        initBarChart(barChart);
        initBarChart(averageBarChart);

        startService(new Intent(this, MkdirService.class));
        startService(new Intent(this, InstallE4DefragService.class));
        startService(new Intent(this, InstallCreateService.class));
        startService(new Intent(this, InstallFindService.class));
        startService(new Intent(this, InstallXargsService.class));

        e4defragButton.setOnClickListener(new E4DefragListener(handler, this));

    }

    private void initPermissions(String ... permissions){

        ArrayList<String> permissionList = new ArrayList<>();
        //获取写入到sdcard的权限

        for (String p : permissions) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED){
                Logger.info("申请获取" + p + "的权限");
                permissionList.add(p);
            }
        }

        String[] permissionSet = permissionList.toArray(new String[permissionList.size()]);

        if(permissionSet.length > 0){
            ActivityCompat.requestPermissions(this, permissionSet, PERMISSIONS_CODE);
        }else {
            //初始化唯一id号
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            try {

                PublicParams.UNIQUEID = telephonyManager.getDeviceId(); // 用来作为用户的唯一id
                if(PublicParams.UNIQUEID == null || PublicParams.UNIQUEID.equals("")){ //如果获取不到，尝试获取收集号码
                    PublicParams.UNIQUEID = telephonyManager.getLine1Number();
                }

                PublicParams.UNIQUEID = android.os.Build.MODEL + "-" + android.os.Build.VERSION.RELEASE + "-" + PublicParams.UNIQUEID.substring(1,5);
                Logger.info("UNIQUEID = " + PublicParams.UNIQUEID);

            }catch (Exception e){
                Logger.error(ExceptionTool.getExceptionStacksMessage(e));
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isWritable = false;
        boolean isReadable = false;

        if(requestCode == PERMISSIONS_CODE){

            for (int i = 0; i < permissions.length; i++) {

                Logger.info("permission = " + permissions[i] + ", ret = " + grantResults[i]);

                if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    isWritable = true;
                }

                if(permissions[i].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    isReadable = true;
                }

                if(isWritable && isReadable){
                    //初始化唯一id号
                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    try {

                        PublicParams.UNIQUEID = telephonyManager.getDeviceId(); // 用来作为用户的唯一id
                        if(PublicParams.UNIQUEID == null || PublicParams.UNIQUEID.equals("")){ //如果获取不到，尝试获取收集号码
                            PublicParams.UNIQUEID = telephonyManager.getLine1Number();
                        }

                        Logger.info("UNIQUEID = " + PublicParams.UNIQUEID);

                    }catch (Exception e){
                        Logger.error(ExceptionTool.getExceptionStacksMessage(e));
                    }
                }

            }


        }


    }

    private void initPieChart(PieChart mPieChart){

        // 显示百分比
        mPieChart.setUsePercentValues(true);
        // 描述信息
        mPieChart.setDescription("");
        // 设置偏移量
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        // 设置滑动减速摩擦系数
        mPieChart.setDragDecelerationFrictionCoef(0.95f);

        mPieChart.setCenterText("");

         /*
            设置饼图中心是否是空心的
            true 中间是空心的，环形图
            false 中间是实心的 饼图
         */
        mPieChart.setDrawHoleEnabled(true);
        /*
            设置中间空心圆孔的颜色是否透明
            true 透明的
            false 非透明的
         */
        mPieChart.setHoleColorTransparent(true);
        // 设置环形图和中间空心圆之间的圆环的颜色
        mPieChart.setTransparentCircleColor(Color.WHITE);
        // 设置环形图和中间空心圆之间的圆环的透明度
        mPieChart.setTransparentCircleAlpha(110);

        // 设置圆孔半径
        mPieChart.setHoleRadius(58f);
        // 设置空心圆的半径
        mPieChart.setTransparentCircleRadius(61f);
        // 设置是否显示中间的文字
        mPieChart.setDrawCenterText(true);

        // 设置旋转角度   ？？
        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(false);

        // add a selection listener
        // mPieChart.setOnChartValueSelectedListener(this);

/*        TreeMap<String, Float> data = new TreeMap<>();
        data.put("data1", 0.5f);
        data.put("data2", 0.3f);
        data.put("data3", 0.1f);
        data.put("data4", 0.1f);
        setPieChartData(mPieChart,data);*/

        // 设置动画
        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        // 设置显示的比例
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

    }

    public void setPieChartData(PieChart mPieChart , TreeMap<String, Float> data) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        int i = 0;
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            // entry的输出结果如key0=value0等
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            float value = (float) entry.getValue();
            xVals.add(key);
            yVals1.add(new Entry(value, i++));
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "DoF");
        // 设置饼图区块之间的距离
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        // 添加颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        // dataSet.setSelectionShift(0f);

        PieData data1 = new PieData(xVals, dataSet);
        data1.setValueFormatter(new PercentFormatter());
        data1.setValueTextSize(10f);
        data1.setValueTextColor(Color.BLACK);
        mPieChart.setData(data1);

        // undo all highlights
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }

    private void initBarChart(BarChart mBarChart){

        mBarChart.setDrawValueAboveBar(true);
        //设置支持触控
        mBarChart.setTouchEnabled(true);
        //设置是否支持拖拽
        mBarChart.setDragEnabled(true);
        //设置能否缩放
        mBarChart.setScaleEnabled(true);
        //设置true支持两个指头向X、Y轴的缩放，如果为false，只能支持X或者Y轴的当方向缩放
        mBarChart.setPinchZoom(true);

        //设置阴影
        mBarChart.setDrawBarShadow(false);
        //设置所有的数值在图形的上面,而不是图形上
        mBarChart.setDrawValueAboveBar(true);
        //设置最大的能够在图表上显示数字的图数
        mBarChart.setMaxVisibleValueCount(60);
        //设置背景是否网格显示
        mBarChart.setDrawGridBackground(false);

        mBarChart.setDescription("");

        mBarChart.getXAxis().setDrawGridLines(false);
        //mBarChart.getXAxis().setLabelsToSkip(11);
        mBarChart.getXAxis().setDrawLabels(true);//是否显示X轴数值
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);



        mBarChart.animateXY(2000,3000);

    }

    private void setBarChartData(BarChart barChart, TreeMap<Integer, Long> dofMap) {

        //设置x轴方向上的标签数据
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        Set<Integer> keySet = dofMap.keySet();

        int index = 0;
        for (Integer key : keySet){

            xVals.add(index, "dof-" + key);
            yVals.add(new BarEntry(dofMap.get(key), index));
            index++;

        }

        //第一个参数是各个矩形在y轴方向上的值得集合，第二个参数为比例的文字说明
        BarDataSet set1 = new BarDataSet(yVals, "Different Dof");
        //设置矩形之间的间距，参数为百分数，可控制矩形的宽度
        set1.setBarSpacePercent(10f);

        //设置矩形的颜色
        // 添加颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        set1.setColors(colors);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        //设置柱形图的数据
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(Typeface.DEFAULT);

        barChart.setData(data);
        barChart.invalidate();

    }

    private void setBarChartData2(BarChart barChart, TreeMap<Integer, Float> dofMap) {

        //设置x轴方向上的标签数据
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        Set<Integer> keySet = dofMap.keySet();

        int index = 0;
        for (Integer key : keySet){

            xVals.add(index, String.valueOf(TypeClassifier.FileType.getNameById(key)));
            yVals.add(new BarEntry(dofMap.get(key), index));
            index++;

        }

        //第一个参数是各个矩形在y轴方向上的值得集合，第二个参数为比例的文字说明
        BarDataSet set1 = new BarDataSet(yVals, "Different Dof");
        //设置矩形之间的间距，参数为百分数，可控制矩形的宽度
        set1.setBarSpacePercent(10f);

        //设置矩形的颜色
        // 添加颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        set1.setColors(colors);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        //设置柱形图的数据
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(Typeface.DEFAULT);

        barChart.setData(data);
        barChart.invalidate();

    }


}

