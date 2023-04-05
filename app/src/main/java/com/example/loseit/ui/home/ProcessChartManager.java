package com.example.loseit.ui.home;

import android.content.Context;
import android.graphics.Color;

import com.example.loseit.R;
import com.example.loseit.model.DailyWeight;
import com.example.loseit.model.UserInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * class for setup and update the chart showing daily weight
 */
public class ProcessChartManager {
    public final LineChart lineChart;
    private final Context mContext;

    public ProcessChartManager(LineChart chart, Context context) {
        lineChart = chart;
        mContext = context;
        //description of chart
        Description description = new Description();
        description.setText("");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        //description.setPosition(200,40);
        lineChart.setDescription(description);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                System.out.println(e.getX());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //set up axis
        setUpAxisX();
        setupAxisY();
    }

    /**
     * generate data for chart from daily weight list
     *
     * @param userInfo UserInfo
     */
    public void showChartOfWeight(UserInfo userInfo) {
        ArrayList<DailyWeight> dailyWeightList = userInfo.getDailyWeights();
        //Date when weight recording began
        Date startDate = dailyWeightList.get(0).getCreateAt();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        //chart data
        ArrayList<Entry> dailyWeightData = new ArrayList<>();
        for (DailyWeight dailyWeight : dailyWeightList) {
            //Date of weight record
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dailyWeight.getCreateAt());
            //Calculate the number of days relative to the start date
            int day = calendar.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
            //chart data point
            Entry dataPoint = new Entry();
            dataPoint.setX(day);
            dataPoint.setY((float) dailyWeight.getWeight());
            dailyWeightData.add(dataPoint);
        }
        //Last time weight was recorded
        Date endDate = dailyWeightList.get(dailyWeightList.size() - 1).getCreateAt();
        //Calculate and set x-axis labels
        setXLabel(startDate, endDate);
        //show chart
        setChartData(dailyWeightData);
    }

    /**
     * calculate x label
     *
     * @param startDate Date
     * @param endDate   Date
     */
    private void setXLabel(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int dayNum = endCal.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
        int dayStep = 1;
        int minX = 0;
        int maxX = 0;
        if (dayNum <= 12) {
            maxX = 12;
        }
        if (12 < dayNum && dayNum <= 30) {
            maxX = 30;
            dayStep = 3;
        }
        if (30 < dayNum && dayNum <= 60) {
            maxX = 60;
            dayStep = 5;
        }
        if (60 < dayNum) {
            maxX = 365;
            dayStep = 30;
        }
        String[] moths = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May",
                "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        ArrayList<String> xLabel = new ArrayList<>();
        int i = minX;
        while (i < maxX) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, i);
            String mon = moths[calendar.get(Calendar.MONTH)];
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            xLabel.add(String.format(Locale.ENGLISH, "%s %d", mon, day));
            i += dayStep;
        }
        DateFormatter dateFormatter = new DateFormatter(xLabel, startDate, dayStep);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMinimum(minX);
        xAxis.setAxisMaximum(maxX);
        xAxis.setValueFormatter(dateFormatter);
        xAxis.setLabelCount(xLabel.size(), true);
    }

    /**
     * custom value formatter so that we can show month on axis x
     */
    public static class DateFormatter extends ValueFormatter {
        public final ArrayList<String> xLabel;
        public final Calendar startCal;
        public final int dayStep;
        private int index = 0;

        public DateFormatter(ArrayList<String> xLabel, Date startDate, int dayStep) {
            this.xLabel = xLabel;
            startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            this.dayStep = dayStep;
            //TODO
            System.out.println("count" + xLabel.size());
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, (int) value);
            if (index >= xLabel.size()) {
                index = 0;
            }
            //TODO
            System.out.println("****" + value);
            String label = xLabel.get(index);
            index += 1;
            return label;
        }
    }

    /**
     * set data for chart
     */
    private void setChartData(ArrayList<Entry> goalWeight) {
        LineDataSet dataSet = new LineDataSet(goalWeight,
                "Daily weight (kg)");
        //line color
        dataSet.setColor(mContext.getColor(R.color.primary_green));
        // point color
        dataSet.setCircleColor(mContext.getColor(R.color.primary_green));
        //Set Line weight
        dataSet.setLineWidth(2f);
        //Set the size of the focus circle center
        dataSet.setCircleRadius(6f);
        //Display style of highlighted lines after clicking
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setHighlightLineWidth(1f);
        //selected data point highlight
        dataSet.setHighlightEnabled(true);
        //highlight color
        dataSet.setHighLightColor(mContext.getColor(R.color.error_msg_bg));
        dataSet.setValueTextSize(9f);
        //Set to disable range background fill
        dataSet.setDrawFilled(false);
        //hide value of point
        dataSet.setDrawValues(false);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        // add the datasets
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    /**
     * setup axis X
     */
    private void setUpAxisX() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        //draw axis line
        xAxis.setDrawAxisLine(true);
        //Set the line corresponding to each point on the x-axis
        xAxis.setDrawGridLines(true);
        //Draw the corresponding value on the x-axis of the label
        xAxis.setDrawLabels(true);
        //Set the display position of the x-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //label rotate
        xAxis.setLabelRotationAngle(-60);
    }


    /**
     * setup axis Y
     */
    private void setupAxisY() {
        //get right Y axis
        YAxis rightAxis = lineChart.getAxisRight();
        //hide right Y axis
        rightAxis.setEnabled(false);
    }
}
