package com.atalaya.evaluador;

public interface IIndicadorProxy {
	
	public final String TIPO_QUERY = "Query"; 	//Este tipo de indicador define una query con bind variables que pueden tomar valores fijos o dinamicos
	public final String TIPO_BUCLE = "Bucle";	//Este tipo de alias define una relacion entre alias tipo para cada elemento del alias "a" aplica el alias "b" 
	public final String TIPO_WS = "WS"; 		//Este tipo de indicador define un recurso tipo WebService
	
	public final String ESTADO_EJECUTADO = "EJEUCTADO";
	public final String ESTADO_NOEJEUCTADO = "NOEJECUTADO";
	public final String ESTADO_EJECUTANDO = "EJECUTANDO";
	public final String ESTADO_VALIDANDO = "VALIDANDO";
	
	public final String ESTADO_INDICADOR_OK = "OK";
	public final String ESTADO_FALLO = "FALLO";
	
	public final int MAX_THEAD = 10;
	
	public boolean validar();
	 
	public boolean ejecutar();

}
