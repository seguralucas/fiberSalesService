package exit.services.singletons;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;

import exit.services.fileHandler.CSVHandler;
import exit.services.fileHandler.ConstantesGenerales;
import exit.services.fileHandler.DirectorioManager;
import exit.services.principal.peticiones.EPeticiones;
import exit.services.principal.peticiones.GetExistFieldURLQueryRightNow;
import exit.services.singletons.entidadesARecuperar.IPeticiones;
import exit.services.singletons.entidadesARecuperar.PeticionEntidad;
import exit.services.singletons.entidadesARecuperar.RecuperadorPeticiones;
import exit.services.singletons.entidadesARecuperar.SFTPPropiedades;
import exit.services.util.ManejadorDateAPI;
import exit.services.util.json.JsonUtils;

public class ConfiguracionEntidadParticular {
	public static final String OUTPUT_FILE_DEFAULT="salida.csv";
	public static final String OUTPUT_PATH_DEFAULT=".";
	
	private String outPut;
	private SFTPPropiedades sftpPropiedades=null;
	private int paginaActual;
	private RecuperadorFormato recuperadorFormato;
	private RecuperadorPropierdadesJson recuperadorPropiedadesJson;
	private HashMap<String, String> mapPropiedades;
	private String entidadNombre;

    public ConfiguracionEntidadParticular(String entidadNombre){
    	this.entidadNombre=entidadNombre;
    	paginaActual=1;
    	mapPropiedades=new HashMap<String,String>();

        Properties props = new Properties();
        try{
		props.load(new FileReader(ConstantesGenerales.PATH_CONFIGURACION_ENTIDADES+"/"+entidadNombre+"/ConfiguracionEntidad.properties"));
		for(String key : props.stringPropertyNames()) 
			  mapPropiedades.put(key, props.getProperty(key));
		try{this.recuperadorFormato=new RecuperadorFormato(entidadNombre);}catch(Exception e){}
		try{this.recuperadorPropiedadesJson=new RecuperadorPropierdadesJson(entidadNombre);}catch(Exception e){}
        }
        catch(Exception e){
        	e.printStackTrace();
        	try(FileWriter fw= new FileWriter(DirectorioManager.getDirectorioFechaYHoraInicio("error.txt"))){
				fw.write("Error al recuperar las propierdades del fichero: ConfiguracionGeneral.properties");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
    }
    

	public void mostrarConfiguracion(){
    	System.out.println("Directorio de busqueda: "+this.getPathCSVRegistros());
    	System.out.println("URL Extraer: "+this.getUrlExtraer());
    	System.out.println("Cabecera extraer: "+this.getCabeceraExtraer());
    	System.out.println("URL insertar: "+this.getUrlInsertar());
    	System.out.println("Cabecera insertar: "+this.getCabeceraInsertar());
	System.out.println("Acci�n: "+this.getAction());
    	System.out.println("Nivel de paralelismo: "+this.getNivelParalelismo());
    	System.out.println("Usa Proxy: "+this.getUsaProxy());
    	if(this.getUsaProxy().equalsIgnoreCase("si")){
        	System.out.println("IP Proxy: "+this.getIpProxy());
    		System.out.println("Puerto Proxy: "+this.getPuertoProxy());
    	}  	
    	System.out.println("Identificador de Atributo: "+this.getIdentificadorAtributo());
	}
    	public String getParametrosUrl(){
    		String identificadorAtr=this.getIdentificadorAtributo();		
    		String parametrosUrl=this.getFiltros().replaceAll(identificadorAtr+"LIMIT"+identificadorAtr, String.valueOf(this.getLimit()));
    		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"OFFSET"+identificadorAtr, String.valueOf((this.getPaginaActual()-1)*this.getLimit()));
    		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_DESDE"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaDesdeService() );
    		parametrosUrl=parametrosUrl.replaceAll(identificadorAtr+"FECHA_HASTA"+identificadorAtr, ManejadorDateAPI.getInstance().getFechaHastaService());
    		return parametrosUrl;
    	}

	  private String getValueMap(String key){
	    	return mapPropiedades.get(key);
	    }
	  
		public String getPathCSVRegistros() {
			return getValueMap("pathCSVRegistros");
			
		}
		
		public boolean isCreateEmptyFile(){
			return getValueMap("createEmptyFile")==null?false:getValueMap("createEmptyFile").equalsIgnoreCase("true");
		}

		public String getUrlExtraer() {
			return getValueMap("urlExtraer");
		}
		
		public String getUrlInsertar() {
			return getValueMap("urlInsertar");
		}
		
		public String getSalida(){
			return getValueMap("salida");
		}

		public String getUser(){
			return getValueMap("user");
		}

		public String getPuerto(){
			return getValueMap("puerto");
		}

		public String getKeyFile(){
			return getValueMap("keyFile");
		}

		public String getMetodoPreEjecutor() {
			return getValueMap("metodoPreEjecutor");
		}
		
		public String getUrlVerificarExistencia(JSONObject jsonReemplazo) {
			if(getValueMap("urlVerificarExistencia")==null)
				return null;
			return JsonUtils.reemplazarJsonEnString(getValueMap("urlVerificarExistencia"), jsonReemplazo);
		}

		public String getParametroPreEjecutor() {
			return getValueMap("parametroPreEjecutor");
		}
		
		public String getIterarSobre() {
			return getValueMap("iterarSobre");
		}
		
		public boolean isPaginado() {
			return getValueMap("paaginado")==null?false:getValueMap("paaginado").equalsIgnoreCase("true");
		}
		
		public String getIdIteracion() {
			return getValueMap("idIteracion");
		}
		
		public String getMetodoEjecutor() {
			return getValueMap("metodoEjecutor")==null?"ejecutorGenerico":getValueMap("metodoEjecutor");
		}

		public String getParametroEjecutor() {
			return getValueMap("parametroEjecutor");
		}
		
		public JSONObject getCabeceraExtraer() {
			if(getValueMap("cabeceraExtraer")==null)
				return null;
			try {
				return JsonUtils.convertir(getValueMap("cabeceraExtraer"));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public JSONObject getCabeceraInsertar() {
			if(getValueMap("cabeceraInsertar")==null)
				return null;
			try {
				return JsonUtils.convertir(getValueMap("cabeceraInsertar"));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public String getSeparadorCSV() {
			if(getValueMap("separadorCSV")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getSeparadorCSV();
			return getValueMap("separadorCSV");
		}
		
		public String getSeparadorCSVREGEX() {
			if(getValueMap("separadorCSV")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getSeparadorCSVREGEX();
			return "\\"+getValueMap("separadorCSV");
		}

		public int getNivelParalelismo() {
			if(getValueMap("nivelParalelismo")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getNivelParalelismo();
			return Integer.parseInt(getValueMap("nivelParalelismo"));
		}

		public String getUsaProxy() {
			if(getValueMap("usaProxy")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getUsaProxy();
			return getValueMap("usaProxy");
		}

		public String getIpProxy() {
			if(getValueMap("ipProxy")==null)
				RecuperadorPropiedadesConfiguracionGenerales.getInstance().getIpProxy();
			return getValueMap("ipProxy");
		}

		public String getPuertoProxy() {
			if(getValueMap("puertoProxy")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getPuertoProxy();
			return getValueMap("puertoProxy");
		}
		public String getAction() {
			if(getValueMap("action")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getSeparadorCSV();
			return getValueMap("action");
		}
		
		public Integer getLimit() {
			if(getValueMap("limit")==null)
				return 500;
			return Integer.parseInt(getValueMap("limit"));
		}

		public String getIdentificadorAtributo() {
			if(getValueMap("separadorCSV")==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().getIdentificadorAtributo();
			return getValueMap("identificadorAtributo");
		}
		
		public SFTPPropiedades getSftpPropiedades(){
			if(sftpPropiedades!=null)
				return sftpPropiedades;
			this.sftpPropiedades=new SFTPPropiedades(getValueMap("host"),getValueMap("user"), Integer.parseInt(getValueMap("puerto")), getValueMap("keyFile"));
			return sftpPropiedades;
		}
		
		public EOutputs getOutput(){
			String output=getValueMap("output");
			if(output==null)
				return EOutputs.DIRECTORIO;
			switch(output.toUpperCase()){
			case "SFTP": return EOutputs.SFTP;
			default: return EOutputs.DIRECTORIO;
			}
		}
		
		public String getOutPutPath(){
			return getValueMap("outputPath")==null?ConstantesGenerales.PATH_EJECUCION:getValueMap("outputPath");
		}
		
		public String getFiltros(){
				return getValueMap("filtros");
		}
		
		public int getPaginaActual() {
			return paginaActual;
		}	
		
		public String[] getSubEntidades() {
			if(getValueMap("subEntidades")==null)
				return null;
			return getValueMap("subEntidades").split(",");
		}	
		
		private String completarFechaConCeroAIzquierda(String value){
			return value.length()<2?"0"+value:value;
		}
		
		public String getOutputFile() {
			if(getValueMap("outputFile")==null)
				return OUTPUT_FILE_DEFAULT;
			if(outPut!=null && outPut.length()>0)
				return outPut;
			LocalDateTime ldt= LocalDateTime.now();
			outPut=getValueMap("outputFile");
			outPut=outPut.replaceAll("YY",completarFechaConCeroAIzquierda(String.valueOf(ldt.getYear())));
			outPut=outPut.replaceAll("MM",completarFechaConCeroAIzquierda(String.valueOf(ldt.getMonthValue())));
			outPut=outPut.replaceAll("DD",completarFechaConCeroAIzquierda(String.valueOf(ldt.getDayOfMonth())));
			outPut=outPut.replaceAll("HH",completarFechaConCeroAIzquierda(String.valueOf(ldt.getHour())));
			outPut=outPut.replaceAll("MI",completarFechaConCeroAIzquierda(String.valueOf(ldt.getMinute())));
			outPut=outPut.replaceAll("SS",completarFechaConCeroAIzquierda(String.valueOf(ldt.getMinute())));
			return outPut;

		}	
		
		public void incresePaginaActual() {
			paginaActual++;
		}

		public void setPaginaActual(int paginaActual) {
			this.paginaActual = paginaActual;
		}
		

		



		public RecuperadorFormato getRecuperadorFormato() {
			return recuperadorFormato;
		}
		
		



		public RecuperadorPropierdadesJson getRecuperadorPropiedadesJson() {
			return recuperadorPropiedadesJson;
		}

		
		
		public String getEntidadNombre() {
			return entidadNombre;
		}


	
		public boolean isBorrarDataSetAlFinalizar() {
			String borrarDataSetAlFinalizar=getValueMap("borrarDataSetAlFinalizar");
			if(borrarDataSetAlFinalizar==null)
				return RecuperadorPropiedadesConfiguracionGenerales.getInstance().isBorrarDataSetAlFinalizar();
			return borrarDataSetAlFinalizar.equalsIgnoreCase("true");
		}
}
