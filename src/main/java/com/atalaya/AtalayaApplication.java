package com.atalaya;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atalaya.evaluador.Evaluador;
import com.modelodatos.Analisis;
import com.modelodatos.Criterio;

@SpringBootApplication
@RestController
public class AtalayaApplication {
	
	@Autowired
	private AnalisisMongoRepository analisisrepo;
	
	public static void main(String[] args) {
		SpringApplication.run(AtalayaApplication.class, args);	
	}
	
	@GetMapping("/quiensoy")
	public String quiensoy() {
		
		return String.format(" %s!", "Soy un atalaya cualquiera");
	}
	
	
	@GetMapping("/ejecutaranalisis")
	public String consultaranalisis(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre) {
		
		System.out.print("nombre:"+nombre);
		int cumplido = -1;
		Analisis analisis = analisisrepo.findByNombre(nombre);
		if (analisis!=null)
		{	
			ArrayList<String> mensaje = new ArrayList<String>();
			int iError = 0;
			
			for (int c = 0; c < analisis.getCriterios().size(); c++) {								
				Criterio criAnaliza = analisis.getCriterios().get(c);					
				Evaluador criEvalua = new Evaluador (criAnaliza.getEvaluacion(), analisis.getIndicadores());					
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
					analisis.eventosCriterio(criEvalua.getEventos());
					iError++;
					break;
				}
			}
			
			
			
			return ("Ejecutado el analisis con nombre: "+ nombre+" con resultado "+cumplido);
		}
		
		else
			return String.format(" %s!", "NO Recuperado el analisis con nombre: "+ nombre);
	}
	
	/*@GetMapping("/publicar")
	public String publicar(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre,@RequestParam(value = "descripcion", defaultValue = "descripcion") String descripcion,@RequestParam(value = "departamento", defaultValue = "departamento") String departamento, @RequestParam(value = "valor", defaultValue = "valor") String valor) {
		
		analisisrepo.save(new Analisis(nombre, descripcion, departamento, valor));
		return String.format(" %s!", "Creado el analisis con nombre: " + nombre);
	}*/
	
}