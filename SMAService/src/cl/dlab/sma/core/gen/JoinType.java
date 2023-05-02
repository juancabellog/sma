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
@XmlType(name = "JoinType")
public class JoinType implements Serializable 
{

    @XmlElement(name = "field", required = true)
    protected List<FieldType> field;

    @XmlAttribute(name = "name")
    protected String name;
    
    @XmlAttribute(name = "tableName")
    protected String tableName;
    
    @XmlTransient
    protected String alias;
    @XmlTransient
    protected String pref;
    @XmlTransient
    protected boolean used;

	/**
	 * @return the field
	 */
	public List<FieldType> getField()
	{
		return field;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}


}
