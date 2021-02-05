package com.atalaya.evaluador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Clase que evalua una condicion compleja. Puede devolver tres posibles resultados:
//1. Se ha evaluado la condicion y se cumple (true)
//0. Se ha evaluado la condicion y no se cumple (false)
//-1. Se han detectado errores en la condicion y no se ha evaluado.
public class Evaluador {
	
	private String condicionCompleta;
	private ArrayList<CondicionMultiple> evaluacion = new ArrayList<CondicionMultiple>();
	private ArrayList<String> errores = new ArrayList<String>();
	private AnalisisProxy analisis;
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
	
	public Evaluador (AnalisisProxy analisis) {		
		super();		
		this.analisis = analisis;		
			
	}
		
	/*public int evaluar (String condicion, ArrayList<Indicador> indicadores)  {
		int result = 0;
		this.condicionCompleta = condicion;		
		this.indicadores = indicadores;
		result = this.evaluar();
		return result;
	}*/
	
	public StringBuffer evaluarAnalisis() {
		StringBuffer mensaje = new StringBuffer();
		int iError = 0;
		int cumplido = 0;
		boolean encontrado = false;

		mensaje.append("</br>Ejecutado el analisis: <b>"+ analisis.getNombre() + "</b></br>");
		
		for (int c = 0; c < analisis.getCriterios().size(); c++) {	
			cumplido = 0;
			CriterioProxy criEvalua = analisis.getCriterios().get(c);
			
			for (int ec = 0; ec < criEvalua.getCriterio().getEventos().length; ec++) {
				encontrado = false;
				for (int e = 0; e < analisis.getEventos().size(); e++) {
					if (!criEvalua.getCriterio().getEventos()[ec].equalsIgnoreCase(analisis.getEventos().get(e).getEvento().getNombre())) {
						encontrado = true;							
					}
				}
				if (!encontrado) {
					nuevoError("El evento " + criEvalua.getCriterio().getEventos()[ec] + " definido en el criterio " + criEvalua.getCriterio().getNombre() + " no esta definido en el analisis " + analisis.getNombre());
					cumplido = -1;
				}
			}					
			
			if (cumplido == 0) {
				cumplido = evaluarCriterio(criEvalua);
			} 
			
			switch (cumplido) {
			case -1:
			// Se han detecado errores en la evaluación
				// mensaje.append("<br>La condicion indicada en el criterio <b>" + criEvalua.getNombre() + "</b> no es correcta.</br>");
				mensaje.append("<br>La definicion del criterio <b>" + criEvalua.getCriterio().getNombre() + "</b> no es correcta.</br>");
				iError++;
		    	for (int e = 0; e < this.errores.size(); e++) {
		    		//mensaje.add(iError, criEvalua.getErrores().get(e));
		    		mensaje.append("<br><b>"+this.errores.get(e)+"</b></br>");
		    		iError++;
		    	}	
		    	break;
			case 0:
			   // La evaluación ha dado como resultado que se no cumple. No deben generarse los eventos asociados al criterio.				
				mensaje.append("<br>El criterio <b>" + criEvalua.getCriterio().getNombre() + "</b> no cumple la condicion. No se generan eventos.</br>");
				iError++;
				break;
			case 1:
			// La evaluación ha dado como resultado que se cumple. Deben generarse los eventos asociados al criterio.
				//mensaje.add(iError, "El criterio " + criAnaliza.getNombre() + " cumple la condicion '" +criAnaliza.getEvaluacion() + "'");
				mensaje.append("<br>El criterio <b>" + criEvalua.getCriterio().getNombre() + "</b> cumple la condicion <b>'" + criEvalua.getCriterio().getEvaluacion() + "'</b> </br>");
				//mensaje.add(iError, "Se han generado los siguientes eventos:");
				mensaje.append("</br>Se han generado los siguientes eventos:</br>");
				for (int e = 0; e < criEvalua.getCriterio().getEventos().length; e++)
				{
					//mensaje.add(iError, criEvalua.getEventos().get(e).getNombre() + " ejecutado con el resultado: "+ criEvalua.getEventos().get(e).getResultadoEjecucion());
					//mensaje.append("</br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b>"+criEvalua.getEventos().get(e).getEvento().getNombre() + "</b> ejecutado con el resultado: <b>"+ criEvalua.getEventos().get(e).getResultadoEjecucion() + "</b></br>");
					for (int ea = 0; ea < this.analisis.getEventos().size(); ea++) {
						if (this.analisis.getEventos().get(ea).getEvento().getNombre().equalsIgnoreCase(criEvalua.getCriterio().getEventos()[e])) {
							mensaje.append("</br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b>"+criEvalua.getCriterio().getEventos()[e] + "</b> ejecutado con el resultado: <b>"+ this.analisis.getEventos().get(ea).getResultadoEjecucion() + "</b></br>");
						}
					}
				}				
				iError++;
				break;
			}

		}
		return mensaje;
	}	
	
	public int evaluarCriterio (CriterioProxy criEvaluar)  {
		int result = 0;
		boolean valido = true;
		CondicionMultiple multiple;
		Condicion simple;
		int maxNivel = 0;
		int minNivel = 0;		
		this.condicionCompleta = criEvaluar.getCriterio().getEvaluacion();
		
		evaluacion.clear();
		errores.clear();
		
		if (condicionCompleta == "" || condicionCompleta == null) {
			valido = false;
			nuevoError("No se ha indicado ninguna condicion para evaluar");
		} else {
			valido = validarCondicion();
		}
		
		if (valido) {
			//informa la lista evaluacion
			extraerCondiciones();
			
			System.out.println("Mostrando Condiciones"); 
			for (int i = 0; i < evaluacion.size(); i++) { 
				multiple = evaluacion.get(i);
				// Se obtienen los niveles de profundidad minimo y maximo de las condiciones  
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
					System.out.println("Condicion simple: " + simple.getOperando1().getNegado() + " -> " + simple.getOperando1().getNombre() + " -> " +  simple.getOperador() + " -> " + simple.getOperando2().getNegado() + " -> " + simple.getOperando2().getNombre());
					System.out.println("Operandos: " + simple.getOperando1().getTipo() + " -> " + simple.getOperando1().getTipoValor() + " -> " + simple.getOperando2().getTipo() + " -> " + simple.getOperando2().getTipoValor());
					} else {
						System.out.println("Condicion simple: " + simple.getOperando1().getNegado() + " -> " + simple.getOperando1().getNombre() + " -> null -> null -> null");
					}
				} 
			}
			
			//valida que los operandos de las condiciones o evaluaciones sean validos
			if (comprobarCondiciones()) {
				// Para obtener el resultado de la condicion, se evalúan las condiciones simples de mayor a menor nivel de profundidad, teniendo en cuenta las relaciones definidas entre ellas 
				for (int nivel = maxNivel; nivel >= minNivel; nivel--) {
					for (int i = 0; i < evaluacion.size(); i++) { 
						multiple = evaluacion.get(i);
						if (multiple.getNivel() == nivel) {
							evaluaCondicionMultiple(multiple);
							// El resultado de la condicion completa es el resultado de la condicion del nivel minimo
							if (nivel == minNivel) {
								if (multiple.getResultado()) {
									result = 1;
									eventosCriterio(criEvaluar);
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
	// Recorre el texto de la condicion indicada, identificando por niveles el texto comprendido entre parentesis. Cada trozo de texto comprendido entre parentesis del mismo nivel, será una condicion
		int nivel = 0;
		String txCondicion;		
		Integer indCond;
		
		ArrayList<Integer> abiertos = new ArrayList<Integer>(); // Almacena las posiciones en el texto en la que se encuntra el '(' que abre la condicion actual. El índice se corresponderá con el nivel
		
		CondicionMultiple multiple;		
		System.out.println("condicion completa: " + this.condicionCompleta);
		char c;
		for (int i = 0;i<this.condicionCompleta.length();i++) {
			c = this.condicionCompleta.charAt(i);
			if (c == abre) {
				// Por cada '(' se guarda en el array la posicion en la que se encuentra en el nivel actual
				abiertos.add(nivel, new Integer(i));
				nivel++;
			} else {
				if (c == cierra) {					
					// Cuando se encuentra un ')' se extre la condicion desde el inicial guardado en el array hasta la posicion en la que se cierra el nivel
					txCondicion = this.condicionCompleta.substring(abiertos.get(nivel-1).intValue(), i + 1);
					// El texto obtenido puede ser una valor, operación ente parentesis, o una condicion. Si es valor, debe ignorarse, si es condicion debe tratarse.
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
	// Se completa la informacion de la condicion recien creada, creando sus condiciones hijas y relacionandolas con ellas
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
		// Las condiciones del nivel superior que no tengan madre seran hijas de esta condicion 
		for (int i = 0; i<evaluacion.size();i++) {
			hija = evaluacion.get(i);			
			if (hija.getMadre() == null && hija.getNivel() == (madre.getNivel() + 1) && hija.getIdCondicion() != madre.getIdCondicion()) {
				hija.setMadre(indCond);
				evaluacion.set(i,  hija);
			}			
		}
		// Las partes del texto de la condicion que estan entre parentesis se eliminan del texto de la condicion pues, por ser de un nivel superior, ya se habran tratado.
		texto = eliminarCondicionesInteriores(madre.getTexto());
		// Se obtienen el numero de operadores AND y OR que tiene la condicion.
		iAnd = contarCaracter(texto, opLogicos[0]);
		iOr = contarCaracter(texto, opLogicos[1]);
		// Si la condicion contiene operadores de un sólo tipo, se le asigna el tipo. Si contiene los dos operadores, se tratará aparte		
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
	// Para obtener las condiciones simples de la condicion. Si no tiene ninguno de los dos operadores, seria una condicion simple. Si tiene varios operadores, se utilizara el 
		// operador logico para separar cada una de las condiciones simples.
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
	// Obtiene las condiciones simples que forman una compuesta eliminando los parentesis sin abrir o cerrar que pueda contener la compuesta .
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
	// La condicion compuesta contiene condiciones simples entre las que hay relaciones AND y OR. Al estar al mismo nivel de agrupacion, en este caso, se da prioridad a la relacion OR,
	// creando una relacion OR entre las condiciones que estan asi relacionadas entre ellas, que se relacionaran como AND con el resto de condiciones.
		
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
		// Las partes del texto de la condicion que estan entre parentesis se eliminan del texto de la condicion pues, por ser de un nivel superior, ya se habran tratado.
		texto = eliminarCondicionesInteriores(madre.getTexto());
		// La condicion se decompone en condiciones o grupos de condiciones que tendran una relacion AND.
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
		
			// Si la condicion contiene un OR, se descompone en las condiciones que la forman para relacionarlas con OR
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
	// A partir de un texto que contiene una condicion simple, obtiene los operandos y operador y crea el objeto condicion que devuelve como resultado
		Condicion nuevaCond = null;
		
		int i = 0;
		String operador = "";
		// Obtiene el operador utilizado en la condicion
		i = 0;
		while (i < operadores.length) {
			if (txCondicion.indexOf(operadores[i]) >= 0) {
				operador = operadores[i];
				i = operadores.length + 4;
			}
			i++;
		}		
		// Si se ha encontrado el operador, divide la condicion en tres partes: operando1, operador y operando2.
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
			//	nuevoError ("La condicion es errónea. Falta un operador: " + txCondicion); 
								
		} else {
			nuevaCond = new Condicion();				
			Operando oper1 = nuevoOperando(aislarOperando(txCondicion));	
			nuevaCond.setOperando1(oper1);
			Operando oper2 = nuevoOperando("true");
			nuevaCond.setOperador("=");
			nuevaCond.setOperando2(oper2);
		//	nuevoError ("La condicion es errónea. No se ha definido ningún operador: " + txCondicion); 
		}
		return nuevaCond;
	}
	
	private boolean validarCondicion() {
		// Comprueba la sintaxis de la condicion
		boolean result = new Boolean(true);
		
		if (this.condicionCompleta.length() == 0 || this.condicionCompleta == null) {
			nuevoError("No se ha definido la condicion");
			result = false;
		} else {
			this.condicionCompleta = formatoCondicion(this.condicionCompleta);	
			int iAbre = contarCaracter(this.condicionCompleta, "(");
			int iCierra = contarCaracter(this.condicionCompleta, ")");			
		
			// En la condicion, el numero de "(" debe ser igual al de ")".
			if (iAbre != iCierra) 	{
				result = false;	
				if (iAbre > iCierra) {
					nuevoError("Falta parentesis )");
				} else {
					nuevoError("Falta parentesis (");
				}					
			}		
		}
		return result;
	}
	
	private String formatoCondicion(String cadena) {
		String cadenaFormato;		
		cadenaFormato = cadena.trim();
		// La condicion completa debe estar entre parentesis, asi se asegura que el nivel minimo tenga una sola condicion, que es la completa.. 
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
	// Comprueba si la condicion completa contenida en el parametro cadena esta indicada entre parentesis. 
	// Estara entre parentesis si la primera y ultima posicion de la cadena contienen, respectivamente '(' y ')' y el parentesis de la ultima posicion cierra el de la primera
		boolean entre = false;
		int nivel = 0;
	
		ArrayList<Integer> abiertos = new ArrayList<Integer>(); // Almacena las posiciones en el texto en la que se encuntra el '(' que abre la condicion actual. El indice se correspondera con el nivel
		cadena = cadena.trim();
		char c;
		for (int i = 0;i<this.condicionCompleta.length();i++) {
			c = this.condicionCompleta.charAt(i);
			if (c == abre) {
				// Por cada '(' se guarda en el array la posicion en la que se encuentra en el nivel actual
				abiertos.add(nivel, new Integer(i));
				nivel++;
			} else {
				if (c == cierra) {		
					// Comprueba si ese ')' cerrara el nivel 0, si la condicion empieza por '(' y el nivel que se va a cerrar se abrio con un '(' en la primera posicion 
					// y si la posicion del ')' es la ultima de la cadena
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
	// Cuenta el numero de veces que el caracter aparece en la cadena
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
	// Elimina los parentesis al inicio y al final de la cadena
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
	// Elimina del texto de entrada las partes de texto contenidas entre parentesis, manteniendo los parentesis de inicio y fin del texto. Esas condiciones interiores se habran definido en niveles superiores
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
	// Devuelve el texto anterior entre espacios en un texto situado delante de la posicion indicada.
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
				
		return opAnt.trim();
	}
	
	private String limpiaCondicion (String texto) {
		String textoLimpio = texto;
		// Se corrige la repetición de operadores lógicos resultante de le eliminación del texto entre parentesis que había entre ellos. 
		
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
			nuevoError("La relacion lógica en la condicion no es correcta: " + textoLimpio);
		}		
		return textoLimpio;
	}
	
	private String remplazarCadena(String cadena, String busca, String cambia) {
	// Sustituye en una cadena la cadena de busqueda por la que que se cambia.
	// Utiliza un bucle con replaceAll para, por ejemplo, los casos en los que se quiere cambiar una serie de espacios seguidos por un solo.
		String remplazo = cadena;
		while (remplazo.indexOf(busca) > 0) {		
			remplazo = remplazo.replaceAll(busca, cambia);
		}	
//		remplazo = remplazarCadena(remplazo, "  ", " ");
		return remplazo;
	}
	
	private boolean esCondicion(String texto, String previo) {
	// Identifica si es una condicion o un valor, para saber como debe tratarse. Si el texto contiene algun operador, sera una condicion.
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
	// Sustituye en una cadena el caracter situado en una posicion por el indicado.
		String cambiada;
		cambiada = cadena.substring(0, pos) + car + cadena.substring(pos+1);
		return cambiada;		
	}	
	
	private void nuevoError(String txError) {
	// Anhade el nuevo error detectado a la lista de errores de la evaluacion		
		int i = errores.size();
		errores.add(i, txError);  ;
	}
	
	private boolean comprobarCondiciones() {
	// Recorre la lista de condiciones encontradas en la consulta completa para determinar si la información obtenida es correcta y se puede evaluar la condicion compleja
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
	// Comprueba si la cadena recibida como parámetro corresponde a alguno de los indicadores definidios en el análisis. Devuelve  -1, si es erroneo, 0 si no lo es, y 1 si es indicador.
		int esIndica = constantes.tpNoIndicador;
		
		if (operando.startsWith(constantes.tpMarcaIndicador))
		{	
			esIndica = constantes.tpIndErroneo;
			
			String[] tramos = new String [] {};
			tramos = operando.split("\\.");
			
			for (int i = 0; i < analisis.getIndicadores().size(); i++) 
			{	
				if (analisis.getIndicadores().get(i).getIndicador().getNombre().equals(tramos[0].substring(1))) 
				{
					esIndica = constantes.tpIndicador;
					break;
				}
			}	
		}
		else
			esIndica = constantes.tpNoIndicador;
		
		return esIndica;		
	}	

	private int esValor(String operando) {
	// Comprueba si la cadena recibida como parametro corresponde a un valor: Los valores pueden ser logicos, de cadena, numericos o de fecha.
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
	// Crea el operando de una condicion simple. 
		int tipo;		
		Operando oper = new Operando();			
		// El operando puede ser de tres tipos: Indicador, valor o fórmula. Éste último aún no se ha codificado, por lo que se identifica como erróneo.	
		oper.setNombre(nombre);		
		tipo = esIndicador(oper.getNombre());
		if (tipo == constantes.tpIndicador) 
		{
			oper.setTipo(constantes.tpIndicador);
			oper.setTipoValor(constantes.tpVlIndicador);
			
			String[] tramos = new String [] {};
			tramos = oper.getNombre().split("\\.");
			
			for (int i = 0; i < analisis.getIndicadores().size(); i++) 
			{	
				if (analisis.getIndicadores().get(i).getIndicador().getNombre().equals(tramos[0].substring(1))) 
				{
					oper.setIndicador(analisis.getIndicadores().get(i));
					parametrosIndicador(analisis.getIndicadores().get(i));
					break;
				}
			}
		} 
		else 
		{
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
	
	//Miedito me da. He metido recursividad por la posible depenedencia entre indicadores esto habria que resolverlo de otra manera que seria 
	//realizar una batida continua (despues de la ejecucion de cualquier indicador) e ir resolviendo los indicadores segun su dependencia
	private void parametrosIndicador(IndicadorProxy indOper) {
		
		for (int p = 0; p < indOper.getIndicador().getParametros().size(); p++) {			
			if (indOper.getIndicador().getParametros().get(p).getValor().startsWith(constantes.tpMarcaIndicador)) {
				
				String[] tramos = new String [] {};
				tramos = indOper.getIndicador().getParametros().get(p).getValor().split("\\.");
				
				for (int i = 0; i < analisis.getIndicadores().size(); i++) 
				{	
					if (analisis.getIndicadores().get(i).getIndicador().getNombre().equals(tramos[0].substring(1))) 
					{
						parametrosIndicador(analisis.getIndicadores().get(i));
						if (analisis.getIndicadores().get(i).ejecutar() == 0 && (analisis.getIndicadores().get(i).getResultadoEjecucion()!=null) && analisis.getIndicadores().get(i).getResultadoEjecucion().size()>0) {					
							String columna = tramos[1];
							Object valParam = new Object();
							Object[] linea = analisis.getIndicadores().get(i).getResultadoEjecucion().elementAt(0);
							int c = 0;
							while (c < analisis.getIndicadores().get(i).getIndicador().getResultado().length ) {
								if (columna.equals(analisis.getIndicadores().get(i).getIndicador().getResultado()[c])) {
									valParam = linea[c];
									break;
								}
								c++;
							}
							indOper.getIndicador().getParametros().get(p).setValor(valParam.toString());					
						}
					}
				}
			}
		}
	}
	
	public boolean evaluaCondicionMultiple (CondicionMultiple condicion) {
		boolean result = true;
		CondicionMultiple hija;
		
		if (condicion.getEvaluada()) {	return condicion.getResultado();		}	
		if (condicion.getTipo().equalsIgnoreCase("AND"))			{	result = true;		}
		else if (condicion.getTipo().equalsIgnoreCase("OR")) 		{	result = false;		}
		
		if (condicion.getCondicion() == null) {
			for (int i = 0; i < evaluacion.size(); i++) { 
				hija = evaluacion.get(i);
				if (hija.getMadre() == condicion.getIdCondicion())	{					
					if (condicion.getTipo().equalsIgnoreCase("AND") && !hija.getResultado() ) {
						result = false;
					}
					if (condicion.getTipo().equalsIgnoreCase("OR") && hija.getResultado()) {
						result = true;
					}					
				}			
			}			
		} else {
			result = evaluaCondicionSimple(condicion.getCondicion());
		}		
		if (condicion.getNegacion()) {
			result = !result;
		}
		condicion.setResultado(result);
		condicion.setEvaluada(true);		
		return result;	
	}		
	
	public boolean evaluaCondicionSimple(Condicion simple) {
	boolean result = false;	
	String oper1;
	String oper2;
	int iOper1 = 0;
	int iOper2 = 0;
	boolean numerica;
	int compara;
	
		if (simple.getEvaluada()) {return simple.getResultado();  } 
		else {
			// Recupera los resultados de cada uno de los operandos de la condicion.
			oper1 = simple.getOperando1().getResultado().toString().trim();
			oper2 = simple.getOperando2().getResultado().toString().trim();			
			// Convierte ambos resultados a int para decidir si la comparacion sera numerica o alfabetica.
			numerica = true;
			try {
				iOper1 = Integer.parseInt(oper1);				
			} catch (NumberFormatException e3) {				
				numerica = false;
			}	
			try {
				iOper2 = Integer.parseInt(oper2);				
			} catch (NumberFormatException e3) {				
				numerica = false;
			}
			// Si ha podido convertir a int ambos resultados, los comparará numéricamente. Si no, hará comparación alfabéticamente.
			String operadores [] = new String[] {"=", "<", ">", ">=", "<=", "<>"};
			int op = 0;
			for (op = 0; op < operadores.length; op++) {
				if (simple.getOperador().equals(operadores[op])) {
					break;
				}
			}			
			
			if (numerica) {			
				switch (op) {
				case 0:
					if (iOper1 == iOper2) 	{	result = true;break;		}
				case 1:
					if (iOper1 < iOper2) 	{	result = true;break;		}
				case 2:
					if (iOper1 > iOper2)	{	result = true;break;		}
				case 3:
					if (iOper1 >= iOper2) 	{	result = true;break;		}
				case 4:
					if (iOper1 <= iOper2) 	{	result = true;break;		}
				case 5:	
					if (iOper1 != iOper2) 	{	result = true;break;		}
				}		
			} else {
				compara = oper1.compareToIgnoreCase(oper2);
				if (compara < 0 && (simple.getOperador().equals("<") || simple.getOperador().equals("<=") || simple.getOperador().equals("<>"))) {
					result = true; 
				} else {
					if (compara == 0 && (simple.getOperador().equals("=") || simple.getOperador().equals("<=") || simple.getOperador().equals(">="))) {
						result = true;
					} else {
						if (compara > 0 && (simple.getOperador().equals(">") || simple.getOperador().equals(">=") || simple.getOperador().equals("<>"))) {
							result = true;
						}
					}
				}					
			}			
			simple.setEvaluada(true);
		}
		if (simple.getNegacion()) 	{result = !result;	}
		simple.setResultado(result);
		return result;
	}

	
	
	public void eventosCriterio (CriterioProxy criProxy) {
	// Recorre la lista de nombres de eventos del criterio para identificar y generar los eventos definidos en el analisis.
		for (int e = 0; e < analisis.getEventos().size(); e++) {
			for (int ec = 0; ec < criProxy.getCriterio().getEventos().length; ec++) {
				if ( analisis.getEventos().get(e).getEvento().getNombre().equalsIgnoreCase(criProxy.getCriterio().getEventos()[ec])) {
					parametrosEvento(analisis.getEventos().get(e));
					analisis.getEventos().get(e).generarEvento();
				}
			}
		}
	}
	
	private void parametrosEvento(EventoProxy eventoPrx) {
		
		for (int p = 0; p < eventoPrx.getEvento().getParametros().size(); p++) {			
			if (eventoPrx.getEvento().getParametros().get(p).getValor().startsWith(constantes.tpMarcaIndicador)) {
				
				String[] tramos = new String [] {};
				tramos = eventoPrx.getEvento().getParametros().get(p).getValor().split("\\.");
				
				for (int i = 0; i < analisis.getIndicadores().size(); i++) 
				{	
					if (analisis.getIndicadores().get(i).getIndicador().getNombre().equals(tramos[0].substring(1))) 
					{
						if ((analisis.getIndicadores().get(i).getResultadoEjecucion()!=null) && analisis.getIndicadores().get(i).getResultadoEjecucion().size()>0) {					
							String columna = tramos[1];
							Object valParam = new Object();
							Object[] linea = analisis.getIndicadores().get(i).getResultadoEjecucion().elementAt(0);
							int c = 0;
							while (c < analisis.getIndicadores().get(i).getIndicador().getResultado().length ) {
								if (columna.equals(analisis.getIndicadores().get(i).getIndicador().getResultado()[c])) {
									valParam = linea[c];
									break;
								}
								c++;							}
							eventoPrx.getEvento().getParametros().get(p).setValor(valParam.toString());					
						}
					}
				}
			}
		}
	}
	
}