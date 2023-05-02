package cl.dlab.sma.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cl.dlab.sma.util.PropertyUtil;

@SuppressWarnings("serial")
public class InitializeServlet extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(InitializeServlet.class);
	public static String REAL_PATH;
	public static String RESOURCE_PATH;
	public static String DATABASE_TYPE;
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		try
		{
			DATABASE_TYPE = getServletContext().getInitParameter("database-type");
			PropertyUtil.loadSqlCompatibility(DATABASE_TYPE);
		}
		catch(Exception e)
		{
			logger.error("Error al cargar y validar licencia", e);
			return;
		}		
		try
		{
			String path = config.getServletContext().getRealPath("tmp");
			File dir = new File(path);
			for (File f : dir.listFiles())
			{
				f.delete();
			}
			logger.info("Borrado de directorio tmp Exitosamente.. ");
		}
		catch(Exception e)
		{
			logger.error("Error al borrar directorio temporal", e);
		}
		
		REAL_PATH = new File(getServletContext().getRealPath(".")).getPath();
		RESOURCE_PATH = new File(getServletContext().getInitParameter("resource-path")).getPath();
		System.out.println(RESOURCE_PATH);
		PropertyUtil.BASE = REAL_PATH + "/";
		System.out.println("REAL_PATH:" + REAL_PATH);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		System.out.println("doget");
		//super.doGet(req, resp);
		if (req.getParameter("set-user") != null)
		{
			req.getSession().setAttribute("user-connected", req.getParameter("set-user"));
		}
		else if (req.getParameter("get-log") != null)
		{
			String DIR = getServletContext().getRealPath("");
			System.out.println(DIR);
			String fileName = "tmp/" + req.getParameter("get-log") + ".gz";
			String[] cmd = new String[]{DIR + "/generaLog.sh", "-" + req.getParameter("n"), fileName};
			System.out.println(Arrays.toString(cmd));
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			pb.directory(new File(DIR));
			Process process = pb.start();
			try
			{
				process.waitFor();
				resp.getWriter().println("archivo " + fileName + " generado correctamente!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				resp.getWriter().println("se ha generado el siguiente error:" + e.getMessage());
			}
		}
		
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	}

}
