package biactiva.services.principal.peticiones;

import java.io.BufferedReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;

public class GetExistsFieldInSales extends AbstractHTTP{

	public GetExistsFieldInSales(String url, JSONObject cabecera) {
		super(EPeticiones.GET, url, cabecera);
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		JSONArray jsonArray= this.getJsonArrayDondeIterar("items", in);
		if(jsonArray.size()<1)
			return null;
		return (String)((JSONObject)(jsonArray.get(0))).get("PartyNumber");
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
