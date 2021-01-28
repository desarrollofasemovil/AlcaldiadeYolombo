package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Gel extends AppCompatActivity {

    public static class Ciudadano{
        int Id_TipoDocumento;
        String NroDocumento;
        DetalleContribuyente DetalleContribuyente;
        String Entidad;
        String Tramite;
        ArrayList<Correo> Correos;
        ArrayList<Telefono> Telefonos;
        ArrayList<Direccion> Direcciones;

        public Ciudadano(int id_TipoDocumento, String nroDocumento, DetalleContribuyente detalleContribuyente,
                         String entidad, String tramite, ArrayList<Correo> correos, ArrayList<Telefono> telefonos,
                         ArrayList<Direccion> direcciones){
            Id_TipoDocumento = id_TipoDocumento;
            NroDocumento = nroDocumento;
            DetalleContribuyente = detalleContribuyente;
            Entidad = entidad;
            Tramite = tramite;
            Correos = correos;
            Telefonos = telefonos;
            Direcciones = direcciones;
        }
    }

    public static class DetalleContribuyente{
        public String PrimerNombre;
        public String SegundoNombre;
        public String PrimerApellido;
        public String SegundoApellido;
        public String NombreCompleto;

        public DetalleContribuyente(String primerNombre,String segundoNombre,String primerApellido,String segundoApellido,
                                    String nombreCompleto){
            PrimerNombre = primerNombre;
            SegundoNombre = segundoNombre;
            PrimerApellido = primerApellido;
            SegundoApellido = segundoApellido;
            NombreCompleto = nombreCompleto;
        }
    }

    public static class Correo{
        public int Id;
        public String Email;

        public Correo(int id,String email){
            Id = id;
            Email = email;
        }
    }

    public static class Telefono{
        public int Id;
        public String Numero;
        public int Indicativo=0;

        public Telefono(int id,String numero,int indicativo){
            Id = id;
            Numero = numero;
            Indicativo = indicativo;
        }
    }

    public static class Direccion{
        public int Id;
        public String Direccion;
        public int Id_Municipio=0;

        public Direccion(int id,String direccion,int id_Municipio){
            Id = id;
            Direccion = direccion;
            Id_Municipio = id_Municipio;
        }
    }
}
