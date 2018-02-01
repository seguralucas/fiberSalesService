package biactiva.services.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import biactiva.services.fileHandler.ConstantesGenerales;

public class ManejadorDateAPI {
	
	private static ManejadorDateAPI instance;
	private String fechaGuardada;
	private Date fechaHasta;
	private String fechaDesdeSales;
	private String fechaHastaSales;
	private String fechaDesdeService;
	private String fechaHastaService;
	private File f= new File(ConstantesGenerales.PATH_HORA_ULTIMA_EJECUCION);
	private File husoHorario= new File(ConstantesGenerales.PATH_HUSO_HORARIO);
	private SimpleDateFormat dateFormatEnArchivo=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateFormat dateFormatYears = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
	private final long HOURS = 60L * 60L * 1000L;

	private ManejadorDateAPI(){
		String fechaDesde;
		if( (fechaDesde=readFechaDesde())!=null){
			this.fechaGuardada= fechaDesde;
			fechaHasta = new Date();
			fechaHasta = new Date(fechaHasta.getTime() + (readHusoHorario() * HOURS));
			initFechaHastaSales();
			initFechaDesdeService(fechaDesde);
			initFechaDesdeSales(fechaDesde);
			initFechaHastaService();
		}
	}
	
	private Long readHusoHorario(){
		try(BufferedReader br = new BufferedReader(new FileReader(husoHorario)) ) {	
			return Long.parseLong(br.readLine());
		}
		 catch (IOException e) {
				e.printStackTrace();
				return 0L;
			}
	}
	
	private String readFechaDesde(){
		try(BufferedReader br = new BufferedReader(new FileReader(f)) ) {	
			return br.readLine();
		}
		 catch (IOException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	public synchronized static ManejadorDateAPI getInstance(){
		if(null == instance){
			instance = new ManejadorDateAPI();
		}
		return instance;
	}


	
	
	public void saveNewDate(){
		try(FileWriter fw= new FileWriter(f,false)) {
			fw.write(getFechaGuardar());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getFechaGuardar(){
		return dateFormatYears.format(this.fechaHasta)+" "+dateFormatTime.format(this.fechaHasta);
	}
	

	private void initFechaHastaSales() {
		this.fechaHastaSales=dateFormatYears.format(this.fechaHasta)+"T"+dateFormatTime.format(this.fechaHasta)+"%2B00:00";
	}
	
	private void initFechaDesdeSales(String line){
		Date date;
		try {
			date = dateFormatEnArchivo.parse(line);
			this.fechaDesdeSales=dateFormatYears.format(date)+"T"+dateFormatTime.format(date)+"%2B00:00";
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void initFechaHastaService() {
		this.fechaHastaService=dateFormatYears.format(this.fechaHasta)+"T"+dateFormatTime.format(this.fechaHasta)+"Z";
	}
	
	private void initFechaDesdeService(String line){
		Date date;
		try {
			date = dateFormatEnArchivo.parse(line);
			this.fechaDesdeService=dateFormatYears.format(date)+"T"+dateFormatTime.format(date)+"Z";
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		System.out.println(ManejadorDateAPI.getInstance().getFechaDesdeService());
		System.out.println(ManejadorDateAPI.getInstance().getFechaHastaService());
		System.out.println(ManejadorDateAPI.getInstance().getFechaDesdeSales());
		System.out.println(ManejadorDateAPI.getInstance().getFechaHastaSales());
		ManejadorDateAPI.getInstance().saveNewDate();
		
		
	}
	
	public String getFechaDesdeSales() {
		return fechaDesdeSales;
	}

	public void setFechaDesdeSales(String fechaDesde) {
		this.fechaDesdeSales = fechaDesde;
	}

	public String getFechaHastaSales() {
		return fechaHastaSales;
	}
	public void setFechaHastaSales(String fechaDesde) {
		this.fechaHastaSales = fechaDesde;
	}
	
	public String getFechaDesdeService() {
		return fechaDesdeService;
	}

	public void setFechaDesdeService(String fechaDesde) {
		this.fechaDesdeService = fechaDesde;
	}

	public String getFechaHastaService() {
		return fechaHastaService;
	}
	public void setFechaHastaService(String fechaDesde) {
		this.fechaHastaService = fechaDesde;
	}
	
	
}
