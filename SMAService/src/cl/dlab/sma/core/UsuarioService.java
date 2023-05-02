package cl.dlab.sma.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import cl.dlab.sma.core.sql.rad.Usuario;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;
import cl.dlab.sma.service.vo.UsuarioOutputVO;
import cl.dlab.sma.util.RSAEncryptionUtil;

public class UsuarioService extends BaseService
{

	public UsuarioService()
	{
		super();
	}

	public UsuarioService(Connection con)
	{
		super(con);
	}

	public RespuestaVO<UsuarioOutputVO> consultar(InputVO input) throws Exception
	{
		return new Usuario(con, con == null).consultar(input);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> consultar(java.util.HashMap<String, Object> input) throws Exception
	{
		String user = "Admin";//(String)input.get("tmp-usuario");
		HashMap<String, Object> result = new Usuario(con, true).consultar(input);
		if (user.equals("Admin"))
		{
			ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>)result.get("listData");
			for (HashMap<String, Object> item : list)
			{
				item.put("password", new String(RSAEncryptionUtil.decrypt((byte[])item.get("password"))));
			}
			
		}
		return result;
	}

	public void eliminar(java.util.HashMap<String, Object> input) throws Exception
	{
		new Usuario(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input) throws Exception
	{
		String pwd = (String)input.get("password");
		input.put("password", RSAEncryptionUtil.encrypt(((String)input.get("password")).getBytes("UTF-8")));
		new Usuario(con, true).guardar(input);
		input.put("password", pwd);
	}
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> validaUsuario(HashMap<String, Object> input) throws Exception
	{
		Usuario usr = new Usuario(con, false);
		try
		{
			input.put("query_id", "unitary");
			ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>)usr.consultar(input).get("listData");
			if (list.size() == 0)
			{
				throw new Exception("Usuario no encontrado");
			}
			HashMap<String, Object> usuario = list.get(0);
			String password = (String)input.get("password");
			byte[] pwd = RSAEncryptionUtil.decrypt((byte[])usuario.get("password"));
			System.out.println("comentar:" + new String(pwd, "UTF-8") + "**" + password + "**");
			if (!password.equals(new String(pwd, "UTF-8")))
			{
				throw new Exception("La clave ingresada no coincide");
			}
			usuario.put("password", password);
			return usuario;
		}
		finally
		{
			usr.getConnection().close();
		}
	}
	public HashMap<String, Object> changePassword(HashMap<String, Object> user) throws Exception
	{
		Usuario usr = new Usuario(con, true);
		try
		{
			PreparedStatement stmt = usr.getConnection().prepareStatement("update usuario set usr_password = ? where usr_id = ?");
			stmt.setBytes(1, RSAEncryptionUtil.encrypt(((String)user.get("newPassword")).getBytes()));
			stmt.setString(2, (String)user.get("user"));
			stmt.executeUpdate();
			usr.getConnection().commit();
			user.put("password", user.get("newPassword"));
			return user;
		}
		finally
		{
			usr.getConnection().close();
		}
	}

}