package com.atalaya;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;

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
				
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Date fecha = new Date();
		System.out.println(fecha.toString() +   " -- Ejecutando análisis de: " + anaParams.get(0).getValor());
		
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
	
	@GetMapping("/cargadatos")
	public String cargarDatosAtalaya(@RequestParam Map<String, String> params) {
		
		String [] nombres = new String [] {"ALBERTO", "ANA", "BLAS", "BELEN", "CARLOS", "CARMEN", "DIEGO", "DIANA", "EDUARDO", "ELVIRA", "FRANCISCO", "FATIMA", 
				"GABRIEL", "GLORIA", "HUGO", "HELENA", "IGNACIO", "ISABEL", "JAIME", "JIMENA", "LUIS", "LAURA", "MANUEL", "MARIA", "NESTOR", "NOELIA", "OSCAR", "OLGA", 
				"PABLO", "PALOMA", "RAUL", "RAQUEL", "SERGIO", "SUSANA", "TELMO", "TERESA", "ULISES", "URSULA", "VICTOR", "VERONICA", "YAGO", "YOLANDA", "MIGUEL", "JORGE", "GODOFREDO", 
				"DIEGO", "JOAQUIN", "RAMON", "JAVIER", "ROSA", "MARTA", "ALICIA", "JULIA", "EVA", "PILAR", "SILVIA", "ANDRES", "BEATRIZ", "PATRICIA", "NATALIA"};
			
		String [] apellidos1 = new String [] {"ALVAREZ", "BENITEZ", "CORONAS", "DIAZ", "ESCOBAR", "FERNANDEZ", "GARCIA", "HERAS", "IBANEZ", "JUAREZ", "LOPEZ", 
			"MUNOZ", "NUNEZ", "OJEDA", "PONTE", "RODRIGUEZ", "SANCHEZ", "TELLEZ", "UCEDA", "VAZQUEZ", "YANGUAS", "ZARZALEJO", "RUBIO", "GONZALEZ", "CABALLERO", "CASTRO", "REYES", 
			"ASENSIO", "SILVA", "MATA", "VILLANUEVA", "CUESTA", "ALCANTARA", "RUIZ", "ABELLAN", "DELGADO", "CORREDOR", "NADAL", "RIVERO", "FUENTES", "CASTAÑO", "FIGUEROA", "VALLEJO", "VERDU"};
		
		String [] apellidos2 = new String [] {"ALONSO", "BLANCO", "CALVO", "DIEZ", "ESCUDERO", "FLORES", "GOMEZ", "HERNANDEZ", "HEREDIA", "ISLA", "JIMENEZ", "LEDESMA", 
			"MARTINEZ", "NAVAS", "OLIVA", "PEREZ", "RIESCO", "SOLIS", "TAPIAS", "URRA", "VELASCO", "YANEZ", "ZURITA", "MORENO", "GUTIERREZ", "PASO", "ROS", 
			"SAMPER", "RAMOS", "COSTA", "CRESPO", "CAMARA", "ARIAS", "MATEOS", "CAMPOS", "SORDO", "CARRERAS", "NOVOA", "MOLINA", "ROMERO", "RIVAS", "MONTERO", "SAURA", "TORRES"};
			
		String [] provincias = new String [] {"CORUNA", "LUGO", "ORENSE", "PONTEVEDRA", "ASTURIAS", "CANTABRIA", "VIZCAYA", "GUIPUZCOA", "ALAVA", "NAVARRA", 
					"HUESCA", "ZARAGOZA", "TERUEL", "LERIDA", "GERONA", "BARCELONA", "TARRAGONA", "LEON", "PALENCIA", "BURGOS", "RIOJA", "ZAMORA", "SORIA", "SALAMANCA", 
					"VALLADOLID", "AVILA", "SEGOVIA", "MADRID", "CASTELLON", "VALENCIA", "ALICANTE", "BALEARES", "MURCIA", "CACERES", "BADAJOZ", "GUADALAJARA", "TOLEDO", "CUENCA", 
					"CIUDAD REAL", "ALBACETE", "JAEN", "CORDOBA", "SEVILLA", "HUELVA", "CADIZ", "MALAGA", "ALMERIA", "GRANADA", "TENERIFE", "LAS PALMAS", "CEUTA", "MELILLA"};
		
		String[] asignaturas = new String [] {"ATALAYA", "DOCKER", "HAPROXY", "CLOUD", "MYSQL", "MONGO", "SPRING_BOOT", "JAVA"};
		
		int identificador = 0;
		int registros = 0;
		int numRegistros = 30;
		int insertados = 0;
		String nombre;
		String apellidos;
		String provincia;
		String asignatura;
		int iniNom = 0;		
		int iniApe1 = 0;
		int iniApe2 = 0;
		int n = 0;
		int a1 = 0;
		int a2 = 0;		
		PreparedStatement maxId = null;
		PreparedStatement nuevoAlumno = null;
		PreparedStatement nuevaNota = null;
		String insAlumno = "INSERT INTO ALUMNADO (ID, NOMBRE, APELLIDOS, PROVINCIA) VALUES (?, ?, ?, ?)";
		String insNota = "INSERT INTO CALIFICACIONES (ID,ASIGNATURA,CALIFICACION) VALUES (?, ?, ?);";
		
		Iterator itParams = params.keySet().iterator();
		while (itParams.hasNext()) {
			String clave = itParams.next().toString();
			if (clave.equalsIgnoreCase("alumnos")) {
				numRegistros = new Integer(params.get(clave)) ;
			} 
		}			
				
		Connection conexion;
		System.out.println(nombres.length + " nombres cargados");
		System.out.println(apellidos1.length + " primeros apellidos cargados");
		System.out.println(apellidos2.length + " segundos apellidos cargados");
		System.out.println(provincias.length + " provincias cargadas");		
		

		try {
			conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/alumnadodb?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true",	"root", "atalaya");
			maxId = conexion.prepareStatement("SELECT ID, NOMBRE, APELLIDOS, PROVINCIA FROM ALUMNADO WHERE ID = (SELECT MAX(ID) FROM ALUMNADO)");

			
			ResultSet rs = maxId.executeQuery();
			if (rs.next()) {
				identificador = rs.getInt("ID");
				nombre = rs.getString("NOMBRE");
				String ultApellidos[] = rs.getString("APELLIDOS").split(" ");
				for (int i = 0; i < nombres.length; i++) {
					if (nombres[i].equals(nombre)) {
						iniNom = i;
						break;
					}
				}
				for (int i = 0; i < apellidos1.length; i++) {
					if (apellidos1[i].equals(ultApellidos[0])) {
						iniApe1 = i ;
						break;
					}
				}
				if (ultApellidos.length == 2) {
					for (int i = 0; i < apellidos2.length; i++) {
						if (apellidos2[i].equals(ultApellidos[1])) {
							iniApe2 = i;
							break;
						}
					}	
				} else {
					iniNom = 0;
					iniApe1 = 0;
					iniApe2 = 0;
				}				
			}	
			System.out.println("Antes iniNom: " + iniNom + ", iniApe1: " + iniApe1 + ", iniApe2: " + iniApe2);
			if (!(iniNom == 0 && iniApe1 == 0 && iniApe2 == 0)) {
				if (iniApe2 == (apellidos2.length - 1)) {
					iniApe2 = 0;
					iniApe1++;
					if (iniApe1 == (apellidos1.length - 1)) {
						iniApe1 = 0;
						iniApe2 = 0;
						iniNom++;
						if (iniNom == (nombres.length - 1)) {
							registros = numRegistros + 10;
						} else {				
							iniNom++;				
						}	
					} else {				
						iniApe1++;				
					}				
				} else {				
					iniApe2 = iniApe2 + 1;					
				}				
			}
			
			System.out.println("Después iniNom: " + iniNom + ", iniApe1: " + iniApe1 + ", iniApe2: " + iniApe2);
			System.out.println("Identificador = " + identificador);			

			for (n = iniNom; n < nombres.length && registros < numRegistros; n++) {
				for (a1 = iniApe1; a1 < apellidos1.length && registros < numRegistros; a1++ ) {
					for (a2 = iniApe2; a2 < apellidos2.length && registros < numRegistros; a2++ ) {
						identificador++;
						nombre = nombres[n];
						apellidos = apellidos1[a1].trim() + " " + apellidos2[a2].trim();
						provincia = provincias[(int) Math.floor(Math.random() * provincias.length )];
						nuevoAlumno = conexion.prepareStatement(insAlumno);
						nuevoAlumno.setInt(1, identificador);
						nuevoAlumno.setString(2, nombre);
						nuevoAlumno.setString(3, apellidos);
						nuevoAlumno.setString(4, provincia);						
						nuevoAlumno.execute();						
						
						for (int a = 0; a < asignaturas.length; a++) {
							nuevaNota = conexion.prepareStatement(insNota);
							nuevaNota.setInt(1, identificador);
							nuevaNota.setString(2, asignaturas[a]);						
							nuevaNota.setInt(3, (int) Math.floor(Math.random() * 11));				
							nuevaNota.execute();
							nuevaNota.close();
						}			
						nuevoAlumno.close();	
						registros++;
						insertados++;
						if (n == nombres.length && a1 == apellidos1.length && a2 == apellidos2.length ) {
							registros = numRegistros + 10;
						}
					}
					iniApe2 = 0;
				}				
				iniApe1 = 0;
			}			
		
			try {
				conexion.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}		
		} catch (SQLException e1) {
		
			e1.printStackTrace();
		} 
		
		return "Insertados " + insertados + " alumnos";
	}	
	
}