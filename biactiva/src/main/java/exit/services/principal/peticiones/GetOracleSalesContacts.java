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

public class GetOracleSalesContacts extends AbstractHTTP {




	public GetOracleSalesContacts(EPeticiones peticion, String url, JSONObject cabecera) {
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
			JSONObject jsonActual=(JSONObject)jArray.get(i);
			ConvertirJsonAJson conv= new ConvertirJsonAJson(r,jsonActual);
			AbstractJsonRestEstructura resultadoContacto=conv.convertir();
			String urlFianl=r.getUrlVerificarExistencia(jsonActual);
			String idContacto=null;
			if(urlFianl!=null){
				GetExistFieldURLQueryRightNow get = new GetExistFieldURLQueryRightNow(EPeticiones.GET,urlFianl,r.getCabeceraInsertar());
				idContacto=(String)get.realizarPeticion();
			}
			String tipoContacto=(String)jsonActual.get("PersonDEO_TipoDeContacto_c");
			if(idContacto==null){
				PostGenerico post= new PostGenerico(EPeticiones.POST,r.getUrlInsertar(),r.getCabeceraInsertar());
				addContactType(tipoContacto,null,resultadoContacto);
				System.out.println(resultadoContacto);
				System.out.println("Insertando");
				post.realizarPeticion( resultadoContacto );
			}
			else{
				GetGenerico getGenerico = new GetGenerico(EPeticiones.GET, r.getUrlInsertar(), r.getCabeceraInsertar());
				JSONObject jsonContactoService=(JSONObject)getGenerico.realizarPeticion(idContacto);
				addContactType(tipoContacto,jsonContactoService,resultadoContacto);
				System.out.println(resultadoContacto);
				System.out.println("Actualizando..");
				UpdateGenericoRightNow update= new UpdateGenericoRightNow(EPeticiones.UPDATE, r.getUrlInsertar(),r.getCabeceraInsertar());
				update.realizarPeticion(idContacto, resultadoContacto);
				
			}
			System.out.println("************");
		}
		
		return jArray.size();
	}
		
	private void addContactType(String tipoContacto, JSONObject jsonContacto, JSONObject jsonContactoAInsertar) throws Exception{
		if(tipoContacto==null)
			return ;
		if(jsonContacto==null){
			jsonContactoAInsertar.put("contactType", mapeoContactType(tipoContacto,1));
			return;
		}
		JSONObject contactType=(JSONObject)jsonContacto.get("contactType");
		if(contactType==null){
			jsonContactoAInsertar.put("contactType",  mapeoContactType(tipoContacto,1));
			return ;
		}
		if(mapeoContactTypeSalesAService(tipoContacto,0).equals(((Long)contactType.get("id"))))
			return ;
		
		JSONObject jsonCustomFields=(JSONObject)jsonContacto.get("customFields");
		if(jsonCustomFields==null)
			return;
		JSONObject jsonCO=(JSONObject)jsonCustomFields.get("c");
		if(jsonCO==null)
			return;
		for(int i=2;i<=3;i++){		
			contactType=(JSONObject)jsonCO.get("contact_type"+i);
			if(contactType==null){
				jsonContactoAInsertar.put("customFields", mapeoContactType(tipoContacto,i));
				return ;
			}
			if(mapeoContactTypeSalesAService(tipoContacto,i).equals(((Long)contactType.get("id"))))
				return ;
		}

	}
	public static void main(String[] args) throws Exception {
		GetOracleSalesContacts get = new GetOracleSalesContacts(EPeticiones.GET, "aa", null);
		JSONObject a=JsonUtils.convertir("{\"contactType\":{\"id\":2},\"customFields\":{\"c\":{\"contact_type2\":{\"id\":19} ,\"contact_type3\":null}}}");
		JSONObject b= JsonUtils.convertir("{}");
//		JSONObject b=JsonUtils.convertir("{\"customFields\":{\"CO\":{\"contact_type1\":{\"id\":2}}}}");
		get.addContactType("TIPOC4",a,b);
		System.out.println(b);
	}
	
	private JSONObject mapeoContactType(String contactType, int nroContactType) throws Exception{
		if(nroContactType == 1){
			JSONObject informacionContactType= JsonUtils.convertir("{\"id\":"+mapeoContactTypeSalesAService(contactType,nroContactType)+"}");
			return informacionContactType;
		}
		JSONObject aux= new JSONObject();
		JSONObject aux2= new JSONObject();
		JSONObject informacionContactType;
		informacionContactType= JsonUtils.convertir("{\"id\":"+mapeoContactTypeSalesAService(contactType,nroContactType)+"}");
		aux.put("contact_type"+nroContactType,informacionContactType );
		aux2.put("c",aux );
		return aux2;
	}
	
	private Long mapeoContactTypeSalesAService(String contactType, int nroContactType){
		if(nroContactType==1){
			if(contactType.equalsIgnoreCase("TIPOC1"))
				return 1L;
			else if(contactType.equalsIgnoreCase("TIPOC2"))
				return 2L;
			else if(contactType.equalsIgnoreCase("TIPOC3"))
				return 3L;
			else if(contactType.equalsIgnoreCase("TIPOC4"))
				return 4L;
			else if(contactType.equalsIgnoreCase("TIPOC5"))
				return 5L;
		}
		else if(nroContactType == 2){
			if(contactType.equalsIgnoreCase("TIPOC1"))
				return 16L;
			else if(contactType.equalsIgnoreCase("TIPOC2"))
				return 17L;
			else if(contactType.equalsIgnoreCase("TIPOC3"))
				return 18L;
			else if(contactType.equalsIgnoreCase("TIPOC4"))
				return 19L;
			else if(contactType.equalsIgnoreCase("TIPOC5"))
				return 20L;			
		}
		else{
			if(contactType.equalsIgnoreCase("TIPOC1"))
				return 21L;
			else if(contactType.equalsIgnoreCase("TIPOC2"))
				return 22L;
			else if(contactType.equalsIgnoreCase("TIPOC3"))
				return 23L;
			else if(contactType.equalsIgnoreCase("TIPOC4"))
				return 24L;
			else if(contactType.equalsIgnoreCase("TIPOC5"))
				return 25L;
		}
		return null;
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
