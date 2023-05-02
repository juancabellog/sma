package cl.dlab.pid.calidaddelaire;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ImportaDatosSMAJob implements Job
{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			System.out.println("Llama a import datos SMA");
			ImportaDatosSMA.getInstance().importData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error al ejecutar carga de datos:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
}