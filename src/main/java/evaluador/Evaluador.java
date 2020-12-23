package evaluador;

import java.util.ArrayList;

public class Evaluador {
	
	private String condicionCompleta;
	ArrayList<CondicionMultiple> evaluacion = new ArrayList<CondicionMultiple>();
	ArrayList<String> errores = new ArrayList<String>();
	private String operadores [] = new String [] {"<=", ">=", "<>", "=", "<", ">"};	
	private String opLogicos [] = new String [] {" AND ", " OR "};
	private char abre = '(';
	private char cierra = ')';
	private Integer indCondicion = 0;
	
	public String getCondicionCompleta() {
		return condicionCompleta;
	}
	public void setCondicionCompleta(String condicionCompleta) {
		this.condicionCompleta = condicionCompleta;
	}	
	
	public Evaluador (String condicionCompleta) {		
		super();		
		this.condicionCompleta = condicionCompleta;	
	}
		
	public boolean evaluar (String condicion)  {
		boolean result = new Boolean(true);
		this.condicionCompleta = condicion;
		result = this.evaluar();
		return result;
	}
	
	public boolean evaluar ()  {
		boolean result = true;
		CondicionMultiple multiple;
		Condicion simple;
		int maxNivel = 0;
		int minNivel = 0;
		
		result = validarCondicion();
		
		if (result) {
			extraerCondiciones();
			
			System.out.println("Mostrando Condiciones"); 
			for (int i = 0; i < evaluacion.size(); i++) { 
				multiple = evaluacion.get(i);
				// Se obtienen los niveles de profundidad mínimo y máximo de las condiciones  
				if (multiple.getNivel() > maxNivel) { maxNivel = multiple.getNivel(); }
				if (multiple.getNivel() < minNivel) { minNivel = multiple.getNivel(); }
				
				System.out.println("Nivel: " + multiple.getNivel() + " " +  multiple.getIdCondicion() + " " + multiple.getTexto() + " Madre: " +  multiple.getMadre() + " Tipo: " + multiple.getTipo()); 
				simple = multiple.getCondicion(); 
				if (simple != null) {
					System.out.println("Condición simple: " + simple.getOperando1() + " " +  simple.getOperador() + " " + simple.getOperando2()); 
				} 
			}
			// Para obtener el resultado de la condición, se evalúan las condiciones simples de mayor a menor nivel de profundidad, teniendo en cuenta las relaciones definidas entre ellas 
			for (int nivel = maxNivel; nivel >= minNivel; nivel--) {
				for (int i = 0; i < evaluacion.size(); i++) { 
					multiple = evaluacion.get(i);
					if (multiple.getNivel() == nivel) {
						multiple.evalua(evaluacion);
						// El resultado de la condición completa es el resultado de la condición del nivel mínimo
						if (nivel == minNivel) {
							result = multiple.getResultado();
						}
					}				
				}			
			}
		}
		else {
			result = false;
		}
		return result;	
		
	}
	
	private void extraerCondiciones () {
	// Recorre el texto de la condición indicada, identificando por niveles el texto comprendido entre paréntesis. Cada trozo de texto comprendido entre paréntesis del mismo nivel, será una condición
		int nivel = 0;
		String txCondicion;		
		Integer indCond;
		
		ArrayList<Integer> abiertos = new ArrayList<Integer>(); // Almacena las posiciones en el texto en la que se encuntra el '(' que abre la condición actual. El índice se corresponderá con el nivel
		
		CondicionMultiple multiple;		
		System.out.println("Condición completa: " + this.condicionCompleta);
		char c;
		for (int i = 0;i<this.condicionCompleta.length();i++) {
			c = this.condicionCompleta.charAt(i);
			if (c == abre) {
				// Por cada '(' se guarda en el array la posición en la que se encuentra en el nivel actual
				abiertos.add(nivel, new Integer(i));
				nivel++;
			} else {
				if (c == cierra) {					
					// Cuando se encuentra un ')' se extre la condición desde el inicial guardado en el array hasta la posición en la que se cierra el nivel
					txCondicion = this.condicionCompleta.substring(abiertos.get(nivel-1).intValue(), i + 1);
					// El texto obtenido puede ser una operación, valor, ente paréntesis o una condación. Si es valor, debe ignorarse, si es condición debe tratarse.
					if (esCondicion(txCondicion)) {
						multiple = new CondicionMultiple();
						multiple.setIdCondicion(indCondicion);
						multiple.setNivel(nivel-1);
						multiple.setTexto(txCondicion);	
						System.out.println("Nivel: " + multiple.getNivel() + " " + multiple.getIdCondicion() + " " + multiple.getTexto() + " Madre: " + multiple.getMadre() + " Tipo: " + multiple.getTipo());
						indCond = indCondicion;
						evaluacion.add(indCond, multiple);		
						indCondicion++;
						infoCondicionMultiple(indCond);																
					}
					abiertos.remove(nivel - 1);	
					nivel--;
				}					
			}				
		}		
	}
	
	private void infoCondicionMultiple(Integer indCond) {
	// Se completa la información de la condición recién creada, creando sus condiciones hijas y relacionándolas con ellas
		CondicionMultiple madre;
		CondicionMultiple hija;
		String[] simples = new String [] {};
		String texto;
		String litOperador = "";
		int iAnd;
		int iOr;		
		
		madre = evaluacion.get(indCond);
		int nivel = madre.getNivel();
		// Las condiciones del nivel superior que no tengan madre serán hijas de esta condición 
		for (int i = 0; i<evaluacion.size();i++) {
			hija = evaluacion.get(i);			
			if (hija.getMadre() == null && hija.getNivel() == (madre.getNivel() + 1) && hija.getIdCondicion() != madre.getIdCondicion()) {
				hija.setMadre(indCond);
				evaluacion.set(i,  hija);
			}			
		}
		// Las partes del texto de la condición que estén entre paréntesis se eliminan del texto de la condición pues, por ser de un nivel superior, ya se habrán tratado.
		texto = eliminarCondicionesInteriores(madre.getTexto());
		// Se obtienen el número de operadores AND y OR que tiene la condición.
		iAnd = contarCaracter(texto, opLogicos[0]);
		iOr = contarCaracter(texto, opLogicos[1]);
		// Si la condición contiene operadores de un sólo tipo, se le asigna el tipo. Si contiene los dos operadores, se tratará aparte		
		if (iAnd > 0 && iOr > 0) {
			analizarCondicion(indCond);			
		} else{
			if ((iAnd > 0 && iOr == 0) || (iAnd == 0 && iOr > 0)) {
				if (iAnd > 0) {
					madre.setTipo("AND");	
					litOperador = opLogicos[0];
				} else {
					if (iOr > 0 ) {
						madre.setTipo("OR");
						litOperador = opLogicos[1];
					} 
				}	
			}
		}
		// Para obtener las condiciones simples de la condición. Si no tiene ninguno de los dos operadores, será una condición simple. Si tiene varios operadores, se utilizará el 
		// operador lógico para separar cada una de las condiciones simples.
		if ((iAnd > 0 && iOr == 0) || (iAnd == 0 && iOr > 0)) {
			simples = texto.substring(1, texto.length()-1).split(litOperador);
		}
		if ((iAnd > 0 && iOr == 0) || (iAnd == 0 && iOr > 0)) 	{
			for (int i = 0; i< simples.length; i++) {			
				hija = new CondicionMultiple();				
				hija.setIdCondicion(indCondicion);
				hija.setNivel(nivel+1);
				hija.setTexto(formatoCondicion(simples[i]));	
				hija.setMadre(madre.getIdCondicion());					 			
				hija.setCondicion(operadoresCondicion(simples[i]));
				evaluacion.add(indCondicion, hija);				
				indCondicion++;			
			}	
		} else {
			if  (iAnd == 0 && iOr == 0) {
				madre.setCondicion(operadoresCondicion(texto));
				evaluacion.set(madre.getIdCondicion(), madre);
			}
		}
	}
	
	private void analizarCondicion(Integer indCond) {
	// La condición compuesta contiene condiciones simples entre las que hay relaciones AND y OR. Al estar al mismo nivel de agrupación, en este caso, se da prioridad a la relación OR,
	// creando una relación OR entre las condiciones que estén así relacionadas entre ellas, que se relacionará como AND con el resto de condiciones.
		
		CondicionMultiple madre;
		CondicionMultiple hija;
		CondicionMultiple nieta;
		String texto;
		String[] partes = new String [] {};
		String[] simples = new String [] {};	
		Integer condHija; 
		
		madre = evaluacion.get(indCond);
		madre.setTipo(opLogicos[0].trim());
		evaluacion.set(indCond, madre);
		// Las partes del texto de la condición que estén entre paréntesis se eliminan del texto de la condición pues, por ser de un nivel superior, ya se habrán tratado.
		texto = eliminarCondicionesInteriores(madre.getTexto());
		// La condición se decompone en condiciones o grupos de condiciones que tendrán una relación AND.
		partes = texto.split(opLogicos[0]);
		for (int i = 0; i< partes.length; i++) {
			hija = new CondicionMultiple();
			hija.setIdCondicion(indCondicion);
			condHija = indCondicion;
			indCondicion++;					
			hija.setNivel(madre.getNivel()+1);
			hija.setTexto(formatoCondicion(partes[i]));		
			hija.setMadre(madre.getIdCondicion());			
		
			// Si la condición contiene un OR, se descompone en las condiciones que la forman para relacionarlas con OR
			if (partes[i].indexOf(opLogicos[1]) >= 0) {
				hija.setTipo("OR");
				simples = partes[i].split(opLogicos[1]);
				for (int j= 0; j< simples.length;j++) {
					nieta = new CondicionMultiple();
					nieta.setIdCondicion(indCondicion);
					nieta.setNivel(madre.getNivel()+2);
					nieta.setTexto(formatoCondicion(simples[j]));	
					nieta.setMadre(condHija);	
					nieta.setCondicion(operadoresCondicion(simples[j]));					
					evaluacion.add(indCondicion, nieta);
					indCondicion++;							
				}
			} else {

				hija.setCondicion(operadoresCondicion(partes[0]));				
			}			
			evaluacion.add(condHija, hija);			
		}	
	}
	
	private Condicion operadoresCondicion(String txCondicion) {
	// A partir de un texto que contiene una condición simple, obtiene los operandos y operador de y crea el objeto Condición que devuelve como resultado
		Condicion nuevaCond = null;
		int i = 0;
		String operador = "";
		
		// Obtiene el operador utilizado en la condición
		i = 0;
		while (operador != "" && i < operadores.length) {
			if (txCondicion.indexOf(operadores[i]) >= 0) {
				operador = operadores[i];
			}
			i++;
		}	
		// Si se ha encontrado el operador, divide la condición en tres partes: operando1, operador y operando2.
		if (operador != "") {
			String[] minimas = txCondicion.split(operador);
			if (minimas.length == 3) {
				nuevaCond = new Condicion();				
				nuevaCond.setOperando1(minimas[0].trim());
				nuevaCond.setOperando2(minimas[1].trim());					
				nuevaCond.setOperador(operador);
				return nuevaCond;
			} else {
				sumarError ("La condición es errónea. Falta un operador: " + txCondicion); 
			}				
		}else {
			sumarError ("La condición es errónea. No se ha definido ningún operador: " + txCondicion); 
		}
		return nuevaCond;
	}
	
	private boolean validarCondicion() {
		// Comprueba la sintaxis de la condición
		boolean result = new Boolean(true);
		this.condicionCompleta = formatoCondicion(this.condicionCompleta);	
		int iAbre = contarCaracter(this.condicionCompleta, "(");
		int iCierra = contarCaracter(this.condicionCompleta, ")");
				
		// En la condición, el número de "(" debe ser igual al de ")".
		if (iAbre != iCierra) 	{
			result = false;	
			if (iAbre > iCierra) {
				sumarError("Falta paréntesis \\)");
			} else {
				sumarError("Falta paréntesis \\(");
			}
				
		}		
		return result;
	}
	
	private String formatoCondicion(String cadena) {
		String cadenaFormato;		
		cadenaFormato = cadena.trim();
		// La condición completa debe estar entre paréntesis, así se asegura que el nivel mínimo tenga una sola condición, que es la completa.. 
		cadenaFormato = "(" + cadenaFormato + ")";	
		// Se añade un espacio delante y detrás de cada uno de los operadores.
		for (int i = 0; i< operadores.length;i++) {
			cadenaFormato = cadenaFormato.replaceAll(operadores[i].toLowerCase(), " " + operadores[i] + " ");			
		}		
		// Se elimina el espacio que se habría añadido en el bucle anterioren los operadores que se definen con dos caracteres
		cadenaFormato = cadenaFormato.replaceAll("< =", "<=");
		cadenaFormato = cadenaFormato.replaceAll("<  =", "<=");	
		cadenaFormato = cadenaFormato.replaceAll("> =", ">=");		
		cadenaFormato = cadenaFormato.replaceAll(">  =", ">=");		
		cadenaFormato = cadenaFormato.replaceAll("< >", "<>");
		cadenaFormato = cadenaFormato.replaceAll("<  >", "<>");
		// Convierte a mayúsculas todos los operadores AND y OR
		for (int i = 0; i< opLogicos.length;i++) {
			cadenaFormato = cadenaFormato.replaceAll(opLogicos[i].toLowerCase(), opLogicos[i]);
		}		
		// Se eliminan los espacios de más		
		while (contarCaracter(cadenaFormato, "  ") > 0) {		
			cadenaFormato = cadenaFormato.replaceAll("  ", " ");
		}
		while (contarCaracter(cadenaFormato, "( ") > 0) {		
			cadenaFormato = cadenaFormato.replaceAll("( ", "(");
		}
		while (contarCaracter(cadenaFormato, " )") > 0) {		
			cadenaFormato = cadenaFormato.replaceAll(" )", ")");
		}
		return cadenaFormato;
	}
	
	private static int contarCaracter(String cadena, String car) {
        int pos = 0;
        int cont = 0;
        pos = cadena.indexOf(car);
        while (pos != -1) {
            cont++;                       
            pos = cadena.indexOf(car, pos + 1);
        }
        return cont;
	}
	
	private String eliminarCondicionesInteriores(String texto) {
	// Elimina del texto de entrada las partes de texto contenidas entre paréntesis, manteniendo los paréntesis de inicio y fin del texto.
		String textoFinal;
		textoFinal = texto;
		int iAbre = 0;
		int i;
		boolean cambio;		

		char c;
		cambio = true;
		while (cambio == true) {
			cambio = false;
			i = 1;
			while (i<(textoFinal.length() - 1) && cambio == false) {				
				c = textoFinal.charAt(i);
				if (c == abre) {
					iAbre = i;
				} else {
					if (c == cierra) {		
						textoFinal = textoFinal.replace(textoFinal.substring(iAbre, i + 1), " ");
						cambio = true;				
					}					
				}
				i++;
			}	
		}
		while (contarCaracter(textoFinal, "  ") > 0) {		
			textoFinal = textoFinal.replaceAll("  ", " ");
		}	
		// Se corrige la repetición de operadores lógicos resultante de le eliminación del texto entre paréntesis que había entre ellos. 
		textoFinal = textoFinal.replaceAll(" AND AND ", " AND ");
		textoFinal = textoFinal.replaceAll(" OR OR ", " OR ");
		textoFinal = textoFinal.replaceAll(" AND \\)", "\\)");
		textoFinal = textoFinal.replaceAll(" OR \\)", "\\)");	
		textoFinal = textoFinal.replaceAll("\\( AND ", "\\(");
		textoFinal = textoFinal.replaceAll("\\( OR ", "\\(");	
		if (textoFinal.indexOf(" AND OR ") > 0 || textoFinal.indexOf(" OR AND ") > 0) {
			sumarError("La relación lógica en la condición no es correcta: " + texto);
		}
		return textoFinal;
	}	 
	
	private boolean esCondicion(String texto) {
	// Identidica si es una condición o un valor, para saber cómo debe tratarse. Si el texto contiene algún operador, será una condición.
		boolean esCond = false;
		for (int i = 0; i < operadores.length; i++) {
			if (texto.indexOf(operadores[i]) >= 0) {
				esCond = true;
			}
		}			
		return esCond;
	}
	
	private void sumarError(String txError) {
		int i = errores.size();
		errores.add(i, txError);  ;
	}
}
 
