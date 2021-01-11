package evaluador;
// Clase para representar cada una de las condiciones simpres de una condición compleja. 


public class Condicion {

	private String operando1;		// Primer operando de la condición
	private String operando2;		// Segundo operando de la condición
	private String operador;		// Operador de la condición
	private boolean resultado;		// Resultado de la condición
	private boolean negacion;		// Indica si la condición es negada
	private boolean negOperando1;	// Indica si operando1 es negado
	private boolean negOperando2;	// Indica si operando2 es negado
	private boolean evaluado;		// Flaga que indica si la condición se ha evaluado ya o no. Es un campo intero, no tiene métodos get y set
	
	public String getOperando1() {
		return operando1;
	}
	public void setOperando1(String operando1) {
		this.negOperando1 = operandoNegado(operando1);
		this.operando1 = operando1.replaceAll("NOT ", "").trim();	
	}
	public String getOperando2() {
		return operando2;
	}
	public void setOperando2(String operando2) {
		this.negOperando2 = operandoNegado(operando2);
		this.operando2 = operando2.replaceAll("NOT ", "").trim();			
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
	public boolean getNegOperando1() {
		return negOperando1;
	}
	public void setNegOperando1(boolean negacion) {
		this.negOperando1 = negacion;
	}		
	public boolean getNegOperando2() {
		return negOperando2;
	}
	public void setNegOperando2(boolean negacion) {
		this.negOperando2 = negacion;
	}
	
	public void Condicion() {
		this.resultado = false;
		this.negacion = false;
		this.negOperando1 = false;
		this.negOperando2 = false;
	}
	
	public boolean evalua() {
	boolean result = true;	
		
		if (evaluado) {return resultado;  } 
		else {
			result = false;
		}
		if (negacion) 	{result = !result;	}
		return result;
	}
	
	private boolean operandoNegado(String operando) {
		boolean negado = false;
		if (operando.startsWith("NOT")) {
			negado = true;
		}		
		return negado;
	}
	
}