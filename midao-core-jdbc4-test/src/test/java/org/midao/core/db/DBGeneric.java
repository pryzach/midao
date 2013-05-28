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

import org.midao.core.exception.MidaoSQLException;
import org.midao.core.handlers.output.ArrayOutputHandler;
import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;

public class DBGeneric extends BaseDB {
	public static void genericTransactionHandlerRollback(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		Object[] result = null;
		
		try {
			structure.create(runner);
		
			result = runner.query("SELECT COUNT(name) FROM students", new ArrayOutputHandler());
		
			assertEquals(2, ((Integer) result[0]).intValue());
		
			structure.execute(runner);
		
			result = runner.query("SELECT COUNT(name) FROM students", new ArrayOutputHandler());
		
			assertEquals(0, ((Integer) result[0]).intValue());
		
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void genericExceptionHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		Object[] result = null;
		
		try {
			structure.create(runner);
		
			try {
				structure.execute(runner);
			
				fail();
			} catch (MidaoSQLException ex) {
				// success
			}
		} finally {
			structure.drop(runner);
		}
	}
}
