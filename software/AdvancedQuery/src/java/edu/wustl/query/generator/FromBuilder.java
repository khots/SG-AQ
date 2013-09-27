package edu.wustl.query.generator;

import static edu.wustl.query.generator.SqlKeyWords.FROM;
import static edu.wustl.query.generator.SqlKeyWords.INNER_JOIN;
import static edu.wustl.query.generator.SqlKeyWords.JOIN_ON;
import static edu.wustl.query.generator.SqlKeyWords.LEFT_JOIN;
import static edu.wustl.query.generator.SqlKeyWords.AND;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.query.queryobject.util.InheritanceUtils;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.util.Utility;
import edu.wustl.query.util.global.AQConstants;
/**
 * Note to human debugger: If an error occurs similar to "column foo_bar is
 * ambiguous" while firing a generated SQL, this is because the column "foo_bar"
 * is present in both the child and parent tables in a TABLE_PER_SUBCLASS
 * hierarchy. See the TODO on the class TblPerSubClass below for the cause and
 * fix.
 */

public class FromBuilder
{
	/**
	 * join graph.
	 */
    private IJoinGraph joinGraph;

    /**
     * root.
     */
    private IExpression root;

    /**
     * length of the alias name.
     */
    private static final int ALIAS_NAME_LENGTH = 26;

    /**
     * from clause.
     */
    private String fromClause;

    /**
     * where clause
     */
    private String whereClause;
    
    /**
     * Parameterized constructor.
     * @param joinGraph joinGraph
     */
    public FromBuilder(IJoinGraph joinGraph)
    {
        if (joinGraph == null)
        {
            this.fromClause = "";
            return;
        }
        this.joinGraph = joinGraph;
        try
        {
            root = joinGraph.getRoot();
        }
        catch (MultipleRootsException e)
        {
            throw new IllegalArgumentException(e);
        }
        buildFrom();
    }

    /**
     * To get the Alias Name for the given IExpression. It will return alias
     * name for the DE entity associated with constraint entity.
     * @param expr expression
     * @return The Alias Name for the given Entity.
     */
    private static String aliasOf(IExpression expr)
    {    	
    	return alias(tableName(entity(expr)), expr);
    }

    /**
     * Get the alias of the string passed.
     * @param string string
     * @param expr expression
     * @return alias
     */
    private static String alias(String string, IExpression expr)
    {
        String stringAlias = Utility.removeSpecialCharactersFromString(string);
        if (stringAlias.length() > ALIAS_NAME_LENGTH)
        {
        	stringAlias = stringAlias.substring(0, ALIAS_NAME_LENGTH);
        }
        return stringAlias + "_" + expr.getExpressionId();
    }

    /**
     * Return the alias of the expression.
     * @param attr attribute
     * @param expr expression
     * @return alias
     */
    protected String aliasOf(AttributeInterface attr, IExpression expr)
    { 
    	attr = origAttr(attr);    
    	return alias(tableName(attr.getEntity()), expr) + "." + columnName(attr);
    }

    /**
     * Returns the generated from clause.
     * @return from clause
     */
    protected String getFromClause()
    {
        return fromClause;
    }

    /**
     * Returns the generated where clause.
     * @return from clause
     */
    protected String getWhereClause()
    {
        return whereClause;
    }
    
    
    /**
     * Builds the from clause.
     * @return from clause
     */
    private void buildFrom()
    {   	 
    	StringBuilder fromClause = new StringBuilder();
    	StringBuilder whereClause = new StringBuilder();
    	
    	Map<String, String> clause = new HashMap<String, String>();
    	getExprSrc(root, clause);
    	
    	fromClause.append(FROM).append(clause.get("from"));
    	if(clause.get("where") != null && !clause.get("where").isEmpty()){
    		whereClause.append(AND).append(clause.get("where"));
    	}

        Set<IExpression> currExprs = new HashSet<IExpression>();
        currExprs.addAll(children(root));

        while (!currExprs.isEmpty())
        {
            Set<IExpression> nextExprs = new HashSet<IExpression>();
            for (IExpression currExpr : currExprs)
            {
            	clause.clear();
            	nextExprs.addAll(children(currExpr));
            	
            	getExprSrc(currExpr, clause);
            	String joinConds = getJoinConds(currExpr);
            	
            	fromClause.append(LEFT_JOIN).append(clause.get("from")).append(JOIN_ON).append(joinConds);
            	
            	if(clause.get("where") != null && !clause.get("where").isEmpty() ){
            		whereClause.append(AND).append(clause.get("where"));
            	}
            }
            currExprs = nextExprs;
        }
        this.fromClause = fromClause.toString();
        this.whereClause = whereClause.toString();
    }
  

    /**
     * Get the join conditions.
     * @param expr expression
     * @return string
     */
    private String getJoinConds(IExpression expr)
    {
        StringBuilder res = new StringBuilder();
        String and = " and ";
        for (IExpression parent : parents(expr))
        {
            res.append(joinCond(parent, expr));
            res.append(and);
        }

        return removeLastOccur(res.toString(), and);
    }

    /**
     * @param leftExpr left expression
     * @param rightExpr right expression
     * @return join condition
     */
    private String joinCond(IExpression leftExpr, IExpression rightExpr)
    {
        AssociationInterface assoc = getAssociation(leftExpr, rightExpr);
        String returnValue;
        
        ConstraintPropertiesInterface assocProps = assoc.getConstraintProperties();
        String leftAttr = assocProps.getSrcEntityConstraintKeyProperties() == null ?
			        		null :assocProps.getSrcEntityConstraintKeyProperties().
			        			getTgtForiegnKeyColumnProperties().getName();
        String rightAttr = assocProps.getTgtEntityConstraintKeyProperties() == null ?
			        		null :assocProps.getTgtEntityConstraintKeyProperties().
			        			getTgtForiegnKeyColumnProperties().getName();
        if (leftAttr != null && rightAttr != null)
        {
            // tricky choice of PK.
        	String leftTab = middleTabAlias(assoc, rightExpr)+ "." + assocProps
        						.getTgtEntityConstraintKeyProperties()
        						.getTgtForiegnKeyColumnProperties().getName();
        	String rightTab = primaryKey(entity(rightExpr), rightExpr);
        	returnValue = equate(leftTab, rightTab);
        }
        else
        {
        	leftAttr = getAppropriateAttribute(assoc.getEntity(), leftAttr, leftExpr);
	        rightAttr = getAppropriateAttribute(assoc.getTargetEntity(), rightAttr, rightExpr);
	        returnValue = equate(leftAttr, rightAttr);
        }
        return returnValue;
    }

    /**
     * @param src source
     * @param tempAttribute attribute
     * @return attribute
     */
    private String getAppropriateAttribute(EntityInterface src, String tempAttribute, IExpression expr)
	{
		String attribute = tempAttribute;
		if (attribute == null) 	{
        	attribute = primaryKey(src, expr);
    	} else {        		
    		attribute = alias(tableName(src), expr) + "." + tempAttribute;
    	}
		return attribute;
	}


    /**
     * @param leftTab left Table
     * @param leftCol left Column
     * @param rightTab right Table
     * @param rightCol right Column
     * @return complete string
     */
    private String equate(String leftAttr, String rightAttr)
    {
		return leftAttr  + "="  + rightAttr;
    }

    /**
     * Get association between left and right association.
     * @param leftExpr left expression
     * @param rightExpr right expression
     * @return association
     */
    private AssociationInterface getAssociation(IExpression leftExpr, IExpression rightExpr)
    {
        IIntraModelAssociation iAssociation = (IIntraModelAssociation)
        joinGraph.getAssociation(leftExpr, rightExpr);
        AssociationInterface assoc = iAssociation.getDynamicExtensionsAssociation();
        return InheritanceUtils.getInstance().getActualAassociation(assoc);
    }

    /**
     * Get list of children.
     * @param expr expression
     * @return list of children
     */
    private List<IExpression> children(IExpression expr)
    {
        return joinGraph.getChildrenList(expr);
    }

    /**
     * Get the list of parent expressions.
     * @param expr expression
     * @return list of parent expressions
     */
    private List<IExpression> parents(IExpression expr)
    {
        return joinGraph.getParentList(expr);
    }

    /**
     * Returns the alias.
     * @param assoc association
     * @param rightExpr right expression
     * @return alias
     */
    private String middleTabAlias(AssociationInterface assoc, IExpression rightExpr)
    {
        return alias(middleTabName(assoc), rightExpr) + "_";
    }

    /**
     * @param expr expression
     * @return string with join condition
     */
    private void getExprSrc(IExpression expr, Map<String, String> clause)
    {
        SrcStringProvider srcStringProvider = getStringProvider(expr);
        srcStringProvider.srcString(expr, clause);
       
        if (expr != root)
        {     	
	        // many-many ??
	        String res = processForManyToMany(expr);
	        if (!"".equals(res))
	        {        	
	        	clause.put("from", res + LEFT_JOIN + clause.get("from"));
	        }
        }        
    }

    /**
     * @param expr expression
     * @return res
     */
	private String processForManyToMany(IExpression expr)
	{
		StringBuffer res = new StringBuffer(80);
        for (IExpression parent : parents(expr))
        {
            AssociationInterface assoc = getAssociation(parent, expr);
            if (manyToMany(assoc))
            {
                ConstraintPropertiesInterface constraintProperty = assoc.getConstraintProperties();
                String middleTabAlias = middleTabAlias(assoc, expr);
                String leftCol = primaryKey(entity(parent), parent);
                String rightCol = middleTabAlias + "."+ constraintProperty
                					.getSrcEntityConstraintKeyProperties()
                					.getTgtForiegnKeyColumnProperties().getName();
                String joinCond = equate(leftCol, rightCol);

                res.append(constraintProperty.getName())
                	.append(' ').append(middleTabAlias).append(JOIN_ON).append(joinCond);
            }
        }
		return res.toString();
	}

    /**
     * @param expr expression
     * @return srcStringProvider
     */
	private SrcStringProvider getStringProvider(IExpression expr)
	{
		EntityInterface entity = entity(expr);

        SrcStringProvider srcStringProvider;
        if (isDerived(entity))
        {
            srcStringProvider = srcStringProvider(inheritanceStrategy(entity));
        }
        else
        {
            srcStringProvider = new DefaultSrcProvider();
        }
		return srcStringProvider;
	}

    /**
     * Checks if the association is many to many.
     * @param assoc association
     * @return <CODE>true</CODE> if association is many to many,
	 * <CODE>false</CODE> otherwise
     */
    private boolean manyToMany(AssociationInterface assoc)
    {
        ConstraintPropertiesInterface constraintProperty = assoc.getConstraintProperties();
        return constraintProperty.getSrcEntityConstraintKeyProperties()!= null 
        		&& constraintProperty.getTgtEntityConstraintKeyProperties() != null;
    }

    /**
     * @param strategy inheritance strategy
     * @return the class according to inheritance strategy
     */
    private SrcStringProvider srcStringProvider(InheritanceStrategy strategy)
    {
    	SrcStringProvider stringProvider;
        switch (strategy)
        {
            case TABLE_PER_CONCRETE_CLASS :
            	stringProvider = new TblPerConcreteClass();
            	break;
            case TABLE_PER_HEIRARCHY :
            	stringProvider = new TblPerHier();
            	break;
            case TABLE_PER_SUB_CLASS :
            	stringProvider = new TblPerSubClass();
            	break;
            default :
                throw new RuntimeException("Unknown inheritance strategy.");
        }
        return stringProvider;
    }

    /**
     * Source String provider.
     */
    private interface SrcStringProvider
    {
        void srcString(IExpression expression, Map<String, String> clause);
    }

    private static class DefaultSrcProvider implements SrcStringProvider
    {
    	/**
    	 * @return query string
    	 */
        public void srcString(IExpression expression, Map<String, String> clause)
        {
            EntityInterface entity = entity(expression);
            AttributeInterface actAttr;
            String tabAlias = aliasOf(expression);

            clause.put("from", tableName(entity) + " " + tabAlias);
            if (expression.containsRule() &&  (actAttr = activityStatus(entity)) != null)
            {   
            	clause.put("where", activeCond(tabAlias + "." + columnName(actAttr)));
            }
        }
    }

    /**
     * Table per concrete class.
     */
    private static class TblPerConcreteClass extends DefaultSrcProvider
    {
    }

    /**
     * Table per hierarchy.
     */
    private static class TblPerHier implements SrcStringProvider
    {
    	/**
    	 * @return query
    	 */
        public void srcString(IExpression expression, Map<String, String> clause)
        {
            EntityInterface entity = entity(expression);
            String table = tableName(getRoot(entity));
            clause.put("from", table + " " + alias(table, expression));
            if (expression.containsRule()) {
            	clause.put("where", conds(entity));  
            }
        }

        /**
         * @param entity entity
         * @return res
         */
        private String conds(EntityInterface entity)
        {   
            String condition = discriminator(entity);
            AttributeInterface actAttr = activityStatus(entity);
            if (actAttr != null)
            {
            	condition += " and " + activeCond(columnName(actAttr));
            }
            return condition;
        }

        /**
         * @param entity entity
         * @return condition
         */
        private static String discriminator(EntityInterface entity)
        {
            String columnName = entity.getDiscriminatorColumn();
            String columnValue = entity.getDiscriminatorValue();
            // Assuming Discriminator is of type String.
            return columnName + "='" + columnValue + "'";
        }
    }

    /**
     * Table per sub-class
     */
    private class TblPerSubClass implements SrcStringProvider
    {
       	private IExpression expr;

        /**
         * TODO assumes a rosy practical database design where (1). SAME KEY for
         * whole hierarchy. this is theoretically FLAWED; a subclass may have a
         * different name for the key column than the superclass. But this isn't
         * supported by DE. (2). No clashes of column names across attributes in
         * the hierarchy; this makes the code easy. If/when a bug occurs when a
         * parent and child tables have same column name (for different
         * attributes), this is the place to fix the bug. FIX: generate unique
         * aliases for the columns in this SELECT clause.
         * @return query string
         */
        public void srcString(IExpression expression, Map<String, String> clause)
        {
        	this.expr = expression;
        	EntityInterface child = entity(expression);
        	clause.put("from", " ("+ fromClause(child) +") ");
        	
        	if (expression.containsRule()) {
        		clause.put("where", whereClause(child));  
        	}
        }
        
        /**
         * Return from clause.
         * @param childEntity child entity
         * @return from clause
         */
        private String fromClause(EntityInterface child)
        {        	        	
            StringBuilder res = new StringBuilder();           
            res.append(tableName(child) + " " + alias(tableName(child), expr));
            EntityInterface parent = child.getParentEntity();
            while (parent != null)
            {
                res.append(INNER_JOIN);
                res.append(tableName(parent)+ " " + alias(tableName(parent), expr));
                res.append(JOIN_ON);
                res.append(joinWithParent(child));

                child = parent;
                parent = parent.getParentEntity();
            }
            return res.toString();
        }

        /**
         * Join condition.
         * @param entity entity
         * @return join condition
         */
        private String joinWithParent(EntityInterface entity)
        {
            return key(entity) + " = " + key(entity.getParentEntity());
        }

        /**
         * @param entity entity
         * @return primary key
         */
        private String key(EntityInterface entity)
        {
        	return primaryKey(entity, expr);
        }

        /**
         * @param attr attribute
         * @return Qualified column name
         */
        private String qualifiedColName(AttributeInterface attr)
        {           
            return alias(tableName(origAttr(attr).getEntity()), expr) + "." + columnName(attr);
        }

        /**
         * Return the where clause with activity status condition.
         * @param child child
         * @return where clause
         */
        private String whereClause(EntityInterface child)
        {
        	String whereClause = "";
        	AttributeInterface actAttr = activityStatus(child);
        	if (actAttr != null)
        	{
        		whereClause = activeCond(qualifiedColName(actAttr));
        	}
            
            return whereClause;
        }
    }

    // entity properties
    /**
     * @param entity entity
     * @return parent entity
     */
    private static EntityInterface getRoot(EntityInterface entity)
    {
        // old code also checked inheritance strategy here; not needed if
        // strategies are not mixed.
		while (entity.getParentEntity() != null) 
		{
			entity = entity.getParentEntity();
		}
	
		return entity;
    }

    /**
     * Returns the entity.
     * @param expr expression
     * @return entity
     */
    private static EntityInterface entity(IExpression expr)
    {
        return expr.getQueryEntity().getDynamicExtensionsEntity();
    }

    /**
     * Returns the collection of attributes of the passed entity.
     * @param entity entity
     * @return attributes
     */
    private static Collection<AttributeInterface> attributes(EntityInterface entity)
    {
        return entity.getEntityAttributesForQuery();
    }

    /**
     * Returns the inheritance strategy of the entity.
     * @param entity entity
     * @return inheritance strategy
     */
    private static InheritanceStrategy inheritanceStrategy(EntityInterface entity)
    {
        return entity.getInheritanceStrategy();
    }

    /**
     * Checks if the entity is a child of any other entity.
     * @param entity entity
     * @return <CODE>true</CODE> passed entity is child of some other entity,
	 * <CODE>false</CODE> otherwise
     */
    private static boolean isDerived(EntityInterface entity)
    {
        return entity.getParentEntity() != null;
    }

    /**
     * Returns the table name.
     * @param entity entity
     * @return table name
     */
    private static String tableName(EntityInterface entity)
    {
        return entity.getTableProperties().getName();
    }

    /**
     * Gets the column name.
     * @param attr attribute
     * @return column name
     */
    private static String columnName(AttributeInterface attr)
    {
        return origAttr(attr).getColumnProperties().getName();
    }

    /**
     * Returns the middle table name.
     * @param assoc association
     * @return middle table name
     */
    private static String middleTabName(AssociationInterface assoc)
    {
        return assoc.getConstraintProperties().getName();
    }

    /**
     * Returns the actual attribute.
     * @param attr attribute
     * @return attribute
     */
    private static AttributeInterface origAttr(AttributeInterface attr)
    {
        return InheritanceUtils.getInstance().getActualAttribute(attr);
    }

    /**
     * To get the primary key attribute of the given entity.
     * @param entity the DE entity.
     * @return The Primary key attribute of the given entity.
     */
    private static String primaryKey(EntityInterface entity, IExpression expr)
    {
        Collection<AttributeInterface> attributes = attributes(entity);
        for (AttributeInterface attribute : attributes)
        {
            if (attribute.getIsPrimaryKey() || attribute.getName().equals("id"))
            {               
                return alias(tableName(entity), expr) + "." + columnName(attribute); 
            }
        }
        EntityInterface parentEntity = entity.getParentEntity();
        if (parentEntity != null)
        {
            return primaryKey(parentEntity, expr);
        }
        throw new RuntimeException("No Primary key attribute found for Entity:" + entity.getName());
    }

    /**
     * Check for activity status present in entity.
     * @param entity The Entity for which we required to check if
     *            activity status present.
     * @return Reference to the AttributeInterface if activityStatus attribute
     *         present in the entity, else null.
     */
    private static AttributeInterface activityStatus(EntityInterface entity)
    {
    	AttributeInterface activityStatus = null;
        for (AttributeInterface attribute : attributes(entity))
        {
            if (attribute.getName().equals(AQConstants.ACTIVITY_STATUS))
            {
                activityStatus = attribute;
            }
        }
        return activityStatus;
    }

    /**
     * Adds activity status condition.
     * @param attr attribute
     * @return attribute
     */
    private static String activeCond(String attr)
    {
        return attr + " != '" + AQConstants.ACTIVITY_STATUS_DISABLED + "'";
    }

    /**
     * Removes the last occurrence of string specified by 'toRemove' from the source.
     * @param src source
     * @param toRemove toRemove
     * @return the source
     */
    private static String removeLastOccur(String src, String toRemove)
    {
    	String returnSrc = src;
        if (src.endsWith(toRemove))
        {
        	returnSrc = src.substring(0, src.length() - toRemove.length());
        }
        return returnSrc;
    }    
}
