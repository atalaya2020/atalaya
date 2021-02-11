package com.atalaya.evaluador;

import java.util.Hashtable;

public interface IIndicadorProxyType {
	
	final static String tipo_bucle = "Bucle";
	final static String tipo_ws = "Ws";
	
	final static String comando_bucle = "para";
	final static String comando_ws = "ws";
	
	public boolean ejecutar(IndicadorProxy IndicadorProxy) throws Exception;
	
}
