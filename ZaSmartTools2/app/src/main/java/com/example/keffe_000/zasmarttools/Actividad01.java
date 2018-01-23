package com.example.keffe_000.zasmarttools;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Actividad01 extends AppCompatActivity {
    //Declaración de variables
    float nivel_brillo=0;
    float cantidad_luz=0;
    float rango_maximo=0;
    ProgressBar nivel_luz;
    TextView tv_luz, tv_brillo;

    //Método onCreate de la actividad 1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad01);
        nivel_luz = (ProgressBar) findViewById(R.id.progressBar_luz);  //Alambrado del progressbar
        tv_luz = (TextView) findViewById(R.id.luz_textView);//Alambrado del textview

        //Alambrado del sensor de luz
        SensorManager administrador_sensores = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor_luz = administrador_sensores.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor_luz != null) {//Comprueba que el dispositivo tenga sensor de luz
            rango_maximo = sensor_luz.getMaximumRange(); //Obtiene el valor máximo que se obtiene del sensor
            nivel_luz.setMax((int) rango_maximo);
            /*Registrando el listener del sensor de luz*/
            administrador_sensores.registerListener(
                    Sensor_Luz_Listener,
                    sensor_luz,
                    SensorManager.SENSOR_DELAY_NORMAL
            );

        } else {
            DialogoAlerta(findViewById(R.id.brillo_textView),"El dispositivo no cuenta con sensor de luz");

        }


    }   // Fin del Oncreate de la Actividad 01

    //Programación del evento del sensor
    private final SensorEventListener Sensor_Luz_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent ev) {//Salta cuando el valor del sensor de luz cambia
            final float lectura = ev.values[0]; //Lee el nivel de luz
            cantidad_luz = lectura;
            float brillo= getBrillo();
            if(ev.sensor.getType()==Sensor.TYPE_LIGHT ){  //Comprueba que el valor proviene del sensor deluz
                tv_luz.setText("Luz: " + lectura+" lux");
                nivel_luz.setProgress((int) ev.values[0]);
                //Compara la cantidad de luz y brillo del telefono para identificar si el usuario utiliza mucha claridad.
                if(lectura<200 && nivel_brillo!= brillo && brillo>25){
                    pico();
                    nivel_brillo=brillo;
                }
                if(lectura > 200 && lectura < rango_maximo / 2 && nivel_brillo!= brillo && brillo>155){
                    pico();
                    nivel_brillo=brillo;
                }
                if(lectura > rango_maximo/2 && lectura < rango_maximo*.95  && nivel_brillo!= brillo && brillo>205){
                    pico();
                    nivel_brillo=brillo;
                }
            }


            // alambramos el Button
            Button MiButton = (Button) findViewById(R.id.btn_luz);

            //Programamos el evento onclick

            MiButton.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View arg0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        permisos();
                        //Asigna el nivel de brillo optimizado al telefono según la cantidad de luz percibida
                        if (Settings.System.canWrite(getApplicationContext())) { //Comprueba que la aplicacion tenga
                            // permiso para modificar las configuraciones
                            if (cantidad_luz < 200) {
                                ContentResolver resolver = getContentResolver();
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 20);
                            } else if (cantidad_luz > 200 && cantidad_luz < rango_maximo / 2) {
                                ContentResolver resolver = getContentResolver();
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 125);

                            } else if (cantidad_luz > rango_maximo / 2 && cantidad_luz < rango_maximo * .95) {
                                ContentResolver resolver = getContentResolver();
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 200);
                            } else if (cantidad_luz > rango_maximo * .95) {
                                ContentResolver resolver = getContentResolver();
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 255);
                            }
                        } else {
                            DialogoAlerta(tv_luz, "Debe otorgar los permisos correspondientes");
                        }
                    } else {

                        DialogoAlerta(tv_luz, "Su versión de android no es compatible con esta función");
                    }
                }

            });
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};

    public void pico(){//Notifica que el usuario esta utilizando mucho brillo

        DialogoAlerta(tv_brillo, "¡Utiliza mucho brillo!");
    }


    float getBrillo() { //Obtiene el brillo del sistema, muestra en el textview correspondiente y lo retorna
        float brillo=0;
        try {
            tv_brillo = (TextView) findViewById(R.id.brillo_textView);
            brillo = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            tv_brillo.setText(String.valueOf("Brillo: "+brillo));

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brillo;
    }


    public void DialogoAlerta(View view, String mensaje){ //Diálogo

        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setMessage(mensaje);
        builder1.setCancelable(true);
        builder1.setNeutralButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    };

    public void permisos() { //Se utiliza para que en los dispositivos con una versión de android 6 o más obtenga el permiso para
        //cambiar la configuración del sisttema

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.System.canWrite(getApplicationContext())){
                Intent intento = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivityForResult(intento,200);
            }
        }
    }
}
