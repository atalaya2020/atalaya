package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datamodel.Indicador;

@SpringBootApplication
@RestController
public class HolaAtalayaApplication {
	
	@Autowired
	private IndicadorRepositorio repositorio;
	
	public static void main(String[] args) {
		SpringApplication.run(HolaAtalayaApplication.class, args);	
	}

	@GetMapping("/consultar")
	public String consultar(@RequestParam(value = "name", defaultValue = "World") String name) {
		
		repositorio.findByName(name);	
		return String.format(" %s!", "Recuperado el indicador con nombre: "+ name);
	}
	
	@GetMapping("/publicar")
	public String publicar(@RequestParam(value = "name", defaultValue = "Indicador") String name,@RequestParam(value = "fuente", defaultValue = "fuente") String fuente,@RequestParam(value = "comando", defaultValue = "comando") String comando) {
		
		repositorio.save(new Indicador(name, fuente, comando));
		return String.format(" %s!", "Creado el indicador con nombre: " + name);
	}
	
	@GetMapping("/ejecutar")
	public String ejecutar(@RequestParam(value = "name", defaultValue = "World") String name) {
		
		repositorio.findByName(name);	
		return String.format(" %s!", "Ejecutado el indicador con nombre: "+ name);
	}
	
	
	
}