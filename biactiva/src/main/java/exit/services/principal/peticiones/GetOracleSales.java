package exit.services.principal.peticiones;

import java.io.BufferedReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exit.services.convertidor.jsonAJson.ConvertirJsonAJson;
import exit.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import exit.services.singletons.ConfiguracionEntidadParticular;
import exit.services.singletons.RecEntAct;
import exit.services.singletons.RecuperadorPropierdadesJson;
import exit.services.util.json.JsonUtils;

public class GetOracleSales extends AbstractHTTP {




	public GetOracleSales(EPeticiones peticion, String url, JSONObject cabecera) {
		super(peticion, url, cabecera);
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, String id, int responseCode) throws Exception {
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, String id, int responseCode) throws Exception {
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, int responseCode) throws Exception {
		ConfiguracionEntidadParticular r=RecEntAct.getInstance().getCep();
		JSONArray jArray= super.getJsonArrayDondeIterar(r.getIterarSobre(), in);
		
		for(int i=0;i<jArray.size();i++){
			ConvertirJsonAJson conv= new ConvertirJsonAJson(r,(JSONObject)jArray.get(i));
			AbstractJsonRestEstructura resultadoContacto=conv.convertir();
			String urlVerificarExistencia=r.getUrlVerificarExistencia((JSONObject)jArray.get(i));
			String id=null;
			if(url!=null){
				GetExistFieldURLQueryRightNow get = new GetExistFieldURLQueryRightNow(EPeticiones.GET,urlVerificarExistencia,r.getCabeceraInsertar());
				id=(String)get.realizarPeticion();
			}
			if(id==null){
				System.out.println("Insertando...");
				PostGenerico post= new PostGenerico(EPeticiones.POST,r.getUrlInsertar(),r.getCabeceraInsertar());
				post.realizarPeticion( resultadoContacto );
			}
			else{
				System.out.println("Actualizando...");
				UpdateGenericoRightNow update= new UpdateGenericoRightNow(EPeticiones.UPDATE,r.getUrlInsertar(),r.getCabeceraInsertar());
				update.realizarPeticion( id, resultadoContacto);
			}
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
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, AbstractJsonRestEstructura json, int responseCode)
			throws Exception {
		return null;
	}

	@Override
	protected Object procesarPeticionOK(BufferedReader in, AbstractJsonRestEstructura json, String id, int responseCode)
			throws Exception {
		return null;
	}

	@Override
	protected Object procesarPeticionError(BufferedReader in, AbstractJsonRestEstructura json, String id,
			int responseCode) throws Exception {
		return null;
	}


	
	

	

}
