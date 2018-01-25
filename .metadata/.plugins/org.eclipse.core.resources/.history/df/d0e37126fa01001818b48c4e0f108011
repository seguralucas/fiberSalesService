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
import biactiva.services.singletons.RecEntAct;
import biactiva.services.util.json.JsonUtils;

public class UpdateGenericoRightNow extends AbstractHTTP{




	public UpdateGenericoRightNow(EPeticiones peticion, String url, JSONObject cabecera) {
		super(peticion, url, cabecera);
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, String id, int responseCode)
			throws Exception {
	    File fichero = DirectorioManager.getDirectorioFechaYHoraInicio(json.getConfEntidadPart(), CSVHandler.PATH_UPDATES_OK);
        super.escribirFicheroResultadoPeticion(fichero, json, id);
        return JsonUtils.convertir(in,true);		
	}


	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, AbstractJsonRestEstructura json, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
