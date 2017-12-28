package exit.services.principal.peticiones;

import java.io.BufferedReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exit.services.convertidor.jsonAJson.ConvertirJsonAJson;
import exit.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import exit.services.singletons.ConfiguracionEntidadParticular;
import exit.services.singletons.RecEntAct;
import exit.services.util.json.ConvertidorJson;

public class GetOracleSales extends AbstractHTTP {

	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, String id, int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		ConfiguracionEntidadParticular r=RecEntAct.getInstance().getCep();
		System.out.println(r.getIterarSobre());
		JSONArray jArray;
		if(r.getIterarSobre()!=null){
			JSONObject json= ConvertidorJson.convertir(in);
			jArray=(JSONArray)json.get(r.getIterarSobre());
			
		}
		else
			jArray = ConvertidorJson.convertirArray(in);
		
		for(int i=0;i<jArray.size();i++){
			ConvertirJsonAJson conv= new ConvertirJsonAJson(r,(JSONObject)jArray.get(i));
			JSONObject resultadoContacto=conv.convertir();
		}
		
		return jArray.size();
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, int responseCode) throws Exception {
		System.out.println("Error aca");
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, int responseCode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, AbstractJsonRestEstructura json, int responseCode)
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

	@Override
	protected Object procesarPeticionError(BufferedReader in, AbstractJsonRestEstructura json, String id,
			int responseCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

	}

}