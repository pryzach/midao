/*
 * Copyright 2013 Zakhar Prykhoda
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.midao.core.db;

import org.midao.core.exception.MidaoException;
import org.midao.core.handlers.model.CallResults;
import org.midao.core.handlers.model.QueryParameters;
import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBCall extends BaseDB {
    public static void callQueryParameters(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	QueryParameters result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (QueryParameters) structure.values.get("result");
    	
    		assertEquals("John", result.getValue("name"));
    		assertEquals("DOE", result.getValue("surname"));
        	assertEquals("John DOE", result.getValue("fullname"));
        
    	} finally {
    		structure.drop(runner);
    	}
    }

    public static void callProcedure(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    /*
        runner.update(CREATE_STUDENT_TABLE);

        runner.update(new MapInputHandler(INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
            put("studentName", "John");
        }}), new RowCountHandler<Integer>());
        runner.update(new MapInputHandler(INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
            put("studentName", "Doe");}}), new RowCountHandler<Integer>());

        if (dbName.equals(derby) == true) {
            runner.update(DERBY_PROCEDURE_IN);
        }

        QueryParameters parameters = new QueryParameters();
        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
        parameters.set("rs", null, QueryParameters.Direction.OUT);

        QueryInputHandler input = new QueryInputHandler(CALL_PROCEDURE_IN, parameters);
        QueryParameters result = null;

        result = runner.call(input);

        System.out.println(result);

        runner.update(DROP_STUDENT_TABLE);
        */
    }

    public static void callFunction(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	QueryParameters result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (QueryParameters) structure.values.get("result1");
    	
    		assertEquals("Doe", result.getValue("name"));
    	
    		result = (QueryParameters) structure.values.get("result2");
    	
    		assertEquals("John", result.getValue("name"));
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callOutputHandlerMap(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	CallResults<QueryParameters, Map<String, Object>> result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (CallResults<QueryParameters, Map<String, Object>>) structure.values.get("result");
    	
    		assertEquals("Doe", result.getCallOutput().get("name"));
    	
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callOutputHandlerListMap(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	CallResults<QueryParameters, List<Map<String, Object>>> result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (CallResults<QueryParameters, List<Map<String, Object>>>) structure.values.get("result");
    	
    		assertEquals("Doe", result.getCallOutput().get(0).get("name"));
    		assertEquals("John", result.getCallOutput().get(1).get("name"));
    	
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callOutputHandlerBean(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	CallResults<QueryParameters, Student> result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (CallResults<QueryParameters, Student>) structure.values.get("result");
    	
    		assertEquals("Doe", result.getCallOutput().getName());
    	
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callOutputHandlerListBean(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	CallResults<QueryParameters, List<Student>> result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (CallResults<QueryParameters, List<Student>>) structure.values.get("result");
    	
    		assertEquals("Doe", result.getCallOutput().get(0).getName());
    		assertEquals("John", result.getCallOutput().get(1).getName());
    	
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callNamedInputHandler(QueryStructure structure, QueryRunnerService runner) {
    	
    }
    
    public static void callLargeParameters(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	CallResults<QueryParameters, Map<String, Object>> result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (CallResults<QueryParameters, Map<String, Object>>) structure.values.get("result");
    		
    		String clobOut = (String) result.getCallInput().getValue("clobOut");
    		Object blobOut = result.getCallInput().getValue("blobOut");
    	
    		assertEquals("Hello John", clobOut);
    		
    		if (blobOut instanceof byte[]) {
    			assertEquals("Hi Doe", new String((byte[]) blobOut));
    		} else if (blobOut instanceof String) {
    			assertEquals("Hi Doe", blobOut);
    		} else {
    			fail();
    		}
    	} finally {
    		structure.drop(runner);
    	}
    }
    
    public static void callNamedHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
    	Student result = null;
    	
    	try {
    		structure.create(runner);
    	
    		structure.execute(runner);
    	
    		result = (Student) structure.values.get("result1");
    		assertEquals("Doe", result.getName());
    	
    		result = (Student) structure.values.get("result2");
    		assertEquals("John", result.getName());
    	} finally {
    		structure.drop(runner);
    	}
    }

    public static void callXMLParameters(QueryStructure structure, QueryRunnerService runner) {
    }

}
