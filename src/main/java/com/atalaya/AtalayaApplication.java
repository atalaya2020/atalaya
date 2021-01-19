package com.atalaya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.modelodatos.Analisis;
import com.modelodatos.Indicador;

@SpringBootApplication
@RestController
public class AtalayaApplication {
	
	@Autowired
	private IndicadorRepository indicadorrepo;
	private AnalisisRepository analisisrepo;
	
	public static void main(String[] args) {
		SpringApplication.run(AtalayaApplication.class, args);	
	}

	@GetMapping("/consultarindicador")
	public String consultarindicador(@RequestParam(value = "nombre", defaultValue = "porld") String nombre) {
		
		indicadorrepo.findByNombre(nombre);	
		return String.format(" %s!", "Recuperado el indicador con nombre: "+ nombre);
	}
	
	@GetMapping("/quiensoy")
	public String quiensoy() {
		
		return String.format(" %s!", "Soy el interface que llama a otros ms");
	}
	
	@GetMapping("/consultaranalisis")
	public String consultar(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre) {
		
		analisisrepo.findByNombre(nombre);	
		return String.format(" %s!", "Recuperado el analisis con nombre: "+ nombre);
	}
	
	@GetMapping("/publicar")
	public String publicar(@RequestParam(value = "nombre", defaultValue = "prueba") String nombre,@RequestParam(value = "descripcion", defaultValue = "descripcion") String descripcion,@RequestParam(value = "departamento", defaultValue = "departamento") String departamento, @RequestParam(value = "valor", defaultValue = "valor") String valor) {
		
		analisisrepo.save(new Analisis(nombre, descripcion, departamento, valor));
		return String.format(" %s!", "Creado el analisis con nombre: " + nombre);
	}
	
	@GetMapping("/ejecutar")
	public String ejecutar(@RequestParam(value = "accion", defaultValue = "World") String accion) {
		
		String sResultado = null;
		
		if (accion.equals("llamarotroms"))
		{
			String sUrl = "http://localhost:8081/consultarindicador?nombre=Indicador1";
			String obj="";
			try
			{
				obj=  new RestTemplate().getForObject(sUrl, String.class);
			}
			catch (Exception e)
			{
				sUrl = "http://atalayareceptor:8081/consultarindicador?nombre=Indicador1";
				try
				{
					obj=  new RestTemplate().getForObject(sUrl, String.class);
				}
				catch(Exception e1)
				{
					sUrl = "http://127.0.0.1:8081/consultarindicador?nombre=Indicador1";
					try
					{
						obj=  new RestTemplate().getForObject(sUrl, String.class);
					}
					catch(Exception e2)
					{
						sResultado = "NO lo consigo";
					}
				}
			}
			
			sResultado = "Ejecutada la accion: "+ obj + " | ultima url "+sUrl;
		}
		
		return sResultado;
	}
	
	
	
}
