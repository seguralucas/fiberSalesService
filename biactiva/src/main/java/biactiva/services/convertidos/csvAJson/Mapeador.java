package biactiva.services.convertidos.csvAJson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import biactiva.services.fileHandler.ConstantesGenerales;
import biactiva.services.singletons.RecEntAct;

public class Mapeador {
	HashMap<String, Properties> mapPropiedadesNombre;
	private static Mapeador instance;
	private Mapeador() {
		this.mapPropiedadesNombre= new HashMap<String,Properties>();
	}
	
	public String getValorMapeado(String archivo, String valor) throws Exception{
		try{
		if(!mapPropiedadesNombre.containsKey(archivo))
			putProperties(archivo, valor);
		Properties p= mapPropiedadesNombre.get(archivo);		
		String resultado=p.getProperty(valor);
		if(resultado==null)
			throw new Exception("No existe mapeo en el archivo "+archivo+" para el valor "+valor);
		return resultado;
		}catch(Exception e){ Exception e2= new Exception("Error al mapear valores"); e2.setStackTrace(e.getStackTrace()); throw e2;}
	}
	
	private void putProperties(String archivo, String valor) throws FileNotFoundException, IOException{
		Properties p=new Properties();
		p.load(new FileReader(ConstantesGenerales.PATH_CONFIGURACION_ENTIDADES+"/"+RecEntAct.getInstance().getCep().getEntidadNombre()+"/"+archivo));
		mapPropiedadesNombre.put(archivo,p );
	}
	
	public static synchronized Mapeador getInstance(){
		if(instance==null)
			instance=new Mapeador();
		return instance;
	}
	
	public static void reinicia(){
		instance=null;
	}
	
}
