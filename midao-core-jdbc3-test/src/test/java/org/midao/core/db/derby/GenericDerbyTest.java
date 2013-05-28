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

package org.midao.core.db.derby;

import org.midao.core.MidaoFactory;
import org.midao.core.db.DBConstants;
import org.midao.core.db.DBGeneric;
import org.midao.core.db.DBGenericQueryStructure;
import org.midao.core.db.QueryStructure;
import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GenericDerbyTest extends BaseDerby {

	public void testTransactionManager() throws SQLException {
    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBGenericQueryStructure.genericTransactionHandlerRollback(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
		        runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
		        
		        defaultStructure.create(runner);
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
    	
    	DBGeneric.genericTransactionHandlerRollback(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBGeneric.genericTransactionHandlerRollback(structure, runner);
	}
	
	public void testExceptionHandler() throws SQLException {
    	QueryRunnerService runner = null;
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBGenericQueryStructure.genericExceptionHandler(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBGeneric.genericExceptionHandler(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBGeneric.genericExceptionHandler(structure, runner);
	}
}
