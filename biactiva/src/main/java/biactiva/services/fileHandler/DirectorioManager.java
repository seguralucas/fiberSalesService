package biactiva.services.fileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import biactiva.services.singletons.ApuntadorDeEntidad;
import biactiva.services.singletons.ConfiguracionEntidadParticular;
import biactiva.services.singletons.EOutputs;
import biactiva.services.singletons.RecEntAct;
import biactiva.services.util.ManejadorFechaYHoraLog;

public class DirectorioManager {
	
	private static final String NOMBRE_TEMP="temp";
	public static void SepararFicheros(File archivo) throws IOException{
		    		String line="";
			    		try(BufferedReader br = new BufferedReader(
			    		         new InputStreamReader(
			    		                 new FileInputStream(archivo), "UTF-8"))){
			    		boolean esPrimeraVez=true;
			    		int i=0;
			    		while ((line = br.readLine()) != null) {
			    			if(esPrimeraVez){
			    				esPrimeraVez=false;
			    				CSVHandler.getInstance().setCabeceraFichero(line);			    									    		
			    			}
			    			else{
			    				CSVHandler.getInstance().escribirCSV(DirectorioManager.getDirectorioFechaYHoraInicioDivision(NOMBRE_TEMP+i+".csv"),line,true);
			    				if(i>=RecEntAct.getInstance().getCep().getNivelParalelismo()-1)
			    					i=0;
			    				else
			    					i++;
			    			}
			    		}
		    		}
			        catch(Exception e){
			        	FileWriter fw = new FileWriter(getDirectorioFechaYHoraInicio("errorLote.txt"));
			        	fw.write(e.getMessage());
			        	fw.close();
			        	throw e;
			        }
	}
	

	private static String getEntidadFecha(ConfiguracionEntidadParticular conf){
		String outputPath=RecEntAct.getInstance().getCep().getOutPutPath();
		if(RecEntAct.getInstance().getCep().getOutput()!=EOutputs.DIRECTORIO)
			outputPath=ConstantesGenerales.PATH_EJECUCION;
		if(conf!=null)
			return outputPath+"/"+conf.getEntidadNombre()+"/"+ManejadorFechaYHoraLog.getFechaYHoraInicio();
		return outputPath+"/"+RecEntAct.getInstance().getCep().getEntidadNombre()+"/"+ManejadorFechaYHoraLog.getFechaYHoraInicio();
	}

	
	public static File getDirectorioFechaYHoraInicio(ConfiguracionEntidadParticular conf, String nombreFichero) throws IOException{
		File file = new File(getEntidadFecha(conf));
		if(!file.exists())
			Files.createDirectories(Paths.get(getEntidadFecha(conf)));
		return new File(getEntidadFecha(conf)+"/"+nombreFichero);
	}
	
	public static File getDirectorioFechaYHoraInicio(String nombreFichero) throws IOException{
		File file = new File(getEntidadFecha(RecEntAct.getInstance().getCep()));
		if(!file.exists())
			Files.createDirectories(Paths.get(getEntidadFecha(RecEntAct.getInstance().getCep())));
		return new File(getEntidadFecha(RecEntAct.getInstance().getCep())+"/"+nombreFichero);
	}
	
	public static String getPathFechaYHoraInicioDivision() throws IOException{
		return getPathFechaYHoraInicioDivision(RecEntAct.getInstance().getCep());
	}
	
	
	public static String getPathFechaYHoraInicioDivision(ConfiguracionEntidadParticular conf) throws IOException{
		File file = new File(getEntidadFecha(conf));
		if(!file.exists())
			Files.createDirectories(Paths.get(getEntidadFecha(conf)));
		file = new File(getEntidadFecha(conf)+"/division");
		if(!file.exists())
			Files.createDirectories(Paths.get(getEntidadFecha(conf)+"/division"));
		return getEntidadFecha(conf)+"/division";
	}
	private static File getDirectorioFechaYHoraInicioDivision(String nombreFichero) throws IOException{
		return new File(getPathFechaYHoraInicioDivision(RecEntAct.getInstance().getCep())+"/"+nombreFichero);
	}
	private static File getDirectorioFechaYHoraInicioDivision(String nombreFichero, ConfiguracionEntidadParticular conf) throws IOException{
		return new File(getPathFechaYHoraInicioDivision(conf)+"/"+nombreFichero);
	}

}
