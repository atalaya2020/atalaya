package com.atalaya.launch;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Analisis;
import com.example.demo.Indicador;
import com.example.demo.Criterio;
import com.example.demo.Evento;
import com.example.demo.JsonReader;

import com.atalaya.evaluador.Evaluador;

//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.MongoDatabase;

@RestController
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class LaunchAtalaya {
	public static void main(String[] args) {
		SpringApplication.run(LaunchAtalaya.class, args);

//		MongoClientURI uri = new MongoClientURI("mongodb+srv://atalaya:prueba_00@cluster0.j5twp.mongodb.net/sample_mflix?retryWrites=true&w=majority");
//		MongoClient mongoClient = new MongoClient(uri);
//		MongoDatabase database = mongoClient.getDatabase("test");
	}
	

	@GetMapping("/atalaya")
	public String atalaya() {
		boolean valido = true;		
		int cumplido = -1;
		String salida = "";
		int iError = 0;
		
		String file = "C:\\JSC\\SQL\\EjemploJSON.json";
		
		ArrayList<Analisis> analizador = JsonReader.read(file);
		
		ArrayList<String> mensaje = new ArrayList<String>();
		
		for (int a = 0; a < analizador.size();a++) {
			if(!analizador.get(0).validar()) {
				valido = false;
			}			
		}				

		if(valido) {
			for (int a = 0; a < analizador.size();a++) {
				for (int c = 0; c < analizador.get(a).getCriterios().size(); c++) {								
					Criterio criAnaliza = analizador.get(a).getCriterios().get(c);					
					Evaluador criEvalua = new Evaluador (criAnaliza.getEvaluacion(), analizador.get(a).getIndicadores());					
					cumplido = criEvalua.evaluar();					
					switch (cumplido) {
					case -1:
					// Se han detecado errores en la evaluación
						mensaje.add(iError, "La condición indicada en el criterio " + criAnaliza.getNombre() + " no es correcta.");
						iError++;
				    	for (int e = 0; e < criEvalua.getErrores().size(); e++) {
				    		mensaje.add(iError, criEvalua.getErrores().get(e));
				    		iError++;
				    	}	
				    	break;
					case 0:
					   // La evaluación ha dado como resultado que se no cumple. No deben generarse los eventos asociados al criterio.
						mensaje.add(iError, "El criterio " + criAnaliza.getNombre() + " no cumple la condición. No se generan eventos.");
						iError++;
						break;
					case 1:
					// La evaluación ha dado como resultado que se cumple. Deben generarse los eventos asociados al criterio.
						mensaje.add(iError, "El criterio " + criAnaliza.getNombre() + " cumple la condición. Se generan los eventos siguientes:");
						iError++;
						break;
					}
				}
			}
		} else {
			mensaje.add(iError, "El fichero json no es válido");
			iError++;
		}		

		salida = "";
		for (int e = 0; e < mensaje.size(); e++) {
			salida = salida + "<br>" + mensaje.get(e) ;
		}			

		System.out.println("Mensaje: " + salida.replaceAll("<br>", "\r\n")   );
		return String.format(salida);
	}
}
