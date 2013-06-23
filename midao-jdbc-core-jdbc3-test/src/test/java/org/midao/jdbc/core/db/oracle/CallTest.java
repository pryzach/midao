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

package org.midao.jdbc.core.db.oracle;

import org.midao.jdbc.core.MidaoFactory;
import org.midao.jdbc.core.MidaoTypes;
import org.midao.jdbc.core.db.*;
import org.midao.jdbc.core.exception.ExceptionUtils;
import org.midao.jdbc.core.exception.MidaoException;
import org.midao.jdbc.core.handlers.input.query.QueryInputHandler;
import org.midao.jdbc.core.handlers.model.CallResults;
import org.midao.jdbc.core.handlers.model.QueryParameters;
import org.midao.jdbc.core.handlers.output.BeanOutputHandler;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.handlers.output.OutputHandler;
import org.midao.jdbc.core.handlers.type.OracleTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class CallTest extends BaseOracle {

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
				runner.update(DBConstants.ORACLE_PROCEDURE_INOUT);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBCall.callQueryParameters(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.ORACLE_FUNCTION);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("name", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

		        input = new QueryInputHandler(DBConstants.ORACLE_CALL_FUNCTION, parameters);
		        QueryParameters result = runner.call(input);

		        this.values.put("result1", result);

		        assertEquals("Doe", result.getValue("name"));

		        parameters.set("id", 1, MidaoTypes.INTEGER, QueryParameters.Direction.IN);
		        parameters.set("name", null, MidaoTypes.VARCHAR, QueryParameters.Direction.OUT);

		        input = new QueryInputHandler(DBConstants.ORACLE_CALL_FUNCTION, parameters);

		        this.values.put("result2", runner.call(input));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
				runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBCall.callFunction(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.ORACLE_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("cursor", null, oracle.jdbc.OracleTypes.CURSOR, QueryParameters.Direction.OUT);
		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.ORACLE_CALL_PROCEDURE_RETURN, parameters);
		        CallResults<QueryParameters, Map<String, Object>> result = runner.call(input, new MapOutputHandler());
		        List<QueryParameters> outputList = (List<QueryParameters>) result.getCallInput().getValue("cursor");

		        result.setCallOutput(outputList.get(0).toMap());
		        
		        this.values.put("result", result);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				//defaultStructure.drop(runner);
				runner.update(DBConstants.DROP_STUDENT_TABLE);
				runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, OracleTypeHandler.class);
    	
    	DBCall.callOutputHandlerMap(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, OracleTypeHandler.class);
    	
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.ORACLE_PROCEDURE_RETURN);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				QueryInputHandler input = null;
		        QueryParameters parameters = new QueryParameters();

		        parameters.set("cursor", null, oracle.jdbc.OracleTypes.CURSOR, QueryParameters.Direction.OUT);
		        parameters.set("id", 2, MidaoTypes.INTEGER, QueryParameters.Direction.IN);

		        input = new QueryInputHandler(DBConstants.ORACLE_CALL_PROCEDURE_RETURN, parameters);

		        OutputHandler<Student> outputHandler = new BeanOutputHandler<Student>(Student.class);
		        CallResults<QueryParameters, Student> result = runner.call(input, outputHandler);
		        List<QueryParameters> outputList = (List<QueryParameters>) result.getCallInput().getValue("cursor");
		        
		        // creating empty technical field, which is required for every OutputHandlers
		        outputList.add(0, new QueryParameters());

                try {
		            result.setCallOutput(outputHandler.handle(outputList));
                } catch (MidaoException ex) {
                    ExceptionUtils.rethrow(ex);
                }
		        
		        this.values.put("result", result);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
				runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, OracleTypeHandler.class);
    	
    	DBCall.callOutputHandlerBean(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, OracleTypeHandler.class);
    	
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
				runner.update(DBConstants.ORACLE_PROCEDURE_LARGE);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, OracleTypeHandler.class);
    	
    	DBCall.callLargeParameters(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, OracleTypeHandler.class);
    	
    	DBCall.callLargeParameters(structure, runner);
    }

    public void testCallProcedureLargeParametersStream() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBCallQueryStructure.callLargeParametersStream(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.ORACLE_PROCEDURE_LARGE);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, OracleTypeHandler.class);

        DBCall.callLargeParameters(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, OracleTypeHandler.class);

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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ);
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.ORACLE_PROCEDURE_NAMED);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
		        runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, OracleTypeHandler.class);
    	
    	DBCall.callNamedHandler(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, OracleTypeHandler.class);
    	
    	DBCall.callNamedHandler(structure, runner);
    }
}
