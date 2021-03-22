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

	final static String propiedad_url = "url";
	final static String propiedad_user = "user";
	final static String propiedad_password = "password";
	final static String propiedad_cachePrepStmts = "cachePrepStmts";
	final static String propiedad_prepStmtCacheSize = "prepStmtCacheSize";
	final static String propiedad_prepStmtCacheSqlLimit = "prepStmtCacheSqlLimit";
	final static String propiedad_maximumpoolsize = "maximumpoolsize";

	//"/var/lib/sistemaFicheros/"
	
	public boolean ejecutar(IndicadorProxy IndicadorProxy) throws Exception;
	
}
