package com.itskasra.devicesensormanager;

import android.hardware.Sensor;

import java.util.ArrayList;

/**
 * Created by kbigdeli on 7/16/2015.
 */
public class SensorDataHolder {

    private Object lockData = new Object();

    private boolean isSelected = false;
    private Sensor mSensor;

    private boolean isBidirectional = false;
    private boolean[] isActiveAxis = {true, true, true};
    private int refreshRate = 3;
    private boolean isSmoothened = true;

    private ArrayList<Double> xValues = new ArrayList<>();
    private ArrayList<Double> yValues = new ArrayList<>();
    private ArrayList<Double> zValues = new ArrayList<>();
    private ArrayList<Long> timeStamps = new ArrayList<>();

    private double avX = 0;
    private double avY = 0;
    private double avZ = 0;
    private double maxValue = 0.00001;


    public Sensor getHardwareSensor() {
        return mSensor;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsBidirectional() {
        return isBidirectional;
    }

    public boolean getIsActiveAxis(int ax) {
        return isActiveAxis[ax];
    }

    public void setIsActiveAxis(int ax, boolean b) {
        isActiveAxis[ax] = b;
    }

    public boolean getIsSmoothened() {
        return isSmoothened;
    }

    public void setIsSmoothened(boolean isSmoothened) {
        this.isSmoothened = isSmoothened;
    }


    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }


    public double getMaxValue() {
        return maxValue;
    }


    public SensorDataHolder(Sensor sensor) {
        this.mSensor = sensor;
    }

    void onDataReceived(double x, double y, double z, long t) {

        synchronized (lockData) {

            if (!isBidirectional) {
                if ((x < 0) || (y < 0) || (z < 0))
                    isBidirectional = true;
            }

            if (maxValue < Math.abs(x))
                maxValue = Math.abs(x);

            if (maxValue < Math.abs(y))
                maxValue = Math.abs(y);

            if (maxValue < Math.abs(z))
                maxValue = Math.abs(z);

            if (isSmoothened) {
                avX = avX * 0.8 + x * 0.2;
                avY = avY * 0.8 + y * 0.2;
                avZ = avZ * 0.8 + z * 0.2;
            } else {
                avX = x;
                avY = y;
                avZ = z;
            }


            this.xValues.add(x);
            this.yValues.add(y);
            this.zValues.add(z);
            this.timeStamps.add(t);

        }
    }

    void clearData() {

        synchronized (lockData) {
            xValues.clear();
            yValues.clear();
            zValues.clear();
            timeStamps.clear();
            maxValue = 0.0001;
            avX = 0;
            avY = 0;
            avZ = 0;
        }
    }

    public long getEarliestTimeStamp() {
        if (timeStamps.size() < 1)
            return 0;
        else
            return timeStamps.get(0);
    }

    public String generateReport(long earliestTimeStamped) {

        StringBuilder ret = new StringBuilder();

        String header = "Name: " + getName() + "\n" + "Type: " + convertSensorTypeToString(getType()) +
                "\n" + "Made by: " + getVendor();
        header += "\n";
        header += "t(nanosec) , x_value , y_value, z_value";
        header += "\n";
        ret.append(header);

        if (
                (xValues.size() != yValues.size())
                        || (xValues.size() != zValues.size())
                        || (xValues.size() != timeStamps.size())
                ) {
            return "Data is inconsistent. Please contact the developer.";
        }

        for (int idx = 0; idx < xValues.size(); idx++) {
            ret.append((timeStamps.get(idx) - earliestTimeStamped));
            ret.append(',');
            ret.append(xValues.get(idx));
            ret.append(',');
            ret.append(yValues.get(idx));
            ret.append(',');
            ret.append(zValues.get(idx));
            ret.append('\n');
        }

        return ret.toString();
    }

    double getLastX() {

        if (!isActiveAxis[0])
            return 0;

        synchronized (lockData) {
            return avX;
        }
    }

    double getLastY() {

        if (!isActiveAxis[1])
            return 0;

        synchronized (lockData) {
            return avY;
        }
    }

    double getLastZ() {

        if (!isActiveAxis[2])
            return 0;

        synchronized (lockData) {
            return avZ;
        }
    }

    static String convertSensorTypeToString(int idx) {
        String sensorTypeName;
        switch (idx) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorTypeName = "Accelerometer";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorTypeName = "Ambient Temperature";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                sensorTypeName = "Game Rotation";
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                sensorTypeName = "Geomagnetic Rotation";
                break;
            case Sensor.TYPE_GRAVITY:
                sensorTypeName = "Gravity";
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorTypeName = "Gyroscope";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                sensorTypeName = "Gyroscope Uncalibrated";
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorTypeName = "Heart Rate";
                break;
            case Sensor.TYPE_LIGHT:
                sensorTypeName = "Light";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorTypeName = "Linear Acceleration";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorTypeName = "Magnetic Field";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                sensorTypeName = "Magnetic Field Uncalibrated";
                break;
            case Sensor.TYPE_PRESSURE:
                sensorTypeName = "Pressure";
                break;
            case Sensor.TYPE_PROXIMITY:
                sensorTypeName = "Proximity";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorTypeName = "Relative Humidity";
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorTypeName = "Rotation";
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                sensorTypeName = "Step Detector";
                break;
            case Sensor.TYPE_STEP_COUNTER:
                sensorTypeName = "Step Counter";
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                sensorTypeName = "Significant Motion";
                break;
            case Sensor.TYPE_ORIENTATION:
                sensorTypeName = "Orientation";
                break;
            case Sensor.TYPE_TEMPERATURE:
                sensorTypeName = "Temperature";
                break;
            default:
                sensorTypeName = "(" + idx + ") Unknown";
                break;
        }
        return sensorTypeName;
    }

    public String getName() {
        return mSensor.getName();
    }

    public int getType() {
        return mSensor.getType();
    }

    public String getVendor() {
        return mSensor.getVendor();
    }
}
