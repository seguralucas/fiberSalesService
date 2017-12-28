package exit.services.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import exit.services.fileHandler.ConstantesGenerales;

public class ManejadorDateAPI {
	
	private static ManejadorDateAPI instance;
	private String fechaDesde;
	private String fechaHasta;
	private File f= new File(ConstantesGenerales.PATH_HORA_ULTIMA_EJECUCION);

	
	private ManejadorDateAPI(){
		String line;
		try(BufferedReader br = new BufferedReader(new FileReader(f)) ) {			
			if( (line=br.readLine())!=null){
				setFechaDesde(line);
				initFechaHasta();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public synchronized static ManejadorDateAPI getInstance(){
		if(null == instance){
			instance = new ManejadorDateAPI();
		}
		return instance;
	}

	public String getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(String fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public String getFechaHasta() {
		return fechaHasta;
	}
	
	public void saveNewDate(){
		String line;
		try(FileWriter fw= new FileWriter(f,false)) {			
			fw.write(getFechaHasta());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initFechaHasta() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();
		this.fechaHasta=dateFormat.format(date)+"T"+dateFormat2.format(date)+"%2B00:00";

	}
	
	
	
}