package biactiva.services.principal.peticiones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import biactiva.services.fileHandler.CSVHandler;
import biactiva.services.fileHandler.ConstantesGenerales;
import biactiva.services.fileHandler.DirectorioManager;

public class EliminarGenerico extends AbstractHTTP {
	
	


	public EliminarGenerico(String url, JSONObject cabecera) {
		super(EPeticiones.DELETE, url, cabecera);
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id,int responseCode) throws Exception{
	    File fichero = DirectorioManager.getDirectorioFechaYHoraInicio(CSVHandler.PATH_BORRADOS_OK);
	    CSVHandler.getInstance().escribirCSV(fichero, "Id eliminado: "+id);
        return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, int responseCode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, String id, int responseCode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}




}
