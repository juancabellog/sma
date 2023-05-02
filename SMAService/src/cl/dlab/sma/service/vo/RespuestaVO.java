package cl.dlab.sma.service.vo;

import java.util.ArrayList;

public class RespuestaVO<D extends VOBase>
{
	private ArrayList<D> registros;
	
	public RespuestaVO()
	{
		this.registros = new ArrayList<D>();
	}
	public int getNumRegistros()
	{
		return this.registros.size();
	}
	/**
	 * @return the registros
	 */
	public ArrayList<D> getRegistros()
	{
		return registros;
	}
	/**
	 * @param registros the registros to set
	 */
	public void setRegistros(ArrayList<D> registros)
	{
		this.registros = registros;
	}
	
}
