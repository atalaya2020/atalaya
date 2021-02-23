package com.atalaya.evaluador;

public interface IIndicadorProxyType {
	
	final static String tipo_bucle = "Bucle";
	final static String tipo_ws = "Ws";
	final static String tipo_fichero = "File";
	
	final static String comando_bucle = "para";
	final static String comando_ws = "ws";
	final static String comando_fichero = "file";
	
	
	
	public boolean ejecutar(IndicadorProxy IndicadorProxy) throws Exception;
	
}
