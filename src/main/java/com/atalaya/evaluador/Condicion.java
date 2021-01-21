package com.atalaya.evaluador;
// Clase para representar cada una de las condiciones simpres de una condición compleja. 


public class Condicion {

	private Operando operando1;		// Primer operando de la condición
	private Operando operando2;		// Segundo operando de la condición
	private String operador;		// Operador de la condición
	private boolean resultado;		// Resultado de la condición
	private boolean negacion;		// Indica si la condición es negada

	private boolean evaluado;		// Flaga que indica si la condición se ha evaluado ya o no. Es un campo intero, no tiene métodos get y set
	
	public Operando getOperando1() {
		return operando1;
	}
	public void setOperando1(Operando operando1) {
		this.operando1 = operando1;	
	}
	
	public Operando getOperando2() {
		return operando2;
	}
	public void setOperando2(Operando operando2) {
		this.operando2 = operando2;			
	}
	
	public String getOperador() {
		return operador;
	}
	public void setOperador(String operador) {
		this.operador = operador;
	}
	
	public boolean getResultado() {
		return resultado;
	}
	public void setResultado(boolean resultado) {
		this.resultado = resultado;
	}	
	
	public boolean getNegacion() {
		return negacion;
	}
	public void setNegacion(boolean negacion) {
		this.negacion = negacion;
	}	
	
	public void Condicion() {
		this.resultado = false;
		this.negacion = false;
	}
	
	public boolean evalua() {
	boolean result = false;	
	String oper1;
	String oper2;
	int iOper1 = 0;
	int iOper2 = 0;
	boolean numerica;
	int compara;
	
		if (evaluado) {return resultado;  } 
		else {
			// Recupera los resultados de cada uno de los operandos de la condicióna.
			oper1 = operando1.getResultado().toString().trim();
			oper2 = operando2.getResultado().toString().trim();			
			// Convierte ambos resultados a int para decidir si la comparación será numérica o alfabéteica.
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
			if (numerica) {			
				switch (this.operador) {
				case "=":
					if (iOper1 == iOper2) 	{	result = true;		}
				case "<":
					if (iOper1 < iOper2) 	{	result = true;		}
				case ">":
					if (iOper1 > iOper2)	{	result = true;		}
				case ">=":
					if (iOper1 >= iOper2) 	{	result = true;		}
				case "<=":
					if (iOper1 <= iOper2) 	{	result = true;		}
				case "<>":	
					if (iOper1 != iOper2) 	{	result = true;		}
				}		
			} else {
				compara = oper1.compareToIgnoreCase(oper2);
				if (compara < 0 && (this.operador.equals("<") || this.operador.equals("<=") || this.operador.equals("<>"))) {
					result = true; 
				} else {
					if (compara == 0 && (this.operador.equals("=") || this.operador.equals("<=") || this.operador.equals(">="))) {
						result = true;
					} else {
						if (compara > 0 && (this.operador.equals(">") || this.operador.equals(">=") || this.operador.equals("<>"))) {
							result = true;
						}
					}
				}					
			}
			
			evaluado = true;
		}
		if (negacion) 	{result = !result;	}
		return result;
	}
	
}