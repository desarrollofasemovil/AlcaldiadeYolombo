package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;

public class Pasarela extends AppCompatActivity {

    public static class oDataTransactionEntities{
        String Referencia;
        String Factura;
        String CodigoMunicipio;
        String TipoDocumento;
        String Identificacion;
        String Nombre;
        Double Total;
        int IDImpuesto;
        String Email;
        String Telefono;
        int FuentePago;
        int TipoImplementacion;


        public oDataTransactionEntities(String referencia, String factura, String codigoMunicipio, String tipoDocumento,
                                        String identificacion, String nombre, Double total, int idImpuesto, String email,
                                        String telefono, int fuentePago, int tipoImplementacion){
            Referencia = referencia;
            Factura = factura;
            CodigoMunicipio = codigoMunicipio;
            TipoDocumento = tipoDocumento;
            Identificacion = identificacion;
            Nombre = nombre;
            Total = total;
            IDImpuesto = idImpuesto;
            Email = email;
            Telefono = telefono;
            FuentePago = fuentePago;
            TipoImplementacion = tipoImplementacion;
        }
    }
}
