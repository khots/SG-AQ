package edu.wustl.query.bizlogic;

import java.io.InputStream;
import java.io.OutputStream;

import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;

public interface ParameterizedQuerySerializer {
 
	public void serialize(IParameterizedQuery query, OutputStream out);
	
	public IParameterizedQuery deserialize(InputStream in);
}
