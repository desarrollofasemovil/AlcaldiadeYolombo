package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RespuestaConsulta extends AppCompatActivity {

    private ListView lv_consulta;
    public ArrayList data = new ArrayList<String>();
    JSONObject json_data;
    public JSONArray lista;
    private TextView tvNulo;
    String documento, correo, telefono, DataConsulta, CampoConsultar, IDImpuestos;
    String facturad, nombre, valor, impuesto, referencia, base64, idmunicipio, datos;
    private ProgressBar progressBar;
    final String urlPasarela = "http://181.143.126.123:7777/InsertTransaction";
    final String urlAPIData = "http://201.184.190.109:7778/api/ImpuestoEntidad/GetImpuestos";
    final String urlAPIFactura = "http://201.184.190.109:7778/api/ImpuestoEntidad/GetDownloadPDF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_consulta);

        init();
        progressBar.setVisibility(View.VISIBLE);

        //Variables que validan conexion
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Icono en el actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        CampoConsultar = getIntent().getStringExtra("CampoConsulta");
        DataConsulta = getIntent().getStringExtra("DatoConsulta");
        IDImpuestos = getIntent().getStringExtra("IDImpuesto");
        documento = getIntent().getStringExtra("documento");
        correo = getIntent().getStringExtra("correo");
        telefono = getIntent().getStringExtra("telefono");
        lv_consulta = (ListView) findViewById(R.id.lv_consulta);
        tvNulo = (TextView) findViewById(R.id.tvNulo);

        isStoragePermissionGranted();

        //validamos conexion
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Toast.makeText(this,
                    "PASO 2", Toast.LENGTH_LONG).show();
            InvocarServicioObtenerConsulta ws = new InvocarServicioObtenerConsulta();
            //ws.execute();

            try {
                String CodigoEntidad = "890984030";
                String DatoConsulta = DataConsulta;
                String CampoConsulta = CampoConsultar;
                int IDImpuesto = Integer.parseInt(IDImpuestos);
                String Factura = "";

                new RespuestaConsulta.InvocarServicioObtenerConsulta().execute(CodigoEntidad, DatoConsulta, CampoConsulta,
                        String.valueOf(IDImpuesto), Factura);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this,
                    "No se detecta conexión a internet, revise su conexión e ingrese nuevamente", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        this.progressBar = findViewById(R.id.progressBar);
    }

    public class ViewHolder {
        TextView datos;
        ImageView pdf;
        ImageView pse;
    }

    //Metodo para consumir el API
    private class InvocarServicioObtenerConsulta extends AsyncTask<String, Void, String> {
        private String resp;

        //HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... strings) {
            //StringBuilder result = new StringBuilder();
            String result = "";
            try {

                String codigoEntidad = strings[0];
                String datoConsulta = strings[1];
                String campoConsulta = strings[2];
                int idImpuesto = Integer.parseInt(strings[3]);
                String factura = strings[4];

                Impuesto.oDatosConsulta oDatosConsulta = new Impuesto.oDatosConsulta(codigoEntidad, datoConsulta, campoConsulta, idImpuesto, factura);

                Gson gson = new Gson();
                String json = gson.toJson(oDatosConsulta);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                RequestBody Body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder().url(urlAPIData)
                        .post(Body).build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        result = response.body().string();

                    } else {
                        Toast.makeText(RespuestaConsulta.this,
                                "En el momento no es posible realizar transaccion, comuniquese con la alcaldia municipal y reporte el error.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } catch (Exception e) {
                Toast.makeText(RespuestaConsulta.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                //urlConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            generateListContent(result);
        }

        @Override
        protected void onPreExecute() {

        }
    }

    private void generateListContent(String msgjson) {
        try {

            JSONObject obj = new JSONObject(msgjson);
            lista = obj.optJSONArray("Informacion");

            String conte = "";

            //Variables para controloar separador de miles
            String valorp = "";
            DecimalFormat formatea = new DecimalFormat("###,###.##");

            if (lista == null) {
                tvNulo.setText("El documento ingresado no cuenta con facturas pendientes");
                lv_consulta.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);

            } else {
                for (int i = 0; i < lista.length(); i++) {
                    JSONObject json_data = lista.getJSONObject(i);
                    //capturo el valor a pagar
                    valorp = json_data.getString("Valor");
                    //le doy el formato de separador de miles al valor
                    valorp = String.valueOf(formatea.format(Double.parseDouble(valorp)));

                    conte = "Entidad: " + json_data.getString("Entidad") + "\n"
                            + "Documento: " + json_data.getString("Documento") + "\n"
                            + "Nombre: " + json_data.getString("Nombre") + "\n"
                            + "Impuesto: " + json_data.getString("Impuesto") + "\n"
                            + "Número Factura: " + json_data.getString("Factura") + "\n"
                            + "Valor: $" + valorp + "\n"
                            + "Fecha Vencimiento: " + json_data.getString("FechaVencimiento");

                    //Enviamos data a gel
                    try {
                        String DocumentType = "CC";
                        String Document = documento;
                        String Name = nombre;
                        String Email = correo;
                        String Phone = telefono;
                        String MunicipalityCode = "890984030";
                        String Ruta = "";

                        String Type = "1";
                        if (DocumentType == "CE") {
                            Type = "3";
                        } else if (DocumentType == "NIT") {
                            Type = "2";
                        }

                        new RespuestaConsulta.Gel().execute(Type, Document, Ruta, Ruta, Ruta, Ruta, Name, MunicipalityCode, Email, Phone, Ruta);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    data.add(conte);

                    //AGREGAR CONDICIONAL PARA VALIDAR SI ES IMPUESTO O SERVICIO PUBLICO

                }

                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
                //lvConsultaImpuestos.setAdapter(adapter);

                /*lvConsultaImpuestos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(RespuestaConsultaImpuestos.this, (CharSequence) data.get(position), Toast.LENGTH_LONG).show();
                    }
                });*/

                MyAdapter myAdapter = new MyAdapter(this, R.layout.lista, data);
                lv_consulta.setAdapter(myAdapter);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);

                lv_consulta.setVisibility(View.VISIBLE);

            }

        } catch (Exception ex) {
            Toast.makeText(this, "Error cargar lista" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {

        }
    }

    public class MyAdapter extends BaseAdapter implements Serializable {

        private Context context;
        private int layout;
        private List<String> data;

        public MyAdapter(Context context, int layout, List<String> data) {
            this.context = context;
            this.layout = layout;
            this.data = data;
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public Object getItem(int position) {
            return this.data.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null){
                LayoutInflater layoutInflater = LayoutInflater.from(this.context);
                convertView = layoutInflater.inflate(R.layout.lista, null);

                holder = new ViewHolder();
                holder.dataTextView = (TextView) convertView.findViewById(R.id.tvData);
                holder.pseImageView = (ImageView) convertView.findViewById(R.id.btnPse);

                lv_consulta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(RespuestaConsulta.this, (CharSequence) data.get(position), Toast.LENGTH_LONG).show();
                    }
                });


                holder.pseImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        lv_consulta.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        datos = data.get(position);

                        try {
                            JSONObject json_data = lista.getJSONObject(position);

                            documento = json_data.getString("Documento");
                            facturad = json_data.getString("Factura");
                            nombre = json_data.getString("Nombre");
                            impuesto = json_data.getString("Impuesto");
                            valor = json_data.getString("Valor");
                            referencia = json_data.getString("Factura");
                            idmunicipio = json_data.getString("CodigoEntidad");

                            try {
                                String DocumentType = "CC";
                                String Document = documento;
                                String Name = nombre;
                                String Email = correo;
                                String Phone = telefono;
                                int IdTramite = 1;
                                if (impuesto.equals("Impuesto Predial Unificado")) {
                                    IdTramite = 1;
                                }
                                if (impuesto.equals("Industria y Comercio")){
                                    IdTramite = 2;
                                }

                                String IdProcess = String.valueOf(IdTramite);
                                String MunicipalityCode = idmunicipio;
                                String Valor = valor;
                                Valor = Valor.replace(",", "");
                                float PayValue = Float.parseFloat(Valor);
                                String Reference = facturad;
                                String Ruta = "";
                                String TickedID = Reference;

                                String Type = "1";
                                if (DocumentType == "CE") {
                                    Type = "3";
                                } else if (DocumentType == "NIT") {
                                    Type = "2";
                                }

                                //campos nueva pasarela
                                String Referencia = facturad;
                                String Factura = facturad;
                                String CodigoMunicipio = MunicipalityCode;
                                String TipoDocumento = DocumentType;
                                String Identificacion = documento;
                                String Nombre = nombre;
                                Double Total = Double.parseDouble(Valor);
                                int IDImpuesto = IdTramite;
                                //email
                                String Telefono = telefono;
                                int FuentePago = 2;
                                int TipoImplementacion = 1;


                                new RespuestaConsulta.Gel().execute(Type, Document, Ruta, Ruta, Ruta, Ruta, Name, MunicipalityCode, Email, Phone, Ruta);

                                new RespuestaConsulta.Transaction().execute(Referencia, Factura, CodigoMunicipio, TipoDocumento,
                                        Identificacion, Nombre, String.valueOf(Total), String.valueOf(IDImpuesto), Email, Telefono,
                                        String.valueOf(FuentePago), String.valueOf(TipoImplementacion));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String currentData = data.get(position);
            //currentData = (String)getItem(position);

            holder.dataTextView.setText(currentData);

            return convertView;
        }

        public class ViewHolder{
            private TextView dataTextView;
            private ImageView pdfImageView;
            private ImageView pseImageView;
        }

    }

    public class Gel extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            int Id_TipoDocumento = Integer.parseInt(strings[0]);
            String NroDocumento = strings[1];
            String PrimerNombre = strings[2];
            String SegundoNombre = strings[3];
            String PrimerApellido = strings[4];
            String SegundoApellido = strings[5];
            String NombreCompleto = strings[6];
            String Entidad = strings[7];
            String Tramite = "AppMovil-Esteban";
            String Email = strings[8];
            String Numero = strings[9];
            String Direccion = strings[10];

            com.tramites.alcaldiadeyolombo.Gel.DetalleContribuyente detalleContribuyente
                    = new com.tramites.alcaldiadeyolombo.Gel.DetalleContribuyente(PrimerNombre,
                    SegundoNombre, PrimerApellido, SegundoApellido, NombreCompleto);
            com.tramites.alcaldiadeyolombo.Gel.Correo correo = new com.tramites.alcaldiadeyolombo.Gel.Correo(0, Email);
            com.tramites.alcaldiadeyolombo.Gel.Telefono telefono = new com.tramites.alcaldiadeyolombo.Gel.Telefono(0, Numero, 0);
            com.tramites.alcaldiadeyolombo.Gel.Direccion direccion = new com.tramites.alcaldiadeyolombo.Gel.Direccion(0, Direccion, 0);

            ArrayList<com.tramites.alcaldiadeyolombo.Gel.Correo> Correos = new ArrayList<com.tramites.alcaldiadeyolombo.Gel.Correo>();
            Correos.add(correo);
            ArrayList<com.tramites.alcaldiadeyolombo.Gel.Telefono> Telefonos = new ArrayList<com.tramites.alcaldiadeyolombo.Gel.Telefono>();
            Telefonos.add(telefono);
            ArrayList<com.tramites.alcaldiadeyolombo.Gel.Direccion> Direcciones = new ArrayList<com.tramites.alcaldiadeyolombo.Gel.Direccion>();
            Direcciones.add(direccion);
            com.tramites.alcaldiadeyolombo.Gel.Ciudadano ciudadano = new com.tramites.alcaldiadeyolombo.Gel.Ciudadano(Id_TipoDocumento,
                    NroDocumento, detalleContribuyente, Entidad, Tramite, Correos, Telefonos, Direcciones);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{  \"Username\": \"PredialApp\",  \"Password\": \"1Cero12020$/*\"}");
            Request request = new Request.Builder()
                    .url("http://201.184.190.108:9892/api/Login/authenticate")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String token = response.body().string().replace("\"", "");

                    Gson gson = new Gson();
                    String json = gson.toJson(ciudadano);

                    OkHttpClient cliente = new OkHttpClient().newBuilder()
                            .build();
                    MediaType media = MediaType.parse("application/json");
                    RequestBody cuerpo = RequestBody.create(media, json.toString());
                    Request solicitud = new Request.Builder()
                            .url("http://201.184.190.108:9892/api/Contribuyente/GuardarContribuyente")
                            .method("POST", cuerpo)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    Response resultado = cliente.newCall(solicitud).execute();
                } else {
                    Toast.makeText(RespuestaConsulta.this,
                            "En el momento no es posible realizar transaccion, comuniquese con la alcaldia municipal y reporte el error.",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {

            }
            return null;
        }
    }

    public class Transaction extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String referencia = strings[0];
            String factura = strings[1];
            String codigoMunicipio = strings[2];
            String tipoDocumento = strings[3];
            String identificacion = strings[4];
            String nombre = strings[5];
            Double total = Double.parseDouble(strings[6]);
            int idImpuesto = Integer.parseInt(strings[7]);
            String email = strings[8];
            String telefono = strings[9];
            int fuentePago = Integer.parseInt(strings[10]);
            int tipoImplementacion = Integer.parseInt(strings[11]);

            Pasarela.oDataTransactionEntities oDataTransactionEntities = new Pasarela.oDataTransactionEntities(referencia,
                    factura, codigoMunicipio, tipoDocumento, identificacion, nombre, total, idImpuesto, email, telefono,
                    fuentePago, tipoImplementacion);

            Gson gson = new Gson();
            String json = gson.toJson(oDataTransactionEntities);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            //okHttpClient.connectTimeoutMillis();
            //okHttpClient.readTimeoutMillis();
            RequestBody Body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder().url(urlPasarela)
                    .post(Body).build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();

                    //llamo la respuesta de la pasarela
                    JSONObject json_data = new JSONObject(result);
                    //capturo el url de la pasarela dentro del objeto json
                    result = json_data.getString("URL");

                    Intent i = new Intent(RespuestaConsulta.this, PasarelaPago.class);
                    i.putExtra("result", result);
                    startActivity(i);
                } else {
                    Toast.makeText(RespuestaConsulta.this,
                            "En el momento no es posible realizar transaccion, comuniquese con la alcaldia municipal y reporte el error.",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void ShowMessage(final String mensaje, final Context context) {
            Thread thread = new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    });
                }
            };
            thread.start();
        }
    }

    public void Salir(View view) {
        Intent i = new Intent(RespuestaConsulta.this, MainActivity.class);
        startActivity(i);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onBackPressed() {

    }
}