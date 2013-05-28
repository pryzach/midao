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

import org.midao.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class QueryStructure {
	public final Map<String, Object> values;
	
	public QueryStructure() {
		this.values = new HashMap<String, Object>();
	}
	
	public QueryStructure(Map<String, Object> values) {
		this.values = values;
	}
	
	public abstract void create(QueryRunnerService runner) throws SQLException;
	public abstract void execute(QueryRunnerService runner) throws SQLException;
	public abstract void drop(QueryRunnerService runner) throws SQLException;
	
}
