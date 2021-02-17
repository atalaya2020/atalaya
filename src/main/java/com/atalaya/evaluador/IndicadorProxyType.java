package com.atalaya.evaluador;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.modelodatos.Indicador;
import com.modelodatos.Parametro;

public class IndicadorProxyType {
	
	protected static IIndicadorProxyType getInstanceByAliasType(String class_name) throws Exception
	{
		String last_error = "";
		IIndicadorProxyType object_class = null;
		Class<IIndicadorProxyType> ident_class = null;
		boolean isSingleton = false;
		
		if (class_name == null)
		{
			last_error = "No se ha especificado el nombre de la clase a instanciar";
			throw new Exception(last_error);
		};
		
		class_name = "com.atalaya.evaluador.IndicadorProxyType"+class_name;
		
		try
		{
			ident_class = (Class<IIndicadorProxyType>)Class.forName(class_name);
			Method[] factoryMethods = ident_class.getDeclaredMethods();
			
			for (int i=0;i<factoryMethods.length;i++)
			{
				//Se trata de un AliasType tipo singleton
				if (factoryMethods[i].getName().equals("getInstance"))
				{
					isSingleton = true;
					object_class = (IIndicadorProxyType)factoryMethods[i].invoke(null, null);
					break;
				}
			}
			
			if (!isSingleton)
				object_class = (IIndicadorProxyType)ident_class.newInstance();
		
//			singleton = (IAliasType)factoryMethod.invoke(null, null);
			// Intentamos instanciar la clase
//			ident_class = (Class<IAliasType>) Class.forName(class_name);
//			object_class = IAliasType.class.getClass().cast(ident_class);
//			//constructor = objeto.getClass().getConstructor(new Class[]{HashMap.class});
//			//objeto = (IAliasType)constructor.newInstance(class_name);
		}
		catch(ClassNotFoundException cnf)
		{
			cnf.printStackTrace();
			last_error = "No puedo encontrar la clase: (" + class_name + ")";
			throw new Exception(last_error);
		}
		/*catch(InstantiationException in)
		{
			last_error = "No puedo instanciar la clase: (" + class_name + ")";
			throw new Exception(last_error);
		}*/
		/*catch(IllegalAccessException ia)
		{
			last_error = "No puedo acceso a la clase: (" + class_name + ")";
			throw new Exception(last_error);
		}*/	
		catch (SecurityException ese) {
			// TODO Auto-generated catch block
			ese.printStackTrace();
			last_error = "Error de acceso a la clase o metodos de la clase (" + class_name + ")";
			throw new Exception(last_error);
			
		} 
		catch (IllegalArgumentException eia) {
			// TODO Auto-generated catch block
			eia.printStackTrace();
			last_error = "Error de acceso a la clase o metodos de la clase (" + class_name + ")";
			throw new Exception(last_error);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			last_error = "Error de acceso a la clase o metodos de la clase (" + class_name + ")";
			throw new Exception(last_error);
		}
		
		return object_class;
	}
	
	public Indicador copiaIndicador (Indicador ind)
	{
		String nombre = new String(ind.getNombre());
		String descripcion = new String(ind.getDescripcion());
		String fuente = new String(ind.getFuente());
		String tipo = new String(ind.getTipo());
		String comando = new String(ind.getComando());
		
		ArrayList<Parametro> parametros_copia = new ArrayList<Parametro>();
		for (int i=0;i<ind.getParametros().size();i++)
		{
			Parametro param = new Parametro();
			param.setNombre(ind.getParametros().get(i).getNombre());
			param.setTipo(ind.getParametros().get(i).getTipo());
			param.setValor(ind.getParametros().get(i).getValor());
			parametros_copia.add(param);
		}
		String[] resultado = ind.getResultado().clone();
		
		Indicador ind_copia = new Indicador(nombre, descripcion, fuente, tipo, comando, parametros_copia, resultado); 
		
		return ind_copia;
	}

}