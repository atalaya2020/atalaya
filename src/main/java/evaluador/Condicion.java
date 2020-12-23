package evaluador;

public class Condicion {

	private String operando1;
	private String operando2;
	private String operador;
	private boolean resultado;
	
	public String getOperando1() {
		return operando1;
	}
	public void setOperando1(String operando1) {
		this.operando1 = operando1;
	}
	public String getOperando2() {
		return operando2;
	}
	public void setOperando2(String operando2) {
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
	public boolean evalua() {
		boolean evaluado = false;
		resultado = false;
		return evaluado;
	}
	
	
	
}