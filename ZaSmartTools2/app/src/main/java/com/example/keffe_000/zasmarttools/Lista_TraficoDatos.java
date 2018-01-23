package com.example.keffe_000.zasmarttools;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Lista_TraficoDatos extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombre;
    private String[] envio;
    private String[] recepcion;
    private boolean[] cambios;

    public Lista_TraficoDatos(Activity context, String[] nombre_apl, String[] envio_apl, String[] recepcion_apl, boolean[] cambios_apl) {
        super(context, R.layout.traficodatos_elemento, nombre_apl);
        this.context = context;
        this.nombre = nombre_apl;
        this.envio = envio_apl;
        this.recepcion = recepcion_apl;
        this.cambios = cambios_apl;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.traficodatos_elemento, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewtr);
        TextView txtTitleE = (TextView) rowView.findViewById(R.id.textViewtr2);
        TextView txtTitleR = (TextView) rowView.findViewById(R.id.textViewtr3);

        txtTitle.setText(nombre[position]);
        txtTitleE.setText(envio[position]);
        txtTitleR.setText(recepcion[position]);
        if(cambios[position]) rowView.setBackgroundColor(Color.GREEN);

        return rowView;

    };
}
