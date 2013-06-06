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

package org.midao.jdbc.core.db;

import org.midao.jdbc.core.MidaoTypes;
import org.midao.jdbc.core.handlers.input.named.BeanInputHandler;
import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.CallResults;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.*;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBCallQueryStructure extends BaseDB {
    public static QueryStructure callQueryParameters(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
		        QueryParameters parameters = new QueryParameters();
		        parameters.set("name", "John", QueryParameters.Direction.IN);
		        parameters.set("surname", "doe", MidaoTypes.VARCHAR, QueryParameters.Direction.INOUT);
		        parameters.set("fullname", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

		        QueryInputHandler input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_INOUT, parameters);

		        this.values.put("result", runner.call(input));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_PROCEDURE_INOUT);
			}
    		
    	};
    }

    public static QueryStructure callFunction(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		        }}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

		        //runner.update(DERBY_FUNCTION);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("name", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

		        input = new QueryInputHandler(DBConstants.CALL_FUNCTION, parameters);
		        QueryParameters result = runner.call(input);

		        this.values.put("result1", result);

		        //assertEquals("Doe", result.getValue("name"));

		        parameters.set("id", 1, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("name", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

		        input = new QueryInputHandler(DBConstants.CALL_FUNCTION, parameters);

		        this.values.put("result2", runner.call(input));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_FUNCTION);
		        
		        runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    }
    
    public static QueryStructure callOutputHandlerMap(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		        }}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

		        //runner.update(DERBY_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_RETURN, parameters);
		        CallResults<QueryParameters, Map<String, Object>> result = null;

		        this.values.put("result", runner.call(input, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_PROCEDURE_RETURN);

		        runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    }
    
    public static QueryStructure callOutputHandlerListMap(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		        }}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

		        //runner.update(DERBY_PROCEDURE_MULTIPLE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id1", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("id2", 1, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_MULTIPLE_RETURN, parameters);
		        CallResults<QueryParameters, List<Map<String, Object>>> result = null;

		        this.values.put("result", runner.call(input, new MapListOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_PROCEDURE_MULTIPLE_RETURN);

		        runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    }
    
    public static QueryStructure callOutputHandlerBean(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		        }}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

		        //runner.update(DERBY_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_RETURN, parameters);

		        this.values.put("result", runner.call(input, new BeanOutputHandler<Student>(Student.class)));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_PROCEDURE_RETURN);

		        runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    }
    
    public static QueryStructure callOutputHandlerListBean(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		        }}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");}}), new RowCountOutputHandler<Integer>());

		        //runner.update(DERBY_PROCEDURE_MULTIPLE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id1", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("id2", 1, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_MULTIPLE_RETURN, parameters);
		        CallResults<QueryParameters, List<Student>> result = null;

		        this.values.put("result", runner.call(input, new BeanListOutputHandler<Student>(Student.class)));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_PROCEDURE_MULTIPLE_RETURN);

		        runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    }
    
    public static QueryStructure callLargeParameters(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("clobIn", "John", MidaoTypes.CLOB, QueryParameters.Direction.IN);
		        parameters.set("clobOut", null, MidaoTypes.CLOB, QueryParameters.Direction.OUT);
		        
		        parameters.set("blobIn", "Doe", MidaoTypes.BLOB, QueryParameters.Direction.IN);
		        parameters.set("blobOut", null, MidaoTypes.BLOB, QueryParameters.Direction.OUT);

		        input = new QueryInputHandler(DBConstants.CALL_PROCEDURE_LARGE, parameters);

		        this.values.put("result", runner.call(input, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_PROCEDURE_LARGE);
			}
    		
    	};
    }
    
    public static QueryStructure callNamedHandler(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {
    		
			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        //runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);

		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "John");
		            put("studentAddress22", "Somewhere safe");
		        	}}), new RowCountOutputHandler<Integer>());
		        runner.update(new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{
		            put("studentName", "Doe");
		            put("studentAddress22", "Somewhere far");
		            }}), new RowCountOutputHandler<Integer>());
		        

		        //runner.update(DBConstants.DERBY_PROCEDURE_NAMED);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				BeanInputHandler<Student> input = null;
		        Student student = new Student();
		        student.setId(2);

		        input = new BeanInputHandler<Student>(DBConstants.CALL_PROCEDURE_NAMED, student);

		        this.values.put("result1", runner.call(input));
		        
		        student.setId(1);

		        input = new BeanInputHandler<Student>(DBConstants.CALL_PROCEDURE_NAMED, student);

		        this.values.put("result2", runner.call(input));

			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.DROP_STUDENT_TABLE);

                runner.update(DBConstants.DROP_PROCEDURE_NAMED);
			}
    		
    	};
    }
}
