package com.atalaya.evaluador;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.modelodatos.Analisis;
import com.modelodatos.Criterio;
import com.modelodatos.Indicador;
import com.modelodatos.Parametro;
<<<<<<< HEAD

public class JsonReader {

	@SuppressWarnings("unchecked")
	public static ArrayList<Analisis> read(Object objIn) 
	{
		ArrayList<Analisis> analizador = new ArrayList<Analisis>();
		JSONParser parser = new JSONParser();	
		
		try 
		{
			Object obj = null;
			
			if (objIn.getClass().equals(String.class))
			{
				obj = parser.parse(new FileReader((String)objIn));
			}
			else
				obj = objIn;
			
			// JSON object para mapear el documento
			JSONObject jsonObject = (JSONObject) obj;

			// JSON array de reglas
			JSONArray conjAnalisis = (JSONArray) jsonObject.get("Analizador");

			Iterator<JSONObject> iteratorAn = conjAnalisis.iterator();
			while (iteratorAn.hasNext()) {
				//Analisis a analisis

				JSONObject analisis = (JSONObject)iteratorAn.next();
				String nombreAn = (String) analisis.get("Nombre");	
				String descAn = (String)analisis.get("Descripcion");

				JSONArray conjCriterios = (JSONArray)analisis.get("Criterios");
				Iterator<JSONObject> iteratorCrit = conjCriterios.iterator();
				ArrayList<Criterio> criterios = new ArrayList<Criterio>();

				while(iteratorCrit.hasNext()) {
					//criterio a criterio

					JSONObject criterio = (JSONObject)iteratorCrit.next();
					String nombreCrit = (String) criterio.get("Nombre");
					String descCrit = (String)criterio.get("Descripcion");					
					String evalCrit = (String)criterio.get("Evaluacion");			
					String tipoResCrit = (String)criterio.get("TipoResultado");			
					Object resCrit = (Object)criterio.get("Resultado");

					criterios.add(new Criterio(nombreCrit, descCrit, evalCrit, tipoResCrit, resCrit));

				}

				JSONArray conjIndicadores = (JSONArray)analisis.get("Indicadores");
				Iterator<JSONObject> iterator3 = conjIndicadores.iterator();
				ArrayList<Indicador> indicadores = new ArrayList<Indicador>();

				while(iterator3.hasNext()) {
					//indicador a indicador

					JSONObject indicador = (JSONObject)iterator3.next();
					String nombreInd = (String) indicador.get("Nombre");
					String descInd = (String)indicador.get("Descripcion");
					String fuenteInd = (String)indicador.get("Fuente");
					String tipoInd = (String)indicador.get("Tipo");	
					String comandInd = (String)indicador.get("Comando");	

					JSONArray conjParam = (JSONArray)indicador.get("Parametros");
					Iterator<JSONObject> iteratorParametros = conjParam.iterator();
					ArrayList<Parametro> parametros = new ArrayList<Parametro>();

					while(iteratorParametros.hasNext()) {

						JSONObject parametro = (JSONObject)iteratorParametros.next();
						String nombreParam = (String) parametro.get("Nombre");
						String tipoParam = (String) parametro.get("Tipo");
						String valorParam = (String)parametro.get("Valor");

						parametros.add(new Parametro(nombreParam, tipoParam, valorParam));
					}

					JSONArray listRes = (JSONArray)indicador.get("Resultado");
					String[] resInd = new String[100];
					for(int i = 0; i< listRes.size(); i++) {
						resInd[i] = listRes.get(i).toString();
					}

					boolean flagInd = false;

					indicadores.add(new Indicador(nombreInd, descInd, fuenteInd, tipoInd, comandInd, parametros, resInd));
				}

				analizador.add(new Analisis(nombreAn, descAn, indicadores, criterios));

			}

		} catch (ParseException pe) {
			pe.printStackTrace();
			
			// el formato del JSON es incorrecto
		}
		catch(IOException e) {
			e.printStackTrace();
			// Fallo en la lectura del fichero
		}
		catch(Exception e) {
			
		}

		return analizador;

	}
	
=======
import com.modelodatos.Evento;

public class JsonReader {

	@SuppressWarnings("unchecked")
	public static ArrayList<Analisis> read(Object objIn) 
	{

		ArrayList<Analisis> analizador = new ArrayList<Analisis>();
		JSONParser parser = new JSONParser();	

		try 
		{
			Object obj = null;
			
			if (objIn.getClass().equals(String.class))
			{
				obj = parser.parse(new FileReader((String)objIn));
			}
			else
				obj = objIn;
			
			// JSON object para mapear el documento
			JSONObject jsonObject = (JSONObject) obj;
			// JSON array de reglas
//			JSONArray conjAnalisis = (JSONArray) jsonObject.get("Analizador");
			
//			Iterator<JSONObject> iteratorAn = conjAnalisis.iterator();
//			while (iteratorAn.hasNext()) {
				//Analisis a analisis
				
//				JSONObject analisis = (JSONObject)iteratorAn.next();
//				String nombreAn = (String) analisis.get("nombre");	
//				String descAn = (String)analisis.get("descripcion");

//				JSONArray conjCriterios = (JSONArray)analisis.get("criterios");
//				Iterator<JSONObject> iteratorCrit = conjCriterios.iterator();
//				ArrayList<Criterio> criterios = new ArrayList<Criterio>();
			
				Iterator<String> itObjeto = jsonObject.keySet().iterator();
				
				String [] clavesAnalisis = clavesJSONObject(jsonObject);			
				
				String nombreAn = (String) jsonObject.get(nombreClaveJSON(clavesAnalisis, "nombre"));	
				String descAn = (String) jsonObject.get(nombreClaveJSON(clavesAnalisis, "descripcion"));	
				
				JSONArray conjCriterios = (JSONArray)jsonObject.get(nombreClaveJSON(clavesAnalisis, "criterios"));
				Iterator<JSONObject> iteratorCrit = conjCriterios.iterator();				
				ArrayList<Criterio> criterios = new ArrayList<Criterio>();	
		
				while(iteratorCrit.hasNext()) {
					//criterio a criterio

					JSONObject criterio = iteratorCrit.next();
					String[] clavesCriterio = clavesJSONObject(criterio);
					
					String nombreCrit = (String) criterio.get(nombreClaveJSON(clavesCriterio, "nombre"));
					String descCrit = (String)criterio.get(nombreClaveJSON(clavesCriterio, "descripcion"));					
					String evalCrit = (String)criterio.get(nombreClaveJSON(clavesCriterio, "evaluacion"));			
					String tipoResCrit = (String)criterio.get(nombreClaveJSON(clavesCriterio, "tipoResultado"));		
					Object resCrit = (Object)criterio.get(nombreClaveJSON(clavesCriterio, "resultado"));
					String eventoCrit = (String)criterio.get(nombreClaveJSON(clavesCriterio, "evento"));
					
					String[] resEventos = new String[1];
					resEventos[0] = eventoCrit;
//					JSONArray listEventos = (JSONArray)criterio.get("evento");
//					String[] resEventos = new String[listEventos.size()];
//					for(int i = 0; i< listEventos.size(); i++) {
//						resEventos[i] = listEventos.get(i).toString();
//					}
					criterios.add(new Criterio(nombreCrit, descCrit, evalCrit, tipoResCrit, resCrit, resEventos));
				}
				
				
//				JSONArray conjIndicadores = (JSONArray)analisis.get("indicadores");
//				Iterator<JSONObject> iterator3 = conjIndicadores.iterator();
//				ArrayList<Indicador> indicadores = new ArrayList<Indicador>();
				
				JSONArray conjIndicadores = (JSONArray)jsonObject.get(nombreClaveJSON(clavesAnalisis, "indicadores"));
				Iterator<JSONObject> iterator3 = conjIndicadores.iterator();
				ArrayList<Indicador> indicadores = new ArrayList<Indicador>();
				
				while(iterator3.hasNext()) {
					//indicador a indicador

					JSONObject indicador = iterator3.next();
					String[] clavesIndicador = clavesJSONObject(indicador);
					String nombreInd = (String) indicador.get(nombreClaveJSON(clavesIndicador, "nombre"));
					String descInd = (String)indicador.get(nombreClaveJSON(clavesIndicador, "descripcion"));
					String fuenteInd = (String)indicador.get(nombreClaveJSON(clavesIndicador, "fuente"));
					String tipoInd = (String)indicador.get(nombreClaveJSON(clavesIndicador, "tipo"));	
					String comandInd = (String)indicador.get(nombreClaveJSON(clavesIndicador, "comando"));	
					
					JSONArray conjParam = (JSONArray)indicador.get(nombreClaveJSON(clavesIndicador, "parametros"));
					Iterator<JSONObject> iteratorParametros = conjParam.iterator();
					ArrayList<Parametro> parametros = new ArrayList<Parametro>();

					while(iteratorParametros.hasNext()) {

						JSONObject parametro = iteratorParametros.next();
						String[] clavesParamInd = clavesJSONObject(parametro);
						String nombreParam = (String) parametro.get(nombreClaveJSON(clavesParamInd, "nombre"));
						String tipoParam = (String) parametro.get(nombreClaveJSON(clavesParamInd, "tipo"));
						String valorParam = (String)parametro.get(nombreClaveJSON(clavesParamInd, "valor"));						
						
						parametros.add(new Parametro(nombreParam, tipoParam, valorParam));
					}

					JSONArray listRes = (JSONArray)indicador.get(nombreClaveJSON(clavesIndicador, "resultado"));
					String[] resInd = new String[listRes.size()];
					for(int i = 0; i< listRes.size(); i++) {
						resInd[i] = listRes.get(i).toString();
					}

					boolean flagInd = false;

					indicadores.add(new Indicador(nombreInd, descInd, fuenteInd, tipoInd, comandInd, parametros, resInd));
				}
				
//				JSONArray conjEventos = (JSONArray)analisis.get("eventos");
//				Iterator<JSONObject> iterator4 = conjEventos.iterator();
//				ArrayList<Indicador> eventos = new ArrayList<Indicador>();
				
				JSONArray conjEventos = (JSONArray)jsonObject.get(nombreClaveJSON(clavesAnalisis, "eventos"));
				Iterator<JSONObject> iterator4 = conjEventos.iterator();
				ArrayList<Evento> eventos = new ArrayList<Evento>();
				
				while(iterator4.hasNext()) {
					//Evento a evento
					
					JSONObject evento = iterator4.next();
					String[] clavesEvento = clavesJSONObject(evento);
					String nombreEvent = (String) evento.get(nombreClaveJSON(clavesEvento, "nombre"));
					String descEvent = (String)evento.get(nombreClaveJSON(clavesEvento, "descripcion"));
					String fuenteEvent = (String)evento.get(nombreClaveJSON(clavesEvento, "fuente"));
					String tipoEvent = (String)evento.get(nombreClaveJSON(clavesEvento, "tipo"));
					String comandEvent = (String)evento.get(nombreClaveJSON(clavesEvento, "comando"));

					JSONArray conjParamEvent = (JSONArray)evento.get(nombreClaveJSON(clavesEvento, "parametros"));
					Iterator<JSONObject> iteratorEventos = conjParamEvent.iterator();
					ArrayList<Parametro> paramEventos = new ArrayList<Parametro>();
					while(iteratorEventos.hasNext()) {
						JSONObject parametro = iteratorEventos.next();
						String[] clavesParamEve = clavesJSONObject(parametro);
						String nombreParam = (String) parametro.get(nombreClaveJSON(clavesParamEve, "nombre"));
						String tipoParam = (String) parametro.get(nombreClaveJSON(clavesParamEve,"tipo"));
						String valorParam = (String) parametro.get(nombreClaveJSON(clavesParamEve,"valor"));		
						
						paramEventos.add(new Parametro(nombreParam, tipoParam, valorParam));
					}
					
					JSONArray listEvent = (JSONArray)evento.get(nombreClaveJSON(clavesEvento, "resultado"));
					String[] resEvent = new String[listEvent.size()];
					for(int i = 0; i< listEvent.size(); i++) {
						resEvent[i] = listEvent.get(i).toString();
					}					
					boolean flagEvent = false;

					eventos.add(new Evento(nombreEvent, descEvent, fuenteEvent, tipoEvent, comandEvent, paramEventos, resEvent));
				}
				analizador.add(new Analisis(nombreAn, descAn, indicadores, criterios, eventos));

			//}

		} catch (ParseException pe) {
			pe.printStackTrace();
			
			// el formato del JSON es incorrecto
		}
		catch(IOException e) {
			e.printStackTrace();
			// Fallo en la lectura del fichero
		}
		catch(Exception e) {
			
		}
		return analizador;

	}
	
	private static String nombreClaveJSON(String[] claves, String valor) {
		String clave = "";
		int i = 0;
		while(i < claves.length && clave.equals("")) {			
			if (claves[i].equalsIgnoreCase(valor)) {
				clave = claves[i];
			}
			i++;
		}
		return clave;
	}
	
	private static String[] clavesJSONObject(JSONObject objeto) {
	String cadena = "";		
	String [] claves = new String [] {};
		Iterator<String> itObjeto = objeto.keySet().iterator();			
		while(itObjeto.hasNext()) {
			cadena = cadena + "cadenaSplit" + itObjeto.next().trim();			
		}
		claves = cadena.split("cadenaSplit");
		return claves;
	}
>>>>>>> refs/remotes/origin/main

}

