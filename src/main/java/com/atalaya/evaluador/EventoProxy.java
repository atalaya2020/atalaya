package com.atalaya.evaluador;

import java.util.Vector;

import org.springframework.web.client.RestTemplate;

import com.modelodatos.Evento;
import com.modelodatos.Parametro;


public class EventoProxy {

	private Evento evento;
	private String resultadoEjecucion;

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public EventoProxy(Evento ev) {
		evento = ev;
		// TODO Auto-generated constructor stub	
	}
	
	public EventoProxy() {
		evento = new Evento();
		// TODO Auto-generated constructor stub	
	}
		
	public void generarEvento () {
		
		if (this.evento.getTipo().equals("WS")) {							// Llamada a un Web Service
			llamaWebService();
		}
	}
	
	public String getResultadoEjecucion() {
		return resultadoEjecucion;
	}
	public void setResultadoEjecucion(String resultadoEjecucion) {
		this.resultadoEjecucion = resultadoEjecucion;
	}
	
	private boolean llamaWebService() {
		
		boolean llamada = false;
		String sComando = this.evento.getComando();
		
		for (int i=0;i<this.evento.getParametros().size();i++)
		{
			sComando = sComando.replaceFirst("-param-", this.evento.getParametros().get(i).getValor());
		}
		
		System.out.println("comando:"+sComando);
		
		//String sUrl = "http://wsValoracion:8080/"+sComando;
		//String sUrl = "http://localhost:8081/"+sComando;
		String sResultado = "";
		
		try
		{
			sResultado=  new RestTemplate().getForObject(sComando, String.class);
			llamada = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("resultado:"+sResultado);
		
		try
		{
			this.setResultadoEjecucion(new RestTemplate().getForObject(sComando, String.class));
			llamada = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/*String endPoint = this.evento.getComando();
		String nameSpace = "";
		String servicio = "";
    	QName tipoParam = new QName("");
    	QName tipoRetorno = new QName("");
    	Object retorno = new Object();
    	
	    Service  service = new Service(); 
	    try {
	    	Call call    = (Call) service.createCall();
	    	call.setTargetEndpointAddress( new java.net.URL(endPoint) );				

		    String paramWS [] = new String [this.indicador.getParametros().size() - 1];
		    int iWS = 0;
		    for (int p = 0; p < this.indicador.getParametros().size(); p++) {
	
		    	if (this.indicador.getParametros().get(p).getTipo().equalsIgnoreCase("STRING")) {
		    		tipoParam = XMLType.XSD_STRING;
		    	} else {
		    		if (this.indicador.getParametros().get(p).getTipo().equalsIgnoreCase("INT")) {
		    			tipoParam = XMLType.XSD_INT;
		    		}
		    	}	
		    	if (this.indicador.getParametros().get(p).getNombre().equalsIgnoreCase("NAMESPACE")) {
		    		nameSpace = this.indicador.getParametros().get(p).getValor();
		    	} else {
		    		if (this.indicador.getParametros().get(p).getNombre().equalsIgnoreCase("SERVICIO")) {
		    			servicio = this.indicador.getParametros().get(p).getValor();
		    		} else {
		    			if (this.indicador.getParametros().get(p).getNombre().equalsIgnoreCase("RETORNO")) {
		    				tipoRetorno = tipoParam;
				    	} else {
			    			call.addParameter(this.indicador.getParametros().get(p).getNombre(), tipoParam, ParameterMode.IN );	
			    			paramWS[iWS] = this.indicador.getParametros().get(p).getValor();
					  		iWS++;
						}
		    		}
		    	}
		    }		
		    call.setOperationName(new QName(nameSpace, servicio));
		    call.setReturnType(tipoRetorno);		    
		    Object objInvocar [] = new Object [paramWS.length];
		    
		    for (int p = 0; p < paramWS.length; p++) {
		    	objInvocar[p] = paramWS[p];
		    }	
		    retorno = call.invoke( objInvocar);
 
		    } 	catch (ServiceException e) 
		    {
		    	llamada = false;
		    } catch (MalformedURLException e) 
		    {	
		    	llamada = false;
		    } catch (RemoteException e) 
		    {
		    	llamada = false;
		    }			    
	 	*/    
	    
		if (llamada)
			this.setResultadoEjecucion(sResultado);
		else
	    	this.setResultadoEjecucion("No se ha podido llamar al Web Service " + this.evento.getNombre());
		
	    return llamada;
	    
	}
	
	public void nuevoParametro(String nombre, String tipo, String valor) {
		Parametro param = new Parametro(nombre, tipo, valor);
		this.evento.getParametros().add(this.evento.getParametros().size(), param);
	}
}
