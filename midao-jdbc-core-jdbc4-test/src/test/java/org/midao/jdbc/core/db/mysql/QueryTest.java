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
import org.midao.jdbc.core.db.DBConstants;
import org.midao.jdbc.core.db.DBQuery;
import org.midao.jdbc.core.db.DBQueryQueryStructure;
import org.midao.jdbc.core.db.QueryStructure;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryTest extends BaseMySQL {
	
	public void testOutputHandler() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryOutputHandlerDS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);
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
    	
    	DBQuery.queryOutputHandlerDS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryOutputHandlerDS(structure, runner);
	}
	
	public void testOutputHandlerWParams() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryOutputHandlerWParamsDS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);
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
    	
    	DBQuery.queryOutputHandlerWParamsDS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryOutputHandlerWParamsDS(structure, runner);
	}
	
	public void testInputHandler1() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler1DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);
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
    	
    	DBQuery.queryInputHandler1DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler1DS(structure, runner);
	}
	
	public void testInputHandler2() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler2DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);
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
    	
    	DBQuery.queryInputHandler2DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler2DS(structure, runner);
	}
	
	public void testInputHandler3() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler3DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_MYSQL);
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
    	
    	DBQuery.queryInputHandler3DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler3DS(structure, runner);
	}
}
