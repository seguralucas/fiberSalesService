package exit.services.principal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

import com.csvreader.CsvWriter;

import exit.services.convertidos.csvAJson.AbstractJsonRestEstructura;
import exit.services.excepciones.ExceptionBiactiva;
import exit.services.fileHandler.CSVHandler;
import exit.services.principal.peticiones.AbstractHTTP;
import exit.services.principal.peticiones.EPeticiones;
import exit.services.principal.peticiones.GetExistFieldURLQueryRightNow;
import exit.services.principal.peticiones.GetOracleSales;
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
		String identificadorAtr=r.getIdentificadorAtributo();
		
		
		String parametrosUrl=r.getFiltros().replaceAll(identificadorAtr+"LIMIT"+identificadorAtr, String.valueOf(r.getLimit()));
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"OFFSET"+identificadorAtr, String.valueOf((r.getPaginaActual()-1)*r.getLimit()));
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_DESDE"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaDesdeSales() );
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_HASTA"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaHastaSales());
		AbstractHTTP get= new GetOracleSales(EPeticiones.GET,r.getUrlExtraer(),RecEntAct.getInstance().getCep().getCabeceraExtraer());
		return get.realizarPeticion(null,null,parametrosUrl);		
	}
	
	public Object ejecutorServiceASales(){
		ConfiguracionEntidadParticular r= RecEntAct.getInstance().getCep();
		String identificadorAtr=r.getIdentificadorAtributo();		
		String parametrosUrl=r.getFiltros().replaceAll(identificadorAtr+"LIMIT"+identificadorAtr, String.valueOf(r.getLimit()));
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"OFFSET"+identificadorAtr, String.valueOf((r.getPaginaActual()-1)*r.getLimit()));
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_DESDE"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaDesdeService() );
		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_HASTA"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaHastaService());
		AbstractHTTP get= new GetServiceListaEntidad(EPeticiones.GET,r.getUrlExtraer(),RecEntAct.getInstance().getCep().getCabeceraExtraer());

		return get.realizarPeticion(null,null,parametrosUrl);		
	}
	
	
	
	
	private void escribirExcepcion(Exception e, AbstractJsonRestEstructura jsonEst){
		e.printStackTrace();
		CSVHandler csv= new CSVHandler();
		try {
			csv.escribirErrorException(e.getStackTrace());
			csv.escribirCSV("error_no_espeficado.csv", jsonEst.getLine());
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
			CSVHandler csv= new CSVHandler();
			csv.escribirErrorException(e.getStackTrace());
			throw new ExceptionBiactiva("Error al ejecutar ejecutor");
		} 
	}
}
