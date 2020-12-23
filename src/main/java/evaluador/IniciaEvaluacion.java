package evaluador;

public class IniciaEvaluacion {

	public static void main(String args[]) {
		Evaluador clase = new Evaluador("Indicador1=Indicador2 and Indicador3= Indicador4 AND (Indicador1 > Indicador3 OR (Indicador3 = Indicador7 AND Indicador2 < Indicador4)) AND (Indicador5 = Indicador6 OR Indicador7 < Indicador8)");
		
		
		clase.evaluar();
	}
}
