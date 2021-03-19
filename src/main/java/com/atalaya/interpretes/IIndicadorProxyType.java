package com.atalaya.interpretes;

public interface IIndicadorProxyType {
	
	final static String tipo_bucle = "Bucle";
	final static String tipo_ws = "Ws";
	final static String tipo_query = "Query";
	final static String tipo_volcado = "VolcadoIndicador";
	
	final static String comando_bucle = "para";
	final static String comando_ws = "ws";
	final static String comando_fichero_Reader = "fileReader";
	final static String comando_fichero_Writer = "fileWriter";
	
	final static String separadorFichero = "\\|";

	//"/var/lib/sistemaFicheros/"
	
	public boolean ejecutar(IndicadorProxy IndicadorProxy) throws Exception;
	
}
