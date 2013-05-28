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

import org.midao.core.handlers.input.named.MapInputHandler;
import org.midao.core.handlers.output.MapOutputHandler;
import org.midao.core.handlers.output.RowCountOutputHandler;
import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBUpdateQueryStructure {

	public static QueryStructure updateGeneratedKeysDS(Map<String, Object> values) throws SQLException {
    		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				MapOutputHandler handler = new MapOutputHandler();
				
				this.values.put("generatedKeys", runner.update(DBConstants.INSERT_STUDENT_TABLE, handler, new Object[0]));
				this.values.put("generatedKeys", runner.update(DBConstants.INSERT_STUDENT_TABLE, handler, new Object[0]));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
	
	public static QueryStructure updateRowCountHandlerDS(Map<String, Object> values) throws SQLException {
		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				this.values.put("rowsUpdated", runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
	
	public static QueryStructure updateWParamsDS(Map<String, Object> values) throws SQLException {
		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				this.values.put("rowsUpdated", runner.update(DBConstants.INSERT_STUDENT_TABLE_W_PARAMS, "not me"));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
	
	public static QueryStructure updateInputHandler1DS(Map<String, Object> values) throws SQLException {
		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				MapInputHandler input = new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{put("studentName", "not me");}});
				
				this.values.put("rowsUpdated", runner.update(input, new RowCountOutputHandler<Integer>()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
	
	public static QueryStructure updateInputHandler2DS(Map<String, Object> values) throws SQLException {
		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				MapInputHandler input = new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{put("studentName", "not me");}});
				
				this.values.put("rowsUpdated", runner.update(input, new RowCountOutputHandler<Integer>()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
	
	public static QueryStructure updateInputHandler3DS(Map<String, Object> values) throws SQLException {
		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				MapInputHandler input = new MapInputHandler(DBConstants.INSERT_NAMED2_STUDENT_TABLE, new HashMap<String, Object>() {{put("name", "not me");put("address", "somewhere");}}, "student");
				
				this.values.put("rowsUpdated", runner.update(input, new RowCountOutputHandler<Integer>()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE));
			}
    		
    	};
	}
}
