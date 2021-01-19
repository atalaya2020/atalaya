
package com.example.demo;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.MongoDatabase;

@RestController
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class DemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);


//		MongoClientURI uri = new MongoClientURI("mongodb+srv://atalaya:prueba_00@cluster0.j5twp.mongodb.net/sample_mflix?retryWrites=true&w=majority");



//		MongoClient mongoClient = new MongoClient(uri);
//		MongoDatabase database = mongoClient.getDatabase("test");

	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
	
	@GetMapping("/json")
	public String json(@RequestParam(value = "name", defaultValue = "World") String name) {
		
		String file = "C:\\EjemploJSON.json";
		
		
		ArrayList<Analisis> analizador = JsonReader.read(file);
		ArrayList<Indicador> indicadores = analizador.get(0).getIndicadores();

		if(analizador.get(0).validar()) {
			System.out.println("El JSON es válido");
		}
		
		return String.format(indicadores.get(0).getNombre());
	}

}
