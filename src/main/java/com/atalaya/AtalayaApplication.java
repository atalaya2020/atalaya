package com.atalaya;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextListener;

import com.atalaya.evaluador.Evaluador;
import com.atalaya.evaluador.AnalisisProxy;
import com.atalaya.evaluador.ParametroProxy;
import com.modelodatos.Analisis;
import com.modelodatos.Parametro;

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
//	public String consultaranalisis(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre) {
	public String consultaranalisis(@RequestParam Map<String, String> params) {
		ArrayList<Parametro> anaParams = new ArrayList<Parametro>(); 
		String nombreAnalisis = null;
		
		Iterator itParams = params.keySet().iterator();
		while (itParams.hasNext()) {
			String clave = itParams.next().toString();
			if (clave.equalsIgnoreCase("nombreAnalisis")) {
				nombreAnalisis = params.get(clave);
			} else {
				Parametro param = new Parametro();
				param.setNombre(clave);
				param.setTipo("String");
				param.setValor(params.get(clave));
				anaParams.add(param);
			}
		}	
		
		if (nombreAnalisis!=null)
		{
			System.out.println("nombre: "+nombreAnalisis);
			Analisis analisis = analisisrepo.findByNombre(nombreAnalisis);
			
			if (analisis!=null)
			{
				AnalisisProxy analisisproxy = new AnalisisProxy(analisis, anaParams) ;
				
				Evaluador evalAnalisis = new Evaluador (analisisproxy);
				StringBuffer mensaje = evalAnalisis.evaluarAnalisis();			
				String resultado = mensaje.toString();
				
				return (resultado);
			}
			else
				return String.format(" %s!", "NO Recuperado el analisis con nombre: "+ nombreAnalisis);
		}
		else
			return String.format(" %s!", "Debe informar el parametro con nombre: nombreAnalisis");
	}		
		
	/*@GetMapping("/publicar")
	public String publicar(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre,@RequestParam(value = "descripcion", defaultValue = "descripcion") String descripcion,@RequestParam(value = "departamento", defaultValue = "departamento") String departamento, @RequestParam(value = "valor", defaultValue = "valor") String valor) {
		
		analisisrepo.save(new Analisis(nombre, descripcion, departamento, valor));
		return String.format(" %s!", "Creado el analisis con nombre: " + nombre);
	}*/
	
}