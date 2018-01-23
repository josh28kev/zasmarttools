package com.example.keffe_000.zasmarttools;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Pablo v2 on 2/6/2017.
 */
public class Lista_RegistroDatos extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] nombre;
    private final String[] datos;

    public Lista_RegistroDatos (Activity context, String[] nombre_apl, String[] datos_apl) {
        super(context, R.layout.registrodatos_elemento, nombre_apl);
        this.context = context;
        this.nombre = nombre_apl;
        this.datos = datos_apl;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.registrodatos_elemento, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewrd);
        TextView txtTitleD = (TextView) rowView.findViewById(R.id.textViewrd2);

        txtTitle.setText(nombre[position]);
        txtTitleD.setText(datos[position]);

        return rowView;

    };
}
