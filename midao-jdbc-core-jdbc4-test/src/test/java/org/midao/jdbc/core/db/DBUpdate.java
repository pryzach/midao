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

public class DBUpdate extends BaseDB {
	public static void updateGeneratedKeysDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		try {
			Map<String, Object> generatedKeys = null;
		
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			generatedKeys = (Map<String, Object>) structure.values.get("generatedKeys");
			assertEquals("2", generatedKeys.get(generatedKeys.keySet().toArray()[0]).toString());
		
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
	public static void updateRowCountHandlerDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		
		try {
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			rowsUpdated = (Integer) structure.values.get("rowsUpdated");
			assertEquals(1, rowsUpdated);
		
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
	public static void updateWParamsDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		
		try {
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			rowsUpdated = (Integer) structure.values.get("rowsUpdated");
			assertEquals(1, rowsUpdated);
		
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
	public static void updateInputHandler1DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		
		try {
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			rowsUpdated = (Integer) structure.values.get("rowsUpdated");
			assertEquals(1, rowsUpdated);
		
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
	public static void updateInputHandler2DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		
		try {
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			rowsUpdated = (Integer) structure.values.get("rowsUpdated");
			assertEquals(1, rowsUpdated);
			
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
	public static void updateInputHandler3DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		int rowsUpdated = -1;
		
		try {
			structure.create(runner);
		
			rowsUpdated = (Integer) structure.values.get("createUpdatedCount");
			assertEquals(0, rowsUpdated);
		
			structure.execute(runner);
		
			rowsUpdated = (Integer) structure.values.get("rowsUpdated");
			assertEquals(1, rowsUpdated);
		
		} finally {
			structure.drop(runner);
		
			rowsUpdated = (Integer) structure.values.get("dropUpdatedCount");
			assertEquals(0, rowsUpdated);
		}
	}
	
}
