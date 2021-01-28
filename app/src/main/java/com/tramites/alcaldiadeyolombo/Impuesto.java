package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;

public class Impuesto extends AppCompatActivity {

    public static class oDatosConsulta{
        String CodigoEntidad;
        String DatoConsulta;
        String CampoConsulta;
        int IDImpuesto;
        String Factura;

        public oDatosConsulta(String codigoEntidad, String datoConsulta, String campoConsulta, int idImpuesto,
                              String factura){
            CodigoEntidad = codigoEntidad;
            DatoConsulta = datoConsulta;
            CampoConsulta = campoConsulta;
            IDImpuesto = idImpuesto;
            Factura = factura;
        }
    }
}
