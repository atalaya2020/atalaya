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
	

}

