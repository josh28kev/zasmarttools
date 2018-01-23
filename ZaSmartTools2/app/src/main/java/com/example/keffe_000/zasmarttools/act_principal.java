package com.example.keffe_000.zasmarttools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class act_principal extends AppCompatActivity {

    public ProgressDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_principal);

        OnclickDelImageView(R.id.imageButtonPr1);
        OnclickDelImageView(R.id.imageButtonPr2);
        OnclickDelImageView(R.id.imageButtonPr3);
        OnclickDelImageView(R.id.imageButtonPr4);
        OnclickDelTextView(R.id.textViewPr1);
        OnclickDelTextView(R.id.textViewPr2);
        OnclickDelTextView(R.id.textViewPr3);
        OnclickDelTextView(R.id.textViewPr4);
    }

    /////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.opciones, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itm1: {
                Intent intento = new Intent(getApplicationContext(), act_integrantes.class);
                startActivity(intento);
                break;
            }
            case R.id.itm2: {
                //Colocar el video tutorial
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/UgjPA31yz0U")));
                break;
            }
            default:  Mensaje("No clasificado"); break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    public void OnclickDelImageView(int ref) {
        // Ejemplo  OnclickDelImageButton(R.id.MiImageButton);
        // 1 Doy referencia al ImageButton
        View view =findViewById(ref);
        ImageView miImageButton = (ImageView) view;
        //  final String msg = miImageButton.getText().toString();
        // 2.  Programar el evento onclick
        miImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                switch (v.getId()) {

                    case R.id.imageButtonPr1:
                        invocar(1);
                        break;

                    case R.id.imageButtonPr2:
                        invocar(2);
                        break;

                    case R.id.imageButtonPr3:
                        invocar(3);
                        break;

                    case R.id.imageButtonPr4:
                        invocar(4);
                        break;
                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelImageButton

    public void OnclickDelTextView(int ref) {

        // Ejemplo  OnclickDelTextView(R.id.MiTextView);
        // 1 Doy referencia al TextView
        View view =findViewById(ref);
        TextView miTextView = (TextView) view;
        //  final String msg = miTextView.getText().toString();
        // 2.  Programar el evento onclick
        miTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                switch (v.getId()) {

                    case R.id.textViewPr1:
                        invocar(1);
                        break;

                    case R.id.textViewPr2:
                        invocar(2);
                        break;

                    case R.id.textViewPr3:
                        invocar(3);
                        break;

                    case R.id.textViewPr4:
                        invocar(4);
                        break;
                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelTextView

    public void invocar(int n) {
        switch(n) {
            case 1: {
                crearProgress();
                Intent intento0 = new Intent(getApplicationContext(), Act_trafico_datos.class);
                startActivity(intento0);
                break;
            }
            case 2: {
                crearProgress();
                Intent intento = new Intent(getApplicationContext(), Act_aplicaciones.class);
                startActivity(intento);
                break;
            }
            case 3: {
                Intent intento3 = new Intent(getApplicationContext(), Actividad01.class);
                startActivity(intento3);
                break;
            }
            case 4: {
                crearProgress();
                Intent intento2 = new Intent(getApplicationContext(), act_ram.class);
                startActivity(intento2);
                break;
            }
            default: {
                Mensaje("No se ha implementado esta función");
                break;
            }
        }
    }

    public void crearProgress() {
        progress = new ProgressDialog(act_principal.this);
        progress.setTitle("Cargando");
        progress.setMessage("Por favor espere");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(progress != null)
            progress.dismiss();
    }
}

