package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import cl.dlab.sma.core.sql.rad.HojaDeDatos;
import cl.dlab.sma.core.sql.rad.TipoArchivo;
import cl.dlab.sma.core.sql.rad.ValidacionesNormativas;
import cl.dlab.sma.core.sql.rad.ValidacionesTipoArchivo;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;
import cl.dlab.sma.service.vo.TipoArchivoOutputVO;
import cl.dlab.sma.util.LogUtil;

public class TipoArchivoService extends BaseService {

	public TipoArchivoService() {
		super();
	}

	public TipoArchivoService(Connection con) {
		super(con);
	}

	public RespuestaVO<TipoArchivoOutputVO> consultar(InputVO input)
			throws Exception {
		return new TipoArchivo(con, con == null)
				.consultar(input);
	}

	public HashMap<String, Object> consultar(
			java.util.HashMap<String, Object> input) throws Exception {
		return new TipoArchivo(con, true)
				.consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input) throws Exception {
		TipoArchivo service = new TipoArchivo(con, false);
		try
		{
			ValidacionesTipoArchivo validacionesService = new ValidacionesTipoArchivo(service.getConnection(), false);
			ValidacionesNormativas validacionesNormativasService = new ValidacionesNormativas(service.getConnection(), false);
			HojaDeDatos hojaDatosService = new HojaDeDatos(service.getConnection(), false);
			HashMap<String, Object> hs = new HashMap<String, Object>();
			hs.put("codigoTipoArchivo", input.get("codigo"));
			hojaDatosService.eliminarAll(hs);
			validacionesService.eliminarAll(hs);
			validacionesNormativasService.eliminarAll(hs);
			service.eliminar(input);
			service.getConnection().commit();
		}
		catch(Exception e)
		{
			service.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al eliminar TipoArchivo");
			throw e;
		}
		finally
		{
			service.getConnection().close();
		}		
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consultaDetalleTiposDeArchivo(HashMap<String, Object> input) throws Exception
	{
		HojaDeDatos hojaDatosService = new HojaDeDatos(null, false);
		try
		{
			ValidacionesTipoArchivo validacionesService = new ValidacionesTipoArchivo(hojaDatosService.getConnection(), false);
			ValidacionesNormativas validacionesNormativasService = new ValidacionesNormativas(hojaDatosService.getConnection(), false);
			HashMap<String, Object> result = new HashMap<String, Object>();

			input.put("codigoTipoArchivo", input.get("codigo"));
			ArrayList<HashMap<String, Object>> hojasDeDatos = (ArrayList<HashMap<String, Object>>)hojaDatosService.consultar(input).get("listData");
			for (HashMap<String, Object> hojaDatos : hojasDeDatos) {
				hojaDatos.put("nombreHojaDatos", hojaDatos.get("nombre"));
				hojaDatos.put("validaciones", validacionesService.consultar(hojaDatos).get("listData"));
			}
			result.put("validacionesNormativas", validacionesNormativasService.consultar(input).get("listData"));
			result.put("hojasDeDatos", hojasDeDatos);
			return result;
		}
		finally
		{
			hojaDatosService.getConnection().close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void guardar(java.util.HashMap<String, Object> input) throws Exception {
		TipoArchivo service = new TipoArchivo(con, false);
		try
		{
			ValidacionesTipoArchivo validacionesService = new ValidacionesTipoArchivo(service.getConnection(), false);
			HojaDeDatos hojaDatosService = new HojaDeDatos(service.getConnection(), false);
			ValidacionesNormativas validacionesNormativasService = new ValidacionesNormativas(service.getConnection(), false);
			
			ArrayList<HashMap<String, Object>> hojasDeDatos = (ArrayList<HashMap<String,Object>>)input.get("hojasDeDatos");
			ArrayList<HashMap<String, Object>> validacionesNormativas = (ArrayList<HashMap<String,Object>>)input.get("validacionesNormativas");
			if (!(Boolean)input.get("isNew")) {
				HashMap<String, Object> hs = new HashMap<String, Object>();
				hs.put("codigoTipoArchivo", input.get("codigo"));
				validacionesNormativasService.eliminarAll(hs);
				validacionesService.eliminarAll(hs);
				hojaDatosService.eliminarAll(hs);
			}
			service.guardar(input);
			int i = 0;
			for (HashMap<String, Object> validacion : validacionesNormativas) {

				validacion.put("codigoTipoArchivo", input.get("codigo"));
				validacion.put("isNew", true);
				validacion.put("id", ++i);
				validacionesNormativasService.guardar(validacion);
			}
			for (HashMap<String, Object> hojaDato : hojasDeDatos) {

				hojaDato.put("codigoTipoArchivo", input.get("codigo"));
				hojaDato.put("isNew", true);
				hojaDatosService.guardar(hojaDato);

				ArrayList<HashMap<String, Object>> validaciones = (ArrayList<HashMap<String,Object>>)hojaDato.get("validaciones");
				i = 0;
				for (HashMap<String, Object> item : validaciones) {
					item.put("codigoTipoArchivo", input.get("codigo"));
					item.put("nombreHojaDatos", hojaDato.get("nombre"));
					item.put("id", ++i);
					item.put("isNew", true);
					validacionesService.guardar(item);
				}
				
			}
			service.getConnection().commit();
		}
		catch(Exception e)
		{
			service.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al guardar TipoArchivo");
			throw e;
		}
		finally
		{
			service.getConnection().close();
		}
		
	}
}