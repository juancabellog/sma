package cl.dlab.sma.core.gen;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import cl.dlab.sma.util.PropertyUtil;



/**
 * <p>Java class for EntityType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EntityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Field" type="{http://www.example.org/Entity}FieldType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Entity")

public class EntityType implements Serializable
{

	@XmlElement(name = "field", required = true)
    protected List<FieldType> field;

    @XmlElement(name = "join", required = false)
    protected List<JoinType> join;

    @XmlElement(name = "query")
    protected List<QueryType> query;

    @XmlElement(name = "sql")
    protected String sql;

    @XmlAttribute(name = "tableName")
    protected String tableName;

    @XmlAttribute(name = "sqlName")
    protected String sqlName;

    @XmlAttribute(name = "procedureName")
    protected String procedureName;

    protected List<FieldType> keys;
    protected List<FieldType> akeys;
    protected List<FieldType> dkeys;

    private String selectAll;
    private String selectId;
    private String select;
    private String update;
    private String delete;
    private String deleteAll;
    private String lock;
    private String unlock;
    private String insert;
    private String selectMaxId;
    private HashMap<String, QueryType> hsQuerys;
    private long lastTime;
    private ArrayList<FieldType> sqlFields;

    public EntityType()
    {
    	field = new ArrayList<FieldType>();
    	query = new ArrayList<QueryType>();
    	keys = new ArrayList<FieldType>();
    	akeys = new ArrayList<FieldType>();
    	dkeys = new ArrayList<FieldType>();
    	join = new ArrayList<JoinType>();
    	hsQuerys = new HashMap<String, QueryType>();
    }
    /**
     * Gets the value of the field property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldType }
     *
     *
     */
    public List<FieldType> getField()
    {
        return this.field;
    }

    public List<JoinType> getJoin()
    {
        return this.join;
    }

    public List<QueryType> getQuery()
    {
        return this.query;
    }

    public QueryType getQueryById(String id)
    {
    	return hsQuerys.get(id);
    }
	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @return the select
	 */
	@XmlTransient
	public String getSelect()
	{
		return select;
	}


	/**
	 * @return the update
	 */
	@XmlTransient
	public String getUpdateSql()
	{
		return update;
	}

	/**
	 * @return the delete
	 */
	@XmlTransient
	public String getDeleteSql()
	{
		return delete;
	}


	/**
	 * @return the insert
	 */
	@XmlTransient
	public String getInsertSql()
	{
		return insert;
	}

	/**
	 * @return the selectAll
	 */
	@XmlTransient
	public String getSelectAll()
	{
		return selectAll;
	}

	/**
	 * @return the selectId
	 */
	@XmlTransient
	public String getSelectId()
	{
		return selectId;
	}

	/**
	 * @return the lastTime
	 */
	@XmlTransient
	public long getLastTime()
	{
		return lastTime;
	}
	/**
	 * @param lastTime the lastTime to set
	 */
	public void setLastTime(long lastTime)
	{
		this.lastTime = lastTime;
	}

	/**
	 * @return the keys
	 */
	@XmlTransient
	public List<FieldType> getKeys()
	{
		return keys;
	}
	/**
	 * @return the akeys
	 */
	@XmlTransient
	public List<FieldType> getAkeys()
	{
		return akeys;
	}
	@XmlTransient
	public List<FieldType> getDkeys()
	{
		return dkeys;
	}

	private void loadQuerys()
	{
		String and = "";
		String andAll = "";
		String comma = "";
		String commaUpd = "";
		String commaIns = "";
		char id = 'a';
		String alias = " " + id;
		String pref = id + ".";

		HashMap<String, JoinType> hsJoin = new HashMap<String, JoinType>();
		for (int i = 0; i < this.join.size(); i++)
		{
			JoinType join = this.join.get(i);
			char c = (char)(id + i + 1);
			join.alias = " " + c;
			join.pref = c + ".";
			hsJoin.put(join.name, join);
		}

		FieldType fMaxId = null;
		System.out.println("enti:" + getTableName());
		for (FieldType f : field)
		{
			System.out.println(f.getProperty());
			if (f.getKey())
			{
				if (!f.getAkey())
				{
					if (f.getType().equals(Type.Integer) || f.getType().equals(Type.Long) || f.getType().equals(Type.Date) || f.getType().equals(Type.Timestamp) || f.getType().equals(Type.Timestamp4))
					{
						if (fMaxId != null)
						{
							fMaxId = null;
							break;
						}
						else
						{
							fMaxId = f;
						}
					}
					else
					{
						fMaxId = null;
						break;
					}
				}
			}
		}


		StringBuilder select = new StringBuilder("select ");
		StringBuilder update = new StringBuilder("update ").append(tableName).append(" set ");
		StringBuilder delete = new StringBuilder("delete from ").append(tableName);
		StringBuilder deleteAll = new StringBuilder("delete from ").append(tableName);
		StringBuilder insert = new StringBuilder("insert into ").append(tableName).append(" (");
		StringBuilder selectMaxId = fMaxId == null ? null : new StringBuilder("select max(").append(fMaxId.getSqlName()).append(") from ").append(tableName);
		StringBuilder insertValues = new StringBuilder();
		StringBuilder keys = new StringBuilder();
		StringBuilder deleteUpdatekeys = new StringBuilder();
		StringBuilder deleteakeys = new StringBuilder();
		StringBuilder akeys = new StringBuilder();
		ArrayList<FieldType> sorts = new ArrayList<FieldType>();
		System.out.println("Entity:" + getTableName());
		for (FieldType f : field)
		{
			if (f.getSort() != null)
			{
				System.out.println("sort:" + f.getProperty());
				sorts.add(f);
			}
			boolean dKey = true;
			if (f.getDkey() != null && !f.getDkey())
			{
				dKey = false;
			}
			if (f.getType() != Type.Join && f.getType() != Type.Function)
			{
				if (f.getKey())
				{
					this.keys.add(f);
					keys.append(and);
					keys.append(pref);
					keys.append(f.getSqlName());
					keys.append(" = ? ");

					deleteUpdatekeys.append(and);
					deleteUpdatekeys.append(f.getSqlName());
					deleteUpdatekeys.append(" = ? ");

					and = " and ";
				}
				else
				{
					update.append(commaUpd);
					//update.append(pref);
					update.append(f.getSqlName());
					update.append(" = ? ");
					commaUpd = ", ";
				}
				if (f.getAkey())
				{
					this.akeys.add(f);
					akeys.append(andAll);
					akeys.append(pref);
					akeys.append(f.getSqlName());
					akeys.append(" = ? ");

					if (dKey)
					{
						dkeys.add(f);
						deleteakeys.append(andAll);
						deleteakeys.append(f.getSqlName());
						deleteakeys.append(" = ? ");
					}
					andAll = " and ";
				}
			}
			select.append(comma);
			if (f.getType() == Type.Join)
			{
				JoinType join = hsJoin.get(f.getJoinRef());
				join.used = true;
				select.append(join.pref);
				select.append(f.getSqlNameRef());
			}
			else if (f.getType() == Type.Function)
			{
				select.append(f.getSqlName());
			}
			else
			{
				select.append(pref);
				select.append(f.getSqlName());
			}
			comma = ", ";

			if (f.getType() != Type.Join && !f.getAutoincrement() && f.getType() != Type.Function)
			{
				insert.append(commaIns);
				insert.append(f.getSqlName());

				insertValues.append(commaIns);
				insertValues.append("?");
				commaIns = ", ";
			}
		}
		if (sorts.size() > 1)
		{
			Collections.sort(sorts, new Comparator<FieldType>()
			{
				@Override
				public int compare(FieldType o1, FieldType o2)
				{
					return o1.getSort() - o2.getSort();
				}
			});
		}
		select.append(" from ").append(tableName).append(alias);
		for (JoinType join : this.join)
		{
			if (join.used)
			{
				select.append(" left join ");
				select.append(join.getTableName());
				select.append(join.alias);
				select.append(" on ");
				and = "";
				for (FieldType f : join.field)
				{
					select.append(and);
					if (f.getJoinRef() != null)
					{
						JoinType joinRef = hsJoin.get(f.getJoinRef());
						select.append(joinRef.pref);
					}
					else
					{
						select.append(pref);
					}
					select.append(f.getSqlName());
					select.append(" = ");
					select.append(join.pref);
					select.append(f.getSqlNameRef());
					and = " and ";
				}
			}
		}
		this.selectAll = select.toString();
		if (akeys.length() > 0)
		{
			this.selectAll += " where " + akeys;
			this.deleteAll = deleteAll.append(" where ").append(deleteakeys).toString();
			this.selectMaxId = selectMaxId == null ? null : selectMaxId.append(" where ").append(deleteakeys).toString();
		}
		else
		{
			this.deleteAll = deleteAll.toString();
			this.selectMaxId = selectMaxId == null ? null : selectMaxId.toString();
		}
		if (sorts.size() > 0)
		{
			comma = "";
			StringBuilder buff = new StringBuilder();
			for (FieldType f : sorts)
			{
				buff.append(comma);
				buff.append(pref);
				buff.append(f.getSqlName());
				buff.append(" ");
				buff.append(f.getSortType());
				comma = ", ";
			}
			this.selectAll += " order by " + buff;
		}

		if (this.keys.size() == 1)
		{
			String keyName = this.keys.get(0).getSqlName();
			this.selectId = MessageFormat.format(PropertyUtil.getSqlProperty("SQL-ID-GENERICO"), new Object[]{keyName, tableName});
		}
		else
		{
			this.selectId = "";
		}

		System.out.println("\n--------------------------\nSQLs " + tableName);
		if (this.query.size() > 0)
		{
			for (QueryType queryType : this.query)
			{
				StringBuilder _select = new StringBuilder(select);

				if (queryType.getField() != null)
				{
					_select.append(" where ");
					and = "";
					for (FieldType field : queryType.getField())
					{
						if (field.getSqlName() == null)
						{
							for (FieldType f : this.field)
							{
								if (f.getProperty().equals(field.getProperty()))
								{
									field.assign(f);
								}
							}
						}
						_select.append(and);
						_select.append(pref);
						_select.append(field.getSqlName());
						_select.append(field.getOperator() != null ? " " + field.getOperator().trim() + " " : " = ");
						_select.append(" ?");

						and = " and ";
					}
				}
				if (sorts.size() > 0)
				{
					comma = "";
					StringBuilder buff = new StringBuilder();
					for (FieldType f : sorts)
					{
						buff.append(comma);
						buff.append(pref);
						buff.append(f.getSqlName());
						buff.append(" ");
						buff.append(f.getSortType());
						comma = ", ";
					}
					_select.append(" order by ").append(buff);
				}

				queryType.setSql(_select.toString());
				hsQuerys.put(queryType.getId(), queryType);
				System.out.println(" id:" + queryType.getId() + " sql:" + _select );
			}
		}
		this.lock = MessageFormat.format(PropertyUtil.getSqlProperty("LOCK_TABLE"), new Object[]{"'" + tableName + "'"});
		this.unlock = MessageFormat.format(PropertyUtil.getSqlProperty("UNLOCK_TABLE"), new Object[]{"'" + tableName + "'"});
		this.select = select.append(" where ").append(keys).toString();
		this.update = update.append(" where ").append(deleteUpdatekeys).toString();
		this.delete = delete.append(" where ").append(deleteUpdatekeys).toString();
		this.insert = insert.append(") values (").append(insertValues).append(")").toString();

		//if (getTableName().equals("ficha_cliente"))
		{
			System.out.println("insert:" + this.insert);
			System.out.println("update:" + this.update);
			System.out.println("selectdelete:" + this.delete);
			System.out.println("deleteAll:" + this.deleteAll);
			System.out.println("select:" + this.select);
			System.out.println("selectAll:" + this.selectAll);
			System.out.println("selectId:" + this.selectId);
			System.out.println("lock:" + this.lock);
			System.out.println("unlock:" + this.unlock);
			System.out.println("selectMaxId:" + this.selectMaxId);
			System.out.println("querys:" + hsQuerys);
		}
	}
	private void loadSql()
	{
		System.out.println("\n--------------------------\nSQLs " + sqlName);
		if (this.query.size() > 1)
		{
			throw new RuntimeException("SQLs tienen que tener solo un query, para los campos de consulta:" + this.query.size());
		}
		StringBuilder buff = new StringBuilder(sql);
		QueryType query;
		if (this.query.size() == 0)
		{
			query = new QueryType();
			query.field = new ArrayList<FieldType>();
			this.query.add(query);
		}
		query = this.query.get(0);
		hsQuerys.put(query.getId(), query);
		HashMap<String, FieldType> fields = new HashMap<String, FieldType>();
		if (query.getField() != null)
		{
			for (FieldType field : query.getField())
			{
				fields.put(field.getProperty(), field);
			}
		}
		int index = 0;
		this.sqlFields = new ArrayList<FieldType>();
		while((index = buff.indexOf(":", index)) != -1)
		{
			if (buff.charAt(index + 1) == '%')
			{
				index++;
				continue;
			}
			int index2 = buff.indexOf(" ", index);
			int index3 = buff.indexOf("\n", index);
			index2 = Math.min(index2, index3);
			String key = buff.substring(index + 1, index2);
			buff.replace(index, index2, "?");
			this.sqlFields.add(fields.get(key));
		}
		this.selectAll = buff.toString();
		query.setSql(this.selectAll);
	}
	private void loadProcedure()
	{
		System.out.println("\n--------------------------\nSQLs " + procedureName);
		if (this.query.size() != 1)
		{
			throw new RuntimeException("SQLs tienen que tener solo un query, para los campos de consulta:" + this.query.size());
		}
		StringBuilder buff = new StringBuilder(System.getProperty("CALL-PROCEDURE")).append(" ");
		StringBuilder open = new StringBuilder(System.getProperty("OPEN-PROCEDURE")).append(" ");
		StringBuilder close = new StringBuilder(System.getProperty("CLOSE-PROCEDURE")).append(" ");
		buff.append(procedureName);
		buff.append(open);
		this.sqlFields = new ArrayList<FieldType>();
		String comma = "";
		QueryType query = this.query.get(0);
		hsQuerys.put(query.getId(), query);
		for (FieldType field : query.getField())
		{
			buff.append(comma);
			buff.append("?");
			comma = ", ";
			this.sqlFields.add(field);
		}
		buff.append(close);
		this.selectAll = buff.toString();
	}
	public void init()
	{
		if (tableName != null)
		{
			loadQuerys();
		}
		else if (sqlName != null)
		{
			loadSql();
		}
		else if (procedureName != null)
		{
			loadProcedure();
		}
		System.out.println("selectAll:" + this.selectAll);
	}
	/**
	 * @return the sqlFields
	 */
	public ArrayList<FieldType> getSqlFields()
	{
		return sqlFields;
	}
	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(String selectAll)
	{
		this.selectAll = selectAll;
	}
	/**
	 * @param sqlFields the sqlFields to set
	 */
	public void setSqlFields(ArrayList<FieldType> sqlFields)
	{
		this.sqlFields = sqlFields;
	}
	/**
	 * @return the deleteAll
	 */
	public String getDeleteAll()
	{
		return deleteAll;
	}
	/**
	 * @return the procedureName
	 */
	public String getProcedureName()
	{
		return procedureName;
	}
	/**
	 * @return the lock
	 */
	public String getLock()
	{
		return lock;
	}
	/**
	 * @return the unlock
	 */
	public String getUnlock()
	{
		return unlock;
	}
	/**
	 * @return the selectMaxId
	 */
	public String getSelectMaxId()
	{
		return selectMaxId;
	}

	//select
}
