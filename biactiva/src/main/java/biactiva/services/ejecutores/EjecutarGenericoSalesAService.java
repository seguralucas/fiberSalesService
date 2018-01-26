package biactiva.services.ejecutores;

import org.json.simple.JSONObject;

import biactiva.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import biactiva.services.principal.peticiones.EPeticiones;
import biactiva.services.principal.peticiones.GetExistFieldURLQueryRightNow;
import biactiva.services.principal.peticiones.GetExistsFieldInSales;
import biactiva.services.principal.peticiones.PostGenerico;
import biactiva.services.principal.peticiones.UpdateGenericoRightNow;
import biactiva.services.singletons.ConfiguracionEntidadParticular;
import biactiva.services.singletons.RecEntAct;

public class EjecutarGenericoSalesAService implements IEjecutar{
	
	public void procesar(AbstractJsonRestEstructura resultadoMapeo, JSONObject jsonActual){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		String urlVerificarExistencia=r.getUrlVerificarExistencia(jsonActual);
		String id=null;
		if(urlVerificarExistencia!=null){
			GetExistFieldURLQueryRightNow getContactosEnSales = new GetExistFieldURLQueryRightNow(urlVerificarExistencia, r.getCabeceraInsertar());//Puede haber m�s de un contacto con el mismo DNI pero distinto contact_type
			id=(String)getContactosEnSales.realizarPeticion();
		}
		System.out.println(resultadoMapeo);
		if(id==null){
			System.out.println("Insertando...");
			PostGenerico post= new PostGenerico(r.getUrlInsertar(),r.getCabeceraInsertar());
			post.realizarPeticion( resultadoMapeo );
		}
		else{
			System.out.println("Actualizando...");
			UpdateGenericoRightNow update= new UpdateGenericoRightNow(r.getUrlInsertar(),r.getCabeceraInsertar());
			update.realizarPeticion( id, resultadoMapeo);
		}
	}
}
