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

package org.midao.jdbc.core.db.mysql;

import org.midao.jdbc.core.MidaoFactory;
import org.midao.jdbc.core.db.DBCall;
import org.midao.jdbc.core.db.DBCallQueryStructure;
import org.midao.jdbc.core.db.DBConstants;
import org.midao.jdbc.core.db.QueryStructure;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class CallTest extends BaseMySQL {

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
				runner.update(DBConstants.MYSQL_PROCEDURE_INOUT);
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.MYSQL_FUNCTION);
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.MYSQL_PROCEDURE_RETURN);
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
    	
    	DBCall.callOutputHandlerMap(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.MYSQL_PROCEDURE_RETURN);
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
    	
    	DBCall.callOutputHandlerBean(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
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
				runner.update(DBConstants.MYSQL_PROCEDURE_LARGE);
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
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callLargeParameters(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
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
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);

		        defaultStructure.create(runner);

		        runner.update(DBConstants.MYSQL_PROCEDURE_NAMED);
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
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);
    	
    	DBCall.callNamedHandler(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);
    	
    	DBCall.callNamedHandler(structure, runner);
    }
}
