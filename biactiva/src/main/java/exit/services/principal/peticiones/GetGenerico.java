package exit.services.principal.peticiones;

import java.io.BufferedReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exit.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import exit.services.singletons.RecEntAct;
import exit.services.util.json.JsonUtils;

public class GetGenerico extends AbstractHTTP{

	public GetGenerico(EPeticiones peticion, String url, JSONObject cabecera) {
		super(peticion, url, cabecera);
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id, int responseCode) throws Exception {
		try{
		return JsonUtils.convertir(in);
		}catch(Exception e){
			return JsonUtils.convertirArray(in);
			}
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		return super.getJsonArrayDondeIterar(RecEntAct.getInstance().getCep().getIterarSobre(), in);
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
