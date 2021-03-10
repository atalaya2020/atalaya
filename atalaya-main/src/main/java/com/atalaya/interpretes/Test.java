package com.atalaya.interpretes;

import java.util.ArrayList;

import com.modelodatos.Analisis;
import com.modelodatos.Configuracion;
import com.modelodatos.Criterio;
import com.modelodatos.Evento;
import com.modelodatos.Indicador;
import com.modelodatos.Parametro;

public class Test {
    

    public static void main(String[] args) {
    ArrayList<Indicador> indicadores = new ArrayList<>();
    ArrayList<Criterio> criterios = new ArrayList<>();
    ArrayList<Evento> eventos = new ArrayList<>();
    ArrayList<Configuracion> configuraciones = new ArrayList<>();

    Parametro param1 = new Parametro("NumeroHilos", "Entero", "10");
    Configuracion conf1 = new Configuracion("Threads", "Info numero hilos", param1);

    Parametro param2 = new Parametro("TiempoEjecucion", "Entero", "100000");
    //Parametro param2 = new Parametro();
    Configuracion conf2 = new Configuracion("Tiempos", "Info tiempo ejecucion", param2);

    configuraciones.add(conf1); 
    configuraciones.add(conf2);

    Analisis analisis = new Analisis("nombre", "descripcion", indicadores, criterios, eventos, configuraciones);

    AnalisisProxy analisisP = new AnalisisProxy(analisis);

    }
}
