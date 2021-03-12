package com.app.exptracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;


import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GraphActivity";
    Context context;
    DatabaseHelper db;
    List<ExcelDataModel> listcatData = new ArrayList<>();
    LineChartView lineChartView;
    String[] axisData;
    int[] yAxisData;
    List yAxisValues = new ArrayList();
    List axisValues = new ArrayList();

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        listcatData = db.GetTransactionData();

        if (listcatData.size() > 0) {
            yAxisData = new int[listcatData.size()];
            axisData = new String[listcatData.size()];
            for (int i = 0; i < listcatData.size(); i++) {
                yAxisData[i] = Integer.parseInt(listcatData.get(i).getAmount());
                axisData[i] = listcatData.get(i).getDate();
            }
        }

        //int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }


    @Override
    protected void InitResources() {
        lineChartView = findViewById(R.id.chart);
    }


    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_graph;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
    }

}
