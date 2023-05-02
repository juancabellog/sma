package cl.dlab.sma.core.gen;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType")
public class QueryType implements Serializable
{
    @XmlAttribute(name = "id")
    protected String id;
    
    @XmlElement(name = "field", required = true)
    protected List<FieldType> field;
    
    private String sql;
 
    public List<FieldType> getField() 
    {
        return this.field;
    }

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return the sql
	 */
	@XmlTransient
	public String getSql()
	{
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql)
	{
		this.sql = sql;
	}
    
}
