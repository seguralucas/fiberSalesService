package biactiva.services.ejecutores;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import biactiva.services.principal.peticiones.EPeticiones;
import biactiva.services.principal.peticiones.GetExistsFieldInSales;
import biactiva.services.principal.peticiones.GetGenerico;
import biactiva.services.principal.peticiones.GetGenericoJson;
import biactiva.services.principal.peticiones.PostGenerico;
import biactiva.services.principal.peticiones.UpdateGenericoRightNow;
import biactiva.services.singletons.ConfiguracionEntidadParticular;
import biactiva.services.singletons.RecEntAct;
import biactiva.services.singletons.RecuperadorPropierdadesJson;
import biactiva.services.util.json.JsonUtils;

public class EjecutarContactosServiceASales implements IEjecutar{

	@Override
	public void procesar(AbstractJsonRestEstructura resultadoMapeo, JSONObject jsonActual) throws Exception {
		ConfiguracionEntidadParticular r=RecEntAct.getInstance().getCep();
		Long id= (Long)jsonActual.get("id");
		this.getMailService(id,resultadoMapeo.getJson());
		this.getPhonesService(id,resultadoMapeo.getJson());
		String urlFianl=r.getUrlVerificarExistencia(jsonActual);
		GetExistsFieldInSales getContactosEnSales = new GetExistsFieldInSales(urlFianl, r.getCabeceraInsertar());//Puede haber m�s de un contacto con el mismo DNI pero distinto contact_type
		String idSales=(String)getContactosEnSales.realizarPeticion();
		addOrganization(jsonActual, resultadoMapeo);
		System.out.println(resultadoMapeo);
			if(idSales!=null){
				System.out.println("Actualizar... "+idSales);
				UpdateGenericoRightNow update = new UpdateGenericoRightNow(r.getUrlInsertar(), r.getCabeceraInsertar());
				update.realizarPeticion(idSales,resultadoMapeo);
			}
			else{
				System.out.println("insertar");
				PostGenerico post = new PostGenerico(r.getUrlInsertar(), r.getCabeceraInsertar());
				post.realizarPeticion(resultadoMapeo);

			}
			System.out.println("***********************************************");
	
	}
		
	private void addOrganization(JSONObject jsonActual, JSONObject jsonResultadoMapeo) throws Exception{
		try{
			ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
			RecuperadorPropierdadesJson rpj = r.getRecuperadorPropiedadesJson();
			JSONObject mapeador = rpj.getMapearOrganizacion("organization");
			JSONObject organization=(JSONObject)jsonActual.get("organization");
			JSONArray organizationLinks=(JSONArray)organization.get("links");
			String urlExtraer=(String)((JSONObject)organizationLinks.get(0)).get("href");
			GetGenericoJson getJson= new GetGenericoJson(urlExtraer, r.getCabeceraExtraer());
			JSONObject json=(JSONObject) getJson.realizarPeticion();
			String CUIT=(String)((JSONObject)(((JSONObject)json.get("customFields")).get("CO"))).get("CUIL_CUIT");
			String urlInsertar= mapeador.get("urlInsertar").toString().replaceAll(r.getIdentificadorAtributo()+"CLAVE"+r.getIdentificadorAtributo(), CUIT);
			GetGenerico get= new GetGenerico( urlInsertar, r.getCabeceraInsertar());
			JSONArray jsonArray= (JSONArray) get.realizarPeticion();
			Long idOrganizacionSales= (Long)((JSONObject)jsonArray.get(0)).get("PartyId");
			jsonResultadoMapeo.put("AccountPartyId", idOrganizacionSales);	
		}catch(Exception e){
			jsonResultadoMapeo.put("AccountPartyId",null);
		}
	}
		private void getMailService(Long idContactoService, JSONObject jsonContactoAInsertar){
			ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
			GetGenerico getListaEmails = new GetGenerico( r.getUrlExtraer()+"/"+idContactoService+"/emails", r.getCabeceraExtraer());
			JSONArray jsonEmails = (JSONArray) getListaEmails.realizarPeticion();
			for(int i=0;i<jsonEmails.size();i++){
				JSONObject jsonObj=(JSONObject)jsonEmails.get(i);
				String urlEmail=jsonObj.get("href").toString();
				String idMail=String.valueOf(urlEmail.charAt(urlEmail.length()-1));
				if(idMail.equals("0")){
					JSONObject jsonMailParticular=(JSONObject)getListaEmails.realizarPeticion(idMail);
					String mail=(jsonMailParticular.get("address").toString());
					jsonContactoAInsertar.put("EmailAddress", mail);
				}
			}
		}
		
		private void getPhonesService(Long idContactoService, JSONObject jsonContactoAInsertar){
			ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
			GetGenerico getListaPhones = new GetGenerico( r.getUrlExtraer()+"/"+idContactoService+"/phones", r.getCabeceraExtraer());
			JSONArray jsonPhones = (JSONArray) getListaPhones.realizarPeticion();
			for(int i=0;i<jsonPhones.size();i++){
				JSONObject jsonObj=(JSONObject)jsonPhones.get(i);
				String urlPhones=jsonObj.get("href").toString();
				String idPhone=String.valueOf(urlPhones.charAt(urlPhones.length()-1));
				if(idPhone.equals("0") || idPhone.equals("1")){
					JSONObject jsonMailParticular=(JSONObject)getListaPhones.realizarPeticion(idPhone);
					String mail=(jsonMailParticular.get("number").toString());
					jsonContactoAInsertar.put(idPhone.equals("0")?"MobileNumber":"WorkPhoneNumber", mail);
				}
			}
		}
		
		

}


