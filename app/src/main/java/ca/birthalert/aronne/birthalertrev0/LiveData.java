package ca.birthalert.aronne.birthalertrev0;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class LiveData extends ActionBarActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor orSensor;
    private static final int HISTORY_SIZE = 100;

    private XYPlot aprHistoryPlot = null;

    private SimpleXYSeries azimuthHistorySeries;
    private SimpleXYSeries pitchHistorySeries;
    private SimpleXYSeries rollHistorySeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);

        azimuthHistorySeries = new SimpleXYSeries("Azimuth");
        azimuthHistorySeries.useImplicitXVals();
        pitchHistorySeries = new SimpleXYSeries("Pitch");
        pitchHistorySeries.useImplicitXVals();
        rollHistorySeries = new SimpleXYSeries("Roll");
        rollHistorySeries.useImplicitXVals();

        aprHistoryPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.MAGENTA);
        aprHistoryPlot.getGraphWidget().getRangeGridLinePaint().setColor(Color.MAGENTA);
        aprHistoryPlot.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.MAGENTA);
        aprHistoryPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        aprHistoryPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        aprHistoryPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        aprHistoryPlot.addSeries(azimuthHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLUE, null, null));
        aprHistoryPlot.addSeries(pitchHistorySeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.GREEN, null, null));
        aprHistoryPlot.addSeries(rollHistorySeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.RED, null, null));
        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Time");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Angle (Degs)");
        aprHistoryPlot.getRangeLabelWidget().pack();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // orSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //sensorManager.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_NORMAL);}
        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }

        }

        if (orSensor == null) {
            System.out.println("Failed to attach to orientation sensor");
            cleanup();
        }

        sensorManager.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void cleanup() {
        sensorManager.unregisterListener(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {

        // get rid of the oldest sample in history:
        if (rollHistorySeries.size() > HISTORY_SIZE) {
            rollHistorySeries.removeFirst();
            pitchHistorySeries.removeFirst();
            azimuthHistorySeries.removeFirst();
        }

        //add the latest history sample:
        azimuthHistorySeries.addLast(null, event.values[0]);
        pitchHistorySeries.addLast(null, event.values[1]);
        rollHistorySeries.addLast(null, event.values[2]);

        //redraw the plots:
        aprHistoryPlot.redraw();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }



}