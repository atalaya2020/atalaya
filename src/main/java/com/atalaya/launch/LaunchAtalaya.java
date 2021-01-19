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
	
	@GetMapping("/json")
	public String json() {
		boolean valido = true;		
		int cumplido = -1;
		String mensaje = "";
		
		String file = "C:\\JSC\\SQL\\EjemploJSON_2.json";
		
		ArrayList<Analisis> analizador = JsonReader.read(file);
		ArrayList<Indicador> indicadores = analizador.get(0).getIndicadores();
		ArrayList<String> errores = new ArrayList<String>();
		
		for (int a = 0; a < analizador.size();a++) {
			if(!analizador.get(0).validar()) {
				valido = false;
			}			
		}				
		valido = true;
		if(valido) {
			for (int a = 0; a < analizador.size();a++) {
				for (int c = 0; c < analizador.get(0).getCriterios().size(); c++) {
					Criterio criAnaliza = analizador.get(0).getCriterios().get(c);
					Evaluador criEvalua = new Evaluador (criAnaliza.getEvaluacion(), analizador.get(a).getIndicadores());
					cumplido = criEvalua.evaluar();
					switch (cumplido) {
					case -1:
					// Se han detecado errores en la evaluación 
						System.out.println("La condición indicada en el criterio " + criAnaliza.getNombre() + " no es correcta. Se han detectado los siguientes errores: ");
						errores = criEvalua.getErrores();
				    	for (int e = 0; e < errores.size(); e++) {
				    		System.out.println(errores.get(e));
				    	}				    	
				    case 0:
				    // La evaluación ha dado como resultado que se no cumple. No deben generarse los eventos asociados al criterio.
				    	mensaje = "El criterio no cumple la condición";
					case 1:
					// La evaluación ha dado como resultado que se cumple. Deben generarse los eventos asociados al criterio.
						mensaje = "El criterio cumple la condición";
				    }
				}
			}
		} else {
			System.out.println("El JSON no es válido");
		}		
		
		if (cumplido == -1) {
			mensaje = "";
			for (int e = 0; e < errores.size(); e++) {
				mensaje = mensaje + "\r\n" + errores.get(e);
			}			
		} 
		return mensaje;
	}
}
