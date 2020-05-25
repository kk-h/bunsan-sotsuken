package android.example.myaccball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor1, sensor2, sensor3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensor3 = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float temperature = 0;
        float pressure = 0;
        float humidity = 0;

        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperature = event.values[0];
            TextView tem = (TextView)findViewById(R.id.temperatureView);
            tem.setText(String.valueOf(temperature));
        }

        if(event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressure = event.values[0];
            TextView pre = (TextView)findViewById(R.id.pressureView);
            pre.setText(String.valueOf(pressure));
        }

        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            humidity = event.values[0];
            TextView hum = (TextView)findViewById(R.id.humidityView);
            hum.setText(String.valueOf(humidity));
        }

        if(pressure >= 1013.0){
            TextView fore = (TextView)findViewById(R.id.forecastView);
            fore.setText("晴れ");
        }else{
            TextView fore = (TextView)findViewById(R.id.forecastView);
            fore.setText("曇りまたは雨");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor1, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensor2, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensor3, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
