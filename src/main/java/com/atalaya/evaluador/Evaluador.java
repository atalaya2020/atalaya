package com.atalaya.evaluador;

import com.example.demo.Indicador;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// Clase que evalúa una condición compleja. Puede devolver tres posibles resultados:
//1. Se ha evaluado la condición y se cumple (true)
//0. Se ha evaluado la condició y no se cumple (false)
//-1. Se han detrectado errores en la condición y no se ha evaluado.
public class Evaluador {
	
	private String condicionCompleta;
	private ArrayList<CondicionMultiple> evaluacion = new ArrayList<CondicionMultiple>();
	private ArrayList<String> errores = new ArrayList<String>();
	private ArrayList<Indicador> indicadores = new ArrayList<Indicador>();
	private String operadores [] = new String [] {"<=", ">=", "<>", "=", "<", ">"};	
	private String opLogicos [] = new String [] {" AND ", " OR "};
	private char abre = '(';
	private char cierra = ')';
	private Integer indCondicion = 0;
	
	private Comunes constantes; 
	
	public ArrayList<String> getErrores() {
		return errores;
	}	 

	public String getCondicionCompleta() {
		return condicionCompleta;
	}
	public void setCondicionCompleta(String condicionCompleta) {
		this.condicionCompleta = condicionCompleta;
	}	
	
	public void setIndicadores(ArrayList<Indicador> indicadores) {
		this.indicadores = indicadores;
	}
	
	public Evaluador (String condicionCompleta, ArrayList<Indicador> indicadores) {		
		super();		
		this.condicionCompleta = condicionCompleta;	
		this.indicadores = indicadores;
	}
		
	public int evaluar (String condicion, ArrayList<Indicador> indicadores)  {
		int result = 0;
		this.condicionCompleta = condicion;		
		this.indicadores = indicadores;
		result = this.evaluar();
		return result;
	}
	
	public int evaluar ()  {
		int result = 0;
		boolean valido = true;
		CondicionMultiple multiple;
		Condicion simple;
		int maxNivel = 0;
		int minNivel = 0;		
		
		evaluacion.clear();
		errores.clear();
		
		if (condicionCompleta == "" || condicionCompleta == null) {
			valido = false;
			nuevoError("No se ha indicado ninguna condición para evaluar");
		} else {
			valido = validarCondicion();
		}
		
		if (valido) {
			extraerCondiciones();
			
			System.out.println("Mostrando Condiciones"); 
			for (int i = 0; i < evaluacion.size(); i++) { 
				multiple = evaluacion.get(i);
				// Se obtienen los niveles de profundidad mínimo y máximo de las condiciones  
				if (multiple.getNivel() > maxNivel) { maxNivel = multiple.getNivel(); }
				if (multiple.getNivel() < minNivel) { minNivel = multiple.getNivel(); }
				if (multiple.getNegacion()) {
					System.out.println("Nivel: " + multiple.getNivel() + " " +  multiple.getIdCondicion() + " (NOT " + multiple.getTexto() + ") Madre: " +  multiple.getMadre() + " Tipo: " + multiple.getTipo() + " Negación: " + multiple.getNegacion());
				} else {
					System.out.println("Nivel: " + multiple.getNivel() + " " +  multiple.getIdCondicion() + " " + multiple.getTexto() + " Madre: " +  multiple.getMadre() + " Tipo: " + multiple.getTipo() + " Negación: " + multiple.getNegacion());
				}
				simple = multiple.getCondicion(); 
				if (simple != null) {
					if (simple.getOperando2() != null) {
					System.out.println("Condición simple: " + simple.getOperando1().getNegado() + " -> " + simple.getOperando1().getNombre() + " -> " +  simple.getOperador() + " -> " + simple.getOperando2().getNegado() + " -> " + simple.getOperando2().getNombre());
					System.out.println("Operandos: " + simple.getOperando1().getTipo() + " -> " + simple.getOperando1().getTipoValor() + " -> " + simple.getOperando2().getTipo() + " -> " + simple.getOperando2().getTipoValor());
					} else {
						System.out.println("Condición simple: " + simple.getOperando1().getNegado() + " -> " + simple.getOperando1().getNombre() + " -> null -> null -> null");
					}
				} 
			}
			if (comprobarCondiciones()) {
				// Para obtener el resultado de la condición, se evalúan las condiciones simples de mayor a menor nivel de profundidad, teniendo en cuenta las relaciones definidas entre ellas 
				for (int nivel = maxNivel; nivel >= minNivel; nivel--) {
					for (int i = 0; i < evaluacion.size(); i++) { 
						multiple = evaluacion.get(i);
						if (multiple.getNivel() == nivel) {
							multiple.evalua(evaluacion);
							// El resultado de la condición completa es el resultado de la condición del nivel mínimo
							if (nivel == minNivel) {
								if (multiple.getResultado()) {
									result = 1;
								} else {
									result = 0;
								}							
							}
						}				
					}			
				}
			} else {
				result = -1;
			}
		}
		else {
			result = -1;
		}
		
//		if (result == -1 ) {
//			System.out.println("MOSTRANDO ERRORES");
//			for (int e = 0; e < errores.size(); e++) {
//				String error = errores.get(e);
//				System.out.println(error);
//			}
//		}
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
					// El texto obtenido puede ser una valor, operación ente paréntesis, o una condición. Si es valor, debe ignorarse, si es condición debe tratarse.
					if (esCondicion(txCondicion, operadorAnterior(this.condicionCompleta, abiertos.get(nivel-1).intValue() ))) {
						multiple = new CondicionMultiple();
						multiple.setIdCondicion(indCondicion);
						multiple.setNivel(nivel-1);
						multiple.setTexto(txCondicion);	
						if ((abiertos.get(nivel-1).intValue() - 4) >= 0) {				
							if (this.condicionCompleta.substring(abiertos.get(nivel-1).intValue() - 4, i + 1).startsWith("NOT")) {
								multiple.setNegacion(true);	
							}
						}						
						System.out.println("Nivel: " + multiple.getNivel() + " " + multiple.getIdCondicion() + " " + multiple.getTexto() + " "  + multiple.getNegacion());
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
		ArrayList<String> simples = new ArrayList<String>();
		String simple;
		String texto;
		String litOperador = "";
		int iAnd;
		int iOr;		
		madre = evaluacion.get(indCond);
		System.out.println("infoCondicionMultiple Entrada: " + madre.getTexto());
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
			simples = condicionesSimples(texto, litOperador);
		}
		if ((iAnd > 0 && iOr == 0) || (iAnd == 0 && iOr > 0)) {	
			for (int i = 0; i< simples.size(); i++) {		
				simple = simples.get(i);
				System.out.println("infoCondicionMultiple simples " + i + ": " + simple);
				hija = new CondicionMultiple();				
				hija.setIdCondicion(indCondicion);
				hija.setNivel(nivel+1);
				hija.setTexto(formatoCondicion(simple));	
				hija.setMadre(madre.getIdCondicion());
				hija.setCondicion(operadoresCondicion(simple));
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
	
	private ArrayList<String>  condicionesSimples(String compuesta, String operador ) {
		ArrayList<String> simples = new ArrayList<String>();
		String condicion;
		int indice = 0;
		int desde = 0;
		int hasta = compuesta.indexOf(operador);
		int abiertos;
		int cerrados;
		while (hasta >= 0 && hasta < compuesta.length()) {			
			condicion = compuesta.substring(desde, hasta);
			abiertos = contarCaracter(condicion, "(");
			cerrados = contarCaracter(condicion, ")");	
			if (abiertos != cerrados) {
				if ((abiertos - cerrados) == 1 && condicion.startsWith("(")) {
					condicion = condicion.substring(1);
					abiertos = contarCaracter(condicion, "(");
				}
				if ((cerrados - abiertos) == 1 && condicion.endsWith(")")) {
					condicion = condicion.substring(0, condicion.length()-1);
					cerrados = contarCaracter(condicion, ")");
				}				
			}			
			if (abiertos == cerrados) {
				simples.add(indice, condicion);		
				desde = hasta + operador.trim().length() + 1;
				indice++;
				hasta = compuesta.indexOf(operador, desde);			
			}	else {
				hasta = compuesta.indexOf(operador, hasta + operador.trim().length() + 1);
			}			
		}
		condicion = compuesta.substring(desde);
		abiertos = contarCaracter(condicion, "(");
		cerrados = contarCaracter(condicion, ")");	
		if (abiertos != cerrados) {
			if ((abiertos - cerrados) == 1 && condicion.startsWith("(")) {
				condicion = condicion.substring(1);				
			}
			if ((cerrados - abiertos) == 1 && condicion.endsWith(")")) {
				condicion = condicion.substring(0, condicion.length()-1);				
			}				
		}
		simples.add(indice, condicion);
		return simples;
	}
	
	private void analizarCondicion(Integer indCond) {
	// La condición compuesta contiene condiciones simples entre las que hay relaciones AND y OR. Al estar al mismo nivel de agrupación, en este caso, se da prioridad a la relación OR,
	// creando una relación OR entre las condiciones que estén así relacionadas entre ellas, que se relacionará como AND con el resto de condiciones.
		
		CondicionMultiple madre;
		CondicionMultiple hija;
		CondicionMultiple nieta;
		String texto;
//		String[] partes = new String [] {};
//		String[] simples = new String [] {};		
		Integer condHija; 
		ArrayList<String> partes = new ArrayList<String>();
		ArrayList<String> simples = new ArrayList<String>();
		String simple;
		String parte;
		
		madre = evaluacion.get(indCond);
		madre.setTipo(opLogicos[0].trim());
		evaluacion.set(indCond, madre);		
		// Las partes del texto de la condición que estén entre paréntesis se eliminan del texto de la condición pues, por ser de un nivel superior, ya se habrán tratado.
		texto = eliminarCondicionesInteriores(madre.getTexto());
		// La condición se decompone en condiciones o grupos de condiciones que tendrán una relación AND.
//		partes = texto.split(opLogicos[0]);
		partes = condicionesSimples(texto, opLogicos[0]);	
		for (int i = 0; i< partes.size(); i++) {
			parte = partes.get(i);
			hija = new CondicionMultiple();
			hija.setIdCondicion(indCondicion);
			condHija = indCondicion;
			indCondicion++;					
			hija.setNivel(madre.getNivel()+1);
			hija.setTexto(formatoCondicion(parte));		
			hija.setMadre(madre.getIdCondicion());			
		
			// Si la condición contiene un OR, se descompone en las condiciones que la forman para relacionarlas con OR
			if (parte.indexOf(opLogicos[1]) >= 0) {
				hija.setTipo("OR");
//				simples = parte.split(opLogicos[1]);
				simples = condicionesSimples(parte, opLogicos[1]);
				for (int j= 0; j< simples.size();j++) {
					simple = simples.get(j);
					nieta = new CondicionMultiple();
					nieta.setIdCondicion(indCondicion);
					nieta.setNivel(madre.getNivel()+2);
					nieta.setTexto(formatoCondicion(simple));	
					nieta.setMadre(condHija);	
					nieta.setCondicion(operadoresCondicion(simple));					
					evaluacion.add(indCondicion, nieta);
					indCondicion++;							
				}
			} else {
				hija.setCondicion(operadoresCondicion(partes.get(0)));				
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
		while (i < operadores.length) {
			if (txCondicion.indexOf(operadores[i]) >= 0) {
				operador = operadores[i];
				i = operadores.length + 4;
			}
			i++;
		}		
		// Si se ha encontrado el operador, divide la condición en tres partes: operando1, operador y operando2.
		if (operador != "") {
			String[] minimas = txCondicion.split(operador);			
				nuevaCond = new Condicion();	
				Operando oper1 =nuevoOperando(aislarOperando(minimas[0].trim()));
				nuevaCond.setOperando1(oper1);
				if (minimas.length == 2) {
					Operando oper2 = nuevoOperando(aislarOperando(minimas[1].trim()));				
					nuevaCond.setOperando2(oper2);			
				}			
				nuevaCond.setOperador(operador);
			//	nuevoError ("La condición es errónea. Falta un operador: " + txCondicion); 
								
		} else {
			nuevaCond = new Condicion();				
			Operando oper1 = nuevoOperando(aislarOperando(txCondicion));	
			nuevaCond.setOperando1(oper1);
			Operando oper2 = nuevoOperando("true");
			nuevaCond.setOperador("=");
			nuevaCond.setOperando2(oper2);
		//	nuevoError ("La condición es errónea. No se ha definido ningún operador: " + txCondicion); 
		}
		return nuevaCond;
	}
	
	private boolean validarCondicion() {
		// Comprueba la sintaxis de la condición
		boolean result = new Boolean(true);
		
		if (this.condicionCompleta.length() == 0 || this.condicionCompleta == null) {
			nuevoError("No se ha definido la condición");
			result = false;
		} else {
			this.condicionCompleta = formatoCondicion(this.condicionCompleta);	
			int iAbre = contarCaracter(this.condicionCompleta, "(");
			int iCierra = contarCaracter(this.condicionCompleta, ")");			
		
			// En la condición, el número de "(" debe ser igual al de ")".
			if (iAbre != iCierra) 	{
				result = false;	
				if (iAbre > iCierra) {
					nuevoError("Falta paréntesis )");
				} else {
					nuevoError("Falta paréntesis (");
				}					
			}		
		}
		return result;
	}
	
	private String formatoCondicion(String cadena) {
		String cadenaFormato;		
		cadenaFormato = cadena.trim();
		// La condición completa debe estar entre paréntesis, así se asegura que el nivel mínimo tenga una sola condición, que es la completa.. 
		if (!entreParentesis(cadena) ) {
			cadenaFormato = "(" + cadenaFormato + ")";
		}
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
		cadenaFormato = cadenaFormato.replaceAll("! =", "<>");
		cadenaFormato = cadenaFormato.replaceAll("!  =", "<>");
		cadenaFormato = cadenaFormato.replaceAll("!", " NOT ");		
		// Convierte a mayúsculas todos los operadores AND y OR
		for (int i = 0; i< opLogicos.length;i++) {
			cadenaFormato = cadenaFormato.replaceAll(opLogicos[i].toLowerCase(), opLogicos[i]);
		}
		cadenaFormato = cadenaFormato.replaceAll(" not ", " NOT ");
		cadenaFormato = cadenaFormato.replaceAll("\\(not\\(", "\\( NOT \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\(NOT\\(", "\\( NOT \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\( not\\( ", "( NOT \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\( NOT\\( ", "\\( NOT \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\(not \\( ", "( NOT \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\ NOT \\( ", "\\( NOT \\(");
		// Se eliminan los espacios de más	
		cadenaFormato = cadenaFormato.replaceAll("\\(", " \\(");
		cadenaFormato = cadenaFormato.replaceAll("\\) ", "\\) ");
		cadenaFormato = remplazarCadena(cadenaFormato, "  ", " ");	
		cadenaFormato = cadenaFormato.replaceAll("\\( ", "\\(");
		cadenaFormato = cadenaFormato.replaceAll(" \\)", "\\)");
		cadenaFormato = cadenaFormato.replaceAll("\\'", constantes.comillas);
		return cadenaFormato;
	}
	
	private boolean entreParentesis(String cadena) {
	// Comprueba si la conrición completa contenida en el parámetro cadena está indicada entre paréntesis. 
	// Estará entre paréntesis si la primera y última posición de la cadena contienen, respectivamente '(' y ')' y el paréntesis de la última posición cierra el de la primera
		boolean entre = false;
		int nivel = 0;
	
		ArrayList<Integer> abiertos = new ArrayList<Integer>(); // Almacena las posiciones en el texto en la que se encuntra el '(' que abre la condición actual. El índice se corresponderá con el nivel
		cadena = cadena.trim();
		char c;
		for (int i = 0;i<this.condicionCompleta.length();i++) {
			c = this.condicionCompleta.charAt(i);
			if (c == abre) {
				// Por cada '(' se guarda en el array la posición en la que se encuentra en el nivel actual
				abiertos.add(nivel, new Integer(i));
				nivel++;
			} else {
				if (c == cierra) {		
					if ((nivel - 1) == 0 && cadena.startsWith("(")  && (abiertos.get(nivel - 1) == 0) && (i == (cadena.length() - 1))) {
						entre = true;
					}
					abiertos.remove(nivel - 1);	
					nivel--;
				}					
			}				
		}			
		return entre;
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

	private String aislarOperando(String operando) {
		String oper = operando;
		
		int abiertos = contarCaracter(oper, "(");
		int cerrados = contarCaracter(oper, ")");		
		if (abiertos != cerrados) {
			if (oper.startsWith("(") && !(oper.endsWith(")"))) {
				oper = oper.replaceAll("\\(", "");
			} else {
				if (oper.endsWith(")") && !(oper.startsWith("("))) {
					oper = oper.replaceAll("\\)", "");
				} 
			}
		} else {
			if (abiertos == 1 && cerrados == 1 && oper.startsWith("(") && oper.endsWith(")") ) {
				oper = oper.replaceAll("\\(", "");
				oper = oper.replaceAll("\\)", "");					
			}
		}		
		return oper;
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
						if (!esCondicion(textoFinal.substring(iAbre, i + 1), operadorAnterior(textoFinal, iAbre))) {
							textoFinal = cambiarCaracter (textoFinal, iAbre, '[');
							textoFinal = cambiarCaracter (textoFinal, i, ']');
						} else {
							if ((iAbre - 4) >= 0) {							
								if (textoFinal.substring(iAbre - 4, i + 1).startsWith("NOT")) {							
									iAbre = iAbre - 4;
								}
							}
							textoFinal = textoFinal.replace(textoFinal.substring(iAbre, i + 1), " ");
						}												
						cambio = true;
					}					
				}
				i++;				
			}	
		}
		textoFinal = limpiaCondicion(textoFinal);
		return textoFinal;
	}	 
	
	private String operadorAnterior(String texto, int pos) {
		String opAnt = " ";
		char c;
		int i = pos - 1;
		c = texto.charAt(i);
		boolean espacio = false;
		
		while (i >=0) {			
			c = texto.charAt(i);
			if (c == ' ') 	{ 
				if (!espacio) {
					espacio = true;
					opAnt = c + opAnt;
				} else {
					i = -1;							
				}				
			} else {
				opAnt = c + opAnt;
			}
			i--;
		}
		//System.out.println("operadorAnterior. Entrada: " + texto + " Pos: " + pos + " . Salida: " + opAnt.trim());
		
		return opAnt.trim();
	}
	private String limpiaCondicion (String texto) {
		String textoLimpio = texto;
		// Se corrige la repetición de operadores lógicos resultante de le eliminación del texto entre paréntesis que había entre ellos. 
		
		textoLimpio = remplazarCadena(textoLimpio, "  ", " ");	
		textoLimpio = remplazarCadena(textoLimpio, " AND AND ", " AND ");
		textoLimpio = remplazarCadena(textoLimpio, " OR OR ", " OR ");
		textoLimpio = textoLimpio.replaceAll(" AND \\)", "\\)");
		textoLimpio = textoLimpio.replaceAll("\\( AND ", "\\(");		
		textoLimpio = textoLimpio.replaceAll(" OR \\)", "\\)");	
		textoLimpio = textoLimpio.replaceAll("\\( OR ", "\\(");	
		
		textoLimpio = textoLimpio.replaceAll("\\[", "\\(");	
		textoLimpio = textoLimpio.replaceAll("\\]", "\\)");
		textoLimpio = textoLimpio.replaceAll("\\( ", "\\(");		
		textoLimpio = textoLimpio.replaceAll(" \\)", "\\)");		
		textoLimpio = remplazarCadena(textoLimpio, "  ", " ");	
		
		if (textoLimpio.indexOf(" AND OR ") > 0 || textoLimpio.indexOf(" OR AND ") > 0) {
			nuevoError("La relación lógica en la condición no es correcta: " + textoLimpio);
		}		
		return textoLimpio;
	}
	
	private String remplazarCadena(String cadena, String busca, String cambia) {
		String remplazo = cadena;
		while (remplazo.indexOf(busca) > 0) {		
			remplazo = remplazo.replaceAll(busca, cambia);
		}	
//		remplazo = remplazarCadena(remplazo, "  ", " ");
		return remplazo;
	}
	
	private boolean esCondicion(String texto, String previo) {
	// Identifica si es una condición o un valor, para saber cómo debe tratarse. Si el texto contiene algún operador, será una condición.
	//	System.out.println ("Parámetros: " + texto + " ->" + previo + "<-") ;
		boolean esCond = true;
		
		for (int i = 0; i < operadores.length; i++) {
			if (previo.equals(operadores[i])) {
				esCond = false;
			}
		}				
		if (esCond) {
			esCond = false;
			for (int i = 0; i < operadores.length; i++) {
				if (texto.indexOf(operadores[i]) >= 0) {
					esCond = true;
				}
			}			
		}
		return esCond;
	}
	
	private String cambiarCaracter (String cadena, int pos, char car) {
	// Sustituye en una cadena el carácter situado en una posición por el indicado.
		String cambiada;
		cambiada = cadena.substring(0, pos) + car + cadena.substring(pos+1);
		return cambiada;		
	}	
	
	private void nuevoError(String txError) {
	// Añade el nuevo error detectado a la lista de errores de la evaluación		
		int i = errores.size();
		errores.add(i, txError);  ;
	}
	
	private boolean comprobarCondiciones() {
	// Recorre la lista de condiciones encontradas en la consulta completa para determinar si la información obtenida es correcta y se puede evaluar la condición compleja
		boolean result = true;		
		CondicionMultiple multiple;		
		
		for (int m = 0; m < evaluacion.size(); m++) {
			multiple = evaluacion.get(m);
			if (multiple.getCondicion() != null) {
				Condicion simple = multiple.getCondicion();
				boolean op1 = comprobarOperando(simple.getOperando1());
				boolean op2 = comprobarOperando(simple.getOperando2());		
				if (op1 == false || op2 == false) {
					result = false;
				}								
			}
		}		
		return result;
	}
	
	private boolean comprobarOperando(Operando oper) {
	// Comprueba si la información obtenida para el valor del opernado es completa y coherente.
		boolean comprobado = true;
		if (oper.getTipo() == constantes.tpIndErroneo) {
			comprobado = false;
			nuevoError("No se ha encontrado el indicador " + oper.getNombre());
		} 
		if (oper.getTipo() == constantes.tpNoIndicador && oper.getTipoValor() == constantes.tpVlNoTipo) {
			comprobado = false;
			nuevoError("No se ha podido identificar el indicador " + oper.getNombre());
		}
		if (oper.getTipo() == constantes.tpIndicador && oper.getTipoValor() != constantes.tpVlIndicador) {
			comprobado = false;
			nuevoError("El tipo de indicador y el tipo del resultado del operando no coinciden: " + oper.getNombre());
		}
		if (oper.getTipo() == constantes.tpValor && !(oper.getTipoValor() != constantes.tpVlBoolean || oper.getTipoValor() != constantes.tpVlString || oper.getTipoValor() != constantes.tpVlInt || oper.getTipoValor() != constantes.tpVlDate)) {
			comprobado = false;
			nuevoError("No se ha podido identificado el tipo de resultado del operando: " + oper.getNombre());
		}		
		return comprobado;
	}
	
	private int esIndicador (String operando) {
	// Comprueba si la cadena recibida como parámetro corresponde a alguno de los indicadores definidios en el análisis
		int esIndica = constantes.tpNoIndicador;
		String[] tramos = new String [] {};
		tramos = operando.split("\\.");

		for (int t = 0; t < tramos.length; t++) {		
			for (int i = 0; i < indicadores.size(); i++) {	
				if (indicadores.get(i).getNombre().equals(tramos[t])) {
					esIndica = constantes.tpIndicador;					
				}
			}
		}
		if (tramos.length > 1 && esIndica == constantes.tpNoIndicador) {
			esIndica = constantes.tpIndErroneo;
		}
		return esIndica;		
	}	

	private int esValor(String operando) {
	// Comprueba si la cadena recibida como párametro corresponde a un valor: Los valores pueden ser lógicos, de cadena, numéricos o de fecha.
		int valor = constantes.tpVlNoTipo;
		String oper = operando; 
		
		if (operando.toUpperCase().equals(constantes.verdadero) || operando.toUpperCase().equals(constantes.falso)) {
			valor = constantes.tpVlBoolean;
		} else {
			if (operando.startsWith(constantes.comillas) && operando.endsWith(constantes.comillas) && contarCaracter(operando, constantes.comillas) == 2) {
				if (esFecha(operando)) {
					valor = constantes.tpVlDate;
				} else {
					valor = constantes.tpVlString;
				}
			} else {
				if (contarCaracter(operando, constantes.comillas) == 0) {
					oper = oper.replaceAll("\\,", "\\.");
					
					try {
						Integer.parseInt(oper);
						valor = constantes.tpVlInt;
					} catch (NumberFormatException e3) {
						valor = constantes.tpVlNoTipo;
						nuevoError("El operando contiene un valor numérico erróneo: " + operando);
					}					
				}
			}
		}		
		return valor;
	}
		
	private Operando nuevoOperando(String nombre) {
	// Crea el operando de una condición simple. 
		int tipo;
		Operando oper = new Operando();			
		// El operando puede ser de tres tipos: Indicador, valor o fórmula. Éste último aún no se ha codificado, por lo que se identifica como erróneo.	
		oper.setNombre(nombre);		
		tipo = esIndicador(oper.getNombre());
		if (tipo == constantes.tpIndicador) {
			oper.setTipo(constantes.tpIndicador);
			oper.setTipoValor(constantes.tpVlIndicador);
		} else {
			if (tipo == constantes.tpIndErroneo) {
				oper.setTipo(constantes.tpIndErroneo);
				oper.setTipoValor(constantes.tpVlNoTipo);				
			} else {			
				tipo = esValor(nombre);
				// Si es de tipo valor, guarda el tipo de valor que es.
				if (tipo != constantes.tpVlNoTipo) {
					oper.setTipo(constantes.tpValor);
					oper.setTipoValor(tipo);				
				} 
			}
		}
		// Si el operando es un Indicador. Crea el objeto indicador del operando.
		if (oper.getTipo() == constantes.tpIndicador) {
			String[] tramos = new String [] {};
			tramos = oper.getNombre().split("\\."); 
			for (int t = 0; t < tramos.length; t++) {		
				for (int i = 0; i < indicadores.size(); i++) {	
					if (indicadores.get(i).getNombre().equals(tramos[t])) {
						oper.setIndicador(indicadores.get(i));				
					}
				}
			}			
		}		
		return oper;
	}
	
	private boolean esFecha (String fecha) {
	// Comprueba si la cadena recibida corresponde a una fecha		
		boolean vale = false;
		String formatosFecha [] = new String [] {"dd/MM/yyyy","dd-MM-yyyy", "MM/dd/yyyy", "MM-dd-yyyy", "yyyy/MM/dd", "yyyy-MM-dd", "dd/MM/yy","dd-MM-yy", "MM/dd/yy", "MM-dd-yy", "yy/MM/dd", "yy-MM-dd"};
		String formatosHora [] = new String [] {"hh:mm:ss","hh:mm", "HH:mm:ss", "HH:mm"};
		String txFecha = fecha.replaceAll(constantes.comillas, "");
		Date fecParse;
		SimpleDateFormat formato;
		String txFormato;
		
		int f = 0;
		int h;
		while (f < formatosFecha.length && vale == false) {
			h = 0;
			while (h < formatosHora.length && vale == false) {
				if (txFecha.indexOf(" ") >= 0) {
					txFormato = formatosFecha[f] + " " + formatosHora[h];					
				} else {
					txFormato = formatosHora[h];	
				}
				formato= new SimpleDateFormat(txFormato);
		        try {		        	
		            fecParse = formato.parse(txFecha);           		            
		            f = formatosFecha.length + 5;
		            h = formatosHora.length + 5;
		            vale = true;	
		        } catch (ParseException ef) {
		        	h++;
		        }					
			}
			if (!vale) {
				txFormato= formatosFecha[f];		
				formato= new SimpleDateFormat(txFormato);
        		try {
        			fecParse = formato.parse(txFecha);        			        			
        			f = formatosFecha.length + 5;
        			vale = true;
        		} catch (ParseException eh) {
        			f++;
        		}	        		
	        }  
		}
		return vale;		
	}
}
 
