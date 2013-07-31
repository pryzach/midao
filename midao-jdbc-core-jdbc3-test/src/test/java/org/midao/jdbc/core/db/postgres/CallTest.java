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

package org.midao.jdbc.core.db.postgres;

import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.MjdbcTypes;
import org.midao.jdbc.core.db.*;
import org.midao.jdbc.core.exception.ExceptionUtils;
import org.midao.jdbc.core.exception.MjdbcException;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.CallResults;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.BeanOutputHandler;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class CallTest extends BasePostgres {

    public void testQueryInputHandler() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callQueryParameters(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.POSTGRES_PROCEDURE_INOUT);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				//defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource);
    	
    	DBCall.callQueryParameters(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn);
    	
    	DBCall.callQueryParameters(structure, runner);
    }

    public void testCallFunction() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callFunction(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.POSTGRES_FUNCTION);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource);
    	
    	DBCall.callFunction(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn);
    	
    	DBCall.callFunction(structure, runner);
    }
    
    public void testCallProcedureReturn() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callOutputHandlerMap(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.POSTGRES_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("cursor", null, MjdbcTypes.OTHER, QueryParameters.Direction.OUT);
		        parameters.set("id", 2, MjdbcTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.POSTGRES_CALL_PROCEDURE_RETURN, parameters);
		        CallResults<QueryParameters, Map<String, Object>> result = runner.call(input, new MapOutputHandler());
		        List<QueryParameters> outputList = (List<QueryParameters>) result.getCallInput().getValue("cursor");

		        result.setCallOutput(outputList.get(0).toMap());
		        
		        this.values.put("result", result);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callOutputHandlerMap(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
    	DBCall.callOutputHandlerMap(structure, runner);
    }
    
    public void testCallProcedureReturn2() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callOutputHandlerBean(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.POSTGRES_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("cursor", null, MjdbcTypes.OTHER, QueryParameters.Direction.OUT);
		        parameters.set("id", 2, MjdbcTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.POSTGRES_CALL_PROCEDURE_RETURN, parameters);

		        OutputHandler<Student> outputHandler = new BeanOutputHandler<Student>(Student.class);
		        CallResults<QueryParameters, Student> result = runner.call(input, outputHandler);
		        List<QueryParameters> outputList = (List<QueryParameters>) result.getCallInput().getValue("cursor");
		        
		        // creating empty technical field, which is required for every OutputHandlers
		        outputList.add(0, new QueryParameters());

                try {
		            result.setCallOutput(outputHandler.handle(outputList));
                } catch (MjdbcException ex) {
                    ExceptionUtils.rethrow(ex);
                }
		        
		        this.values.put("result", result);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callOutputHandlerBean(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
    	DBCall.callOutputHandlerBean(structure, runner);
    }
    
    public void testCallProcedureLargeParameters() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callLargeParameters(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.POSTGRES_PROCEDURE_LARGE);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				//defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callLargeParameters(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
    	DBCall.callLargeParameters(structure, runner);
    }
    
    public void testNamedHandler() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBCallQueryStructure.callNamedHandler(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.POSTGRES_PROCEDURE_NAMED);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callNamedHandler(structure, runner);
        
    	// Call to DatabaseMetadata and call for a function cannot be in one "transaction".
    	// Solutions:
    	// a) Use DataSource. In such case Call to DatabaseMetadata is performed using different connection.
    	// b) Use metadataHandler.getProcedureParameters. Using CallableUtils.updateDirections and CallableUtils.updateTypes - update your QueryParameters

    	//runner = MjdbcFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
    	//DBCall.callNamedHandler(structure, runner);
    }
}
