package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaImpuesto extends AppCompatActivity {

    private EditText et_documento, et_correo, et_telefono;
    private Spinner spnDatoCons;
    private Button btn_buscar;
    private ScrollView svConsultaImpuesto;
    private TextView tvTipoImpuesto;
    String CampoConsulta, IDImpuesto ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_impuesto);

        //Variables que validan conexion
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Icono en el actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        et_documento = (EditText)findViewById(R.id.et_documento);
        et_correo = (EditText)findViewById(R.id.et_correo);
        et_correo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        et_telefono = (EditText)findViewById(R.id.et_telefono);
        btn_buscar = (Button)findViewById(R.id.btn_buscar);
        svConsultaImpuesto = (ScrollView)findViewById(R.id.svConsultaImpuesto);
        spnDatoCons = (Spinner) findViewById(R.id.spnDatoConsulta);
        tvTipoImpuesto = (TextView)findViewById(R.id.tvTipoImpuesto);

        IDImpuesto  = getIntent().getStringExtra("IDImpuesto");

        if (IDImpuesto.equals("1")){
            tvTipoImpuesto.setText("PAGOS SEGUROS EN LINEA DE IMPUESTO PREDIAL UNIFICADO");
        }
        if (IDImpuesto.equals("2")){
            tvTipoImpuesto.setText("PAGOS SEGUROS EN LINEA DE IMPUESTO DE INDUSTRIA Y COMERCIO");
        }

        svConsultaImpuesto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        ArrayAdapter spnDatoConsulta = ArrayAdapter.createFromResource(ConsultaImpuesto.this, R.array.parametroconsulta, R.layout.spinner_item_personalizado);
        spnDatoCons.setAdapter(spnDatoConsulta);

        //validamos conexion
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            Toast.makeText(this,
                    "PASO 1", Toast.LENGTH_LONG).show();
            btn_buscar.setEnabled(true);
        } else {
            Toast.makeText(this,
                    "No se detecta conexión a internet, revise su conexión e ingrese nuevamente", Toast.LENGTH_LONG).show();
            btn_buscar.setEnabled(false);
        }
    }

    public void Consultar(View view){
        if (spnDatoCons.getSelectedItemId() == 0) {
            Toast.makeText(ConsultaImpuesto.this, " Seleccione tipo de dato para consultar. ",Toast.LENGTH_SHORT).show();
            spnDatoCons.requestFocus();
            return;
        }
        if (spnDatoCons.getSelectedItemId() == 1) {
            CampoConsulta = "Identificacion";
        }
        if (spnDatoCons.getSelectedItemId() == 2) {
            CampoConsulta = "Referencia";
        }
        if (et_documento.getText().length() == 0) {
            et_documento.setError("Ingrese un documento");
            et_documento.requestFocus();
            return;
        }
        if (et_documento.getText().length() > 0 && et_documento.getText().length() < 4) {
            et_documento.setError("Ingrese un número de documento valido");
            et_documento.requestFocus();
            return;
        }
        /*String email = et_correo.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            et_correo.setError("Ingrese un correo electrónico valido");
            et_correo.requestFocus();
            return;
        }*/
        if (et_correo.getText().length() == 0) {
            et_correo.setError("Ingrese correo electrónico");
            et_correo.requestFocus();
            return;
        }
        String email = et_correo.getText().toString().trim();
        String emailPattern = "[A-AA-Z0-9._-]+@[A-Z]+\\.+[A-Z]+";
        String emailPatternD = "[A-ZA-Z0-9._-]+@[A-Z]+\\.+[A-Z]+\\.+[A-Z]+";
        if (!email.matches(emailPattern) && !email.matches(emailPatternD)) {
            et_correo.setError("Ingrese un correo electrónico valido");
            et_correo.requestFocus();
            return;
        }
        if (et_telefono.getText().length() == 0) {
            et_telefono.setError("Ingrese número de telefono");
            et_telefono.requestFocus();
            return;
        }
        if (et_telefono.getText().length() != 10 && et_telefono.getText().length() != 0) {
            et_telefono.setError("Ingrese un número de telefono valido");
            et_telefono.requestFocus();
            return;
        }

        Intent intent = new Intent(this, RespuestaConsulta.class);
        intent.putExtra("CampoConsulta", CampoConsulta);
        intent.putExtra("DatoConsulta", et_documento.getText().toString());
        intent.putExtra("IDImpuesto", IDImpuesto);
        intent.putExtra("documento", et_documento.getText().toString());
        intent.putExtra("correo", et_correo.getText().toString());
        intent.putExtra("telefono", et_telefono.getText().toString());
        startActivity(intent);
        finish();
    }

    public void Cancelar(View view){
        Intent i = new Intent(ConsultaImpuesto.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){

    }
}