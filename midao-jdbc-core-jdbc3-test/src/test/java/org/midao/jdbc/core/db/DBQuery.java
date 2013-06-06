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

import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.Map;

public class DBQuery extends BaseDB {
	public static void queryOutputHandlerWParamsDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryOutputHandlerDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler1DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler2DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler3DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
}
