package biactiva.services.principal.peticiones;

import java.io.BufferedReader;

import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import biactiva.services.util.json.JsonUtils;

/****
 * Devuelve un json si se le pasa /entidad o /entidad/id
 * @author GAS
 *
 */
public class GetGenericoJson extends AbstractHTTP{

	public GetGenericoJson(String url, JSONObject cabecera) {
		super(EPeticiones.GET, url, cabecera);
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
		try{
		return JsonUtils.convertir(in);
		}catch(Exception e){
			return JsonUtils.convertirArray(in);
			}
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
