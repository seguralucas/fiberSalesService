package biactiva.services.ejecutores;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import biactiva.services.principal.peticiones.EPeticiones;
import biactiva.services.principal.peticiones.GetExistFieldURLQueryRightNow;
import biactiva.services.principal.peticiones.GetGenerico;
import biactiva.services.principal.peticiones.PostGenerico;
import biactiva.services.principal.peticiones.UpdateGenericoRightNow;
import biactiva.services.singletons.ConfiguracionEntidadParticular;
import biactiva.services.singletons.RecEntAct;
import biactiva.services.singletons.RecuperadorPropierdadesJson;
import biactiva.services.util.json.JsonUtils;

public class EjecutarContactosSalesAService implements IEjecutar {

	@Override
	public void procesar(AbstractJsonRestEstructura resultadoMapeo, JSONObject jsonActual) throws Exception {
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		String urlFianl=r.getUrlVerificarExistencia(jsonActual);
		String idContacto=null;
		if(urlFianl!=null){
			GetExistFieldURLQueryRightNow get = new GetExistFieldURLQueryRightNow(urlFianl,r.getCabeceraInsertar());
			idContacto=(String)get.realizarPeticion();
		}
		addOrganization(jsonActual,resultadoMapeo);
		if(idContacto==null){
			PostGenerico post= new PostGenerico(r.getUrlInsertar(),r.getCabeceraInsertar());
			System.out.println(resultadoMapeo);
			System.out.println("Insertando");
			post.realizarPeticion( resultadoMapeo );
		}
		else{
			System.out.println(resultadoMapeo);
			System.out.println("Actualizando..");
			resultadoMapeo.deleteUrlEliminar(idContacto);
			UpdateGenericoRightNow update= new UpdateGenericoRightNow( r.getUrlInsertar(),r.getCabeceraInsertar());
			update.realizarPeticion(idContacto, resultadoMapeo);
		}
		System.out.println("************");		
	}
	

	
	private void addOrganization(JSONObject jsonActual, JSONObject jsonResultadoMapeo) throws Exception{
		try{
			ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
			RecuperadorPropierdadesJson rpj = r.getRecuperadorPropiedadesJson();
			JSONObject mapeador = rpj.getMapearOrganizacion("AccountPartyId");
			String accountParyId=jsonActual.get("AccountPartyId").toString();
			String urlExtraer= mapeador.get("urlExtraer").toString().replaceAll(r.getIdentificadorAtributo()+"CLAVE"+r.getIdentificadorAtributo(), accountParyId);
			GetGenerico get= new GetGenerico( urlExtraer, r.getCabeceraExtraer());
			JSONArray jsonArray= (JSONArray) get.realizarPeticion();
			JSONObject json=(JSONObject) jsonArray.get(0);
			String CUIT=json.get("OrganizationDEO_NDocumento_c").toString();
			String urlInsertar= mapeador.get("urlInsertar").toString().replaceAll(r.getIdentificadorAtributo()+"CLAVE"+r.getIdentificadorAtributo(), CUIT);

			get= new GetGenerico(urlInsertar, r.getCabeceraInsertar());
			jsonArray= (JSONArray) get.realizarPeticion();
			json=(JSONObject) jsonArray.get(0);
			jsonResultadoMapeo.put("organization", JsonUtils.convertir("{\"id\":"+json.get("id")+"}"));

		
		}catch(Exception e){
			jsonResultadoMapeo.put("organization",null);
		}
	}

}
