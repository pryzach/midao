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
import org.midao.jdbc.core.db.DBBatch;
import org.midao.jdbc.core.db.DBBatchQueryStructure;
import org.midao.jdbc.core.db.DBConstants;
import org.midao.jdbc.core.db.QueryStructure;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class BatchTest extends BasePostgres {
    public void testUpdateWithParams() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBBatchQueryStructure.batchWParamsDS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
				//this.values.put("rowsUpdated", runner.batch(DBConstants.INSERT_STUDENT_TABLE_W_PARAMS_ORACLE, new Object[][] {new Object[] {"not me"}, new Object[] {"not me either"}}));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource);
    	
        DBBatch.batchWParamsDS(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn);
    	
    	DBBatch.batchWParamsDS(structure, runner);
    }

    public void testInputHandler1DS() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBBatchQueryStructure.batchInputHandler1DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_POSTGRES);
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
    	
    	runner = MjdbcFactory.getQueryRunner(this.dataSource);
    	
    	DBBatch.batchInputHandler1DS(structure, runner);
        
    	runner = MjdbcFactory.getQueryRunner(this.conn);
    	
    	DBBatch.batchInputHandler1DS(structure, runner);
    }
}
