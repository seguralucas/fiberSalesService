package exit.services.principal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.csvreader.CsvWriter;

import exit.services.convertidor.jsonAJson.ConvertirJsonAJson;
import exit.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import exit.services.excepciones.ExceptionBiactiva;
import exit.services.fileHandler.CSVHandler;
import exit.services.principal.peticiones.AbstractHTTP;
import exit.services.principal.peticiones.EPeticiones;
import exit.services.principal.peticiones.GetExistFieldURLQueryRightNow;
import exit.services.principal.peticiones.GetExistsFieldInSales;
import exit.services.principal.peticiones.GetGenerico;
import exit.services.principal.peticiones.GetOracleSales;
import exit.services.principal.peticiones.GetOracleSalesContacts;
import exit.services.principal.peticiones.GetServiceListaEntidad;
import exit.services.principal.peticiones.PostGenerico;
import exit.services.principal.peticiones.UpdateGenericoRightNow;
import exit.services.singletons.ApuntadorDeEntidad;
import exit.services.singletons.ConfiguracionEntidadParticular;
import exit.services.singletons.RecEntAct;
import exit.services.util.ManejadorDateAPI;
import exit.services.util.json.JsonUtils;

public class Ejecutor {
	


	
	public Object ejecutorSalesAService(){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		AbstractHTTP get= new GetOracleSales(EPeticiones.GET,r.getUrlExtraer(),RecEntAct.getInstance().getCep().getCabeceraExtraer());
		return get.realizarPeticion(null,null,r.getParametrosUrl());		
	}
	
	public Object ejecutorSalesAServiceContacts(){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		AbstractHTTP get= new GetOracleSalesContacts(EPeticiones.GET,r.getUrlExtraer(),RecEntAct.getInstance().getCep().getCabeceraExtraer());
		return get.realizarPeticion(null,null,r.getParametrosUrl());		
	}
		
	public Object ejecutorServiceASales(){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		AbstractHTTP get= new GetServiceListaEntidad(EPeticiones.GET,r.getUrlExtraer(),RecEntAct.getInstance().getCep().getCabeceraExtraer());
		return get.realizarPeticion(null,null,r.getParametrosUrl());		
	}
	
	public Object ejecutorServiceASalesContactos() throws Exception{
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		GetGenerico getGenerico= new GetGenerico(EPeticiones.GET, r.getUrlExtraer(), RecEntAct.getInstance().getCep().getCabeceraExtraer());
		JSONArray jsonArray= (JSONArray) getGenerico.realizarPeticion(null,null,r.getParametrosUrl());		
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonActual= (JSONObject)jsonArray.get(i);
			Long id = (Long)((JSONObject)(jsonActual)).get("id");
			JSONObject jsonContaco=(JSONObject)getGenerico.realizarPeticion(String.valueOf(id));
			String url=r.getUrlVerificarExistencia(jsonContaco);
			ConvertirJsonAJson conv= new ConvertirJsonAJson(r,jsonContaco);
			AbstractJsonRestEstructura resultadoContacto=conv.convertir();
			this.getMailService(id,resultadoContacto.getJson());
			this.getPhonesService(id,resultadoContacto.getJson());
			ArrayList<String> listaContactType= getContactsTypes(jsonContaco);
			String urlFianl=r.getUrlVerificarExistencia(jsonContaco);
			GetGenerico getContactosEnSales = new GetGenerico(EPeticiones.GET, urlFianl, r.getCabeceraInsertar());//Puede haber m�s de un contacto con el mismo DNI pero distinto contact_type
			JSONArray contactosEnSales = (JSONArray)getContactosEnSales.realizarPeticion();
			
			for(String contactType: listaContactType){
				String idSales=null;
				for(int j=0;j<contactosEnSales.size();j++){
					JSONObject contactoSalesActual= (JSONObject)contactosEnSales.get(i);
					String contactTypeContactoActualSales= (String)contactoSalesActual.get("PersonDEO_TipoDeContacto_c");
					if(contactTypeContactoActualSales!=null && contactTypeContactoActualSales.equals(contactType)){
						idSales=(String)contactoSalesActual.get("PartyNumber");
					}
				}
				resultadoContacto.put("PersonDEO_TipoDeContacto_c", contactType);
				if(idSales!=null){
					System.out.println("Actualizar... "+idSales);
					System.out.println(resultadoContacto);
					UpdateGenericoRightNow update = new UpdateGenericoRightNow(EPeticiones.UPDATE, r.getUrlInsertar(), r.getCabeceraInsertar());
					update.realizarPeticion(idSales,resultadoContacto);
				}
				else{
					System.out.println("insertar");
					System.out.println(resultadoContacto);
					PostGenerico post = new PostGenerico(EPeticiones.POST, r.getUrlInsertar(), r.getCabeceraInsertar());
					post.realizarPeticion();

				}
				System.out.println("***********************************************");
				
			}
		}
		return 0;
	}
	
	private ArrayList<String> getContactsTypes(JSONObject jsonContacto){
		ArrayList<String> listaContactType= new ArrayList<String>();
		JSONObject contactType=(JSONObject)jsonContacto.get("contactType");
		Long idContactType;
		JSONObject aux;
		if(contactType!=null){
			idContactType=(Long)contactType.get("id");
			listaContactType.add(mapeoContactTypeServiceSales(idContactType));
		}
		aux=((JSONObject)((JSONObject)jsonContacto.get("customFields")).get("c"));
		for(int i=2;i<=3;i++){
			contactType=(JSONObject)aux.get("contact_type"+i);
			if(contactType!=null){
				idContactType=(Long)contactType.get("id");
				listaContactType.add(mapeoContactTypeServiceSales(idContactType));
			}
		}
		return listaContactType;
	}
	
	private String mapeoContactTypeServiceSales(Long id){
		if(id.equals(1l) || id.equals(16l) || id.equals(21l))
			return "TIPOC1";
		if(id.equals(2l) || id.equals(17l) || id.equals(22l))
			return "TIPOC2";	
		if(id.equals(3l) || id.equals(18l) || id.equals(23l))
			return "TIPOC3";	
		if(id.equals(4l) || id.equals(19l) || id.equals(24l))
			return "TIPOC4";	
		else if(id.equals(5l) || id.equals(20l) || id.equals(25l))
			return "TIPOC5";	
		return null;
	}
	
	private void getMailService(Long idContactoService, JSONObject jsonContactoAInsertar){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		GetGenerico getListaEmails = new GetGenerico(EPeticiones.GET, r.getUrlExtraer()+"/"+idContactoService+"/emails", r.getCabeceraExtraer());
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
		GetGenerico getListaPhones = new GetGenerico(EPeticiones.GET, r.getUrlExtraer()+"/"+idContactoService+"/phones", r.getCabeceraExtraer());
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
	
	
		
	private void escribirExcepcion(Exception e, AbstractJsonRestEstructura jsonEst){
		e.printStackTrace();
		try {
			CSVHandler.getInstance().escribirErrorException(e);
			CSVHandler.getInstance().escribirCSV("error_no_espeficado.csv", jsonEst.getLine());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public Object ejecutar(String nombreMetodo,AbstractJsonRestEstructura jsonEst) throws ExceptionBiactiva{
		ApuntadorDeEntidad.getInstance().siguienteEntidad();
		return ejecutar(nombreMetodo,null,jsonEst);
	}
	
	public Object ejecutar(String nombreMetodo, String parametros) throws ExceptionBiactiva{
		return ejecutar(nombreMetodo,parametros,null);
	}
	
	public Object ejecutar(String nombreMetodo, String parametros, AbstractJsonRestEstructura jsonEst) throws ExceptionBiactiva{
		Class<Ejecutor> a= Ejecutor.class;
		try {
			Method m;
			Object o;
			if(parametros!=null){
				if(jsonEst!=null){
					m= a.getMethod(nombreMetodo, parametros.getClass(),AbstractJsonRestEstructura.class);
					o=m.invoke(this,parametros,jsonEst);
				}
				else{
					m= a.getMethod(nombreMetodo, parametros.getClass());
					o=m.invoke(this,parametros);				}
			}
			else{
				if(jsonEst!=null){
					m=a.getMethod(nombreMetodo,AbstractJsonRestEstructura.class);
					o=m.invoke(this,jsonEst);
				}
				else{
					m=a.getMethod(nombreMetodo);
					o=m.invoke(this);
				}
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
			CSVHandler.getInstance().escribirErrorException(e);
			throw new ExceptionBiactiva("Error al ejecutar ejecutor");
		} 
	}
}
