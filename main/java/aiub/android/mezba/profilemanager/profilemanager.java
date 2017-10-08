package aiub.android.mezba.profilemanager;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

public class profilemanager extends Service implements SensorEventListener {
    MainActivity context;

    AudioManager audioManager;
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor proximity;
    NotificationManager notificationManager;

    private boolean proxySilent=false;
    private boolean proxyRinger=false;
    //private int priority = 0;

    private boolean vibrate = false;
    private boolean ringing = false;
    private boolean dontdisturb = false;

    //private int modeConstantValue = audioManager.getRingerMode();

    private static final int SENSOR_SENSITIVITY = 4;

    private double X;
    private double Y;
    private double Z;

    @Override
    public IBinder onBind(Intent a) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.proximity=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(this.proximity!=null)
        {
            sensorManager.registerListener(this, proximity,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int acc) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            X = event.values[0];
            Y = event.values[1];
            Z = event.values[2];

            if ((X <= 11 && X >= -10) && (Y <= .7 && Y >= -.7) && (Z <= 10 && Z >= -8)) {
                //this.priority = 3;
                 this.vibrate = false;
                 this.ringing = true;
                 this.dontdisturb = false;
                 setRinging();
            }
            if ((X <= 2.7 && X >= -2.7) && (Y <= .7 && Y >= -.5) && (Z <= -7 && Z >= -11)) {

                this.vibrate = true;
                this.ringing = false;
                this.dontdisturb = false;
                setVibrate();
            }
            if ((X <= .5 && X >= -.5) && (Y >= 6) && (Z <= 8 && Z >= -8)) {
                //this.priority = 2;
                this.vibrate = false;
                this.ringing = false;
                this.dontdisturb = true;
                setDonotdisturb();
            }
        }

        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY)
        {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                    //near
                    //this.priority = 1;
                    this.vibrate = false;
                    this.ringing = false;
                    this.dontdisturb = true;

                    setDonotdisturb();
                    Log.d("Proximity","setSilent()");
                    //Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
                }
                else {
                    //far
                    setRinging();
                    Log.d("Proximity","setRinger()");
                    //Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started Successfully", Toast.LENGTH_SHORT).show();
        return START_STICKY;//when the phone runs out of memory and kills the service before it finishes executing.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        //Toast.makeText(this, "Service Finished Successfully", Toast.LENGTH_SHORT).show();
    }

    public void setRinging() {
            //Toast.makeText(this,"Ringing Mode Successfull",Toast.LENGTH_SHORT).show();
            Log.d("ACCELEROMETER","Ringing");
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            SystemClock.sleep(10);
    }

    public void setVibrate() {
            Log.d("ACCELEROMETER", "Vibrate");
            //Toast.makeText(this, "Vibrate Mode Successfull", Toast.LENGTH_SHORT).show();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            SystemClock.sleep(10);
    }

    public void setDonotdisturb() {
            try {
                // Toast.makeText(this, "Do not disturb Mode Successfull", Toast.LENGTH_SHORT).show();
                Log.d("ACCELEROMETER","Donotdisturb");
                //audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.cancel();
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                SystemClock.sleep(10);
            }
            catch (Exception ex) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            }
    }
}
