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

import org.midao.jdbc.core.handlers.output.lazy.BeanLazyOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.BeanLazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyOutputHandler;
import org.midao.jdbc.core.handlers.output.lazy.MapLazyScrollUpdateOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.List;
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

    public static void queryLazyOutputMapList(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            List<Map<String, Object>> result = (List<Map<String, Object>>) structure.values.get("resultMapList");
            assertEquals("Not me", result.get(0).get("name").toString());

            for (int i = 0; i < 20; i++) {
                assertEquals(Integer.toString(i + 1), result.get(i).get("id").toString());
            }
        } finally {
            structure.drop(runner);
        }
    }

    public static void queryLazyOutputHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            MapLazyOutputHandler resultMap = (MapLazyOutputHandler) structure.values.get("lazyMapList");
            assertEquals("Not me", resultMap.getNext().get("name").toString());

            int i = 1;

            while (resultMap.hasNext() == true) {
                assertEquals(Integer.toString(i + 1), resultMap.getNext().get("id").toString());

                i++;
            }

            BeanLazyOutputHandler<Student> resultBean = (BeanLazyOutputHandler<Student>) structure.values.get("lazyBeanList");
            assertEquals("Not me", resultBean.getNext().getName());

            i = 1;

            while (resultBean.hasNext() == true) {
                assertEquals(Integer.toString(i + 1), resultBean.getNext().getId().toString());

                i++;
            }

            resultBean.close();
            resultMap.close();
        } finally {
            structure.drop(runner);
        }
    }

    public static void queryLazyScrollOutputHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        MapLazyScrollUpdateOutputHandler resultMap = null;
        BeanLazyScrollUpdateOutputHandler<Student> resultBean = null;

        try {

            structure.create(runner);

            structure.execute(runner);

            resultMap = (MapLazyScrollUpdateOutputHandler) structure.values.get("lazyMapList");
            assertEquals("Not me", resultMap.getNext().get("name").toString());

            int i = 1;
            String id = null;

            while (resultMap.hasNext() == true) {
                id = resultMap.getNext().get("id").toString();

                assertEquals(Integer.toString(i + 1), id);

                i++;
            }

            // last ID returned should be 20.
            assertEquals("20", id);

            while (resultMap.hasPrev()) {
                id = resultMap.getPrev().get("id").toString();

                assertEquals(Integer.toString(i), id);

                i--;
            }

            // last ID returned should be 1.
            assertEquals("1", id);

            resultMap.moveTo(10);
            assertEquals("10", resultMap.getCurrent().get("id").toString());

            resultMap.moveRelative(-2);
            assertEquals("8", resultMap.getCurrent().get("id").toString());

            resultMap.moveRelative(2);
            assertEquals("10", resultMap.getCurrent().get("id").toString());

            resultMap.moveTo(0);

            i = 0;
            id = null;

            while (resultMap.hasNext() == true) {
                resultMap.moveRelative(1);
                id = resultMap.getCurrent().get("id").toString();

                assertEquals(Integer.toString(i + 1), id);

                i++;
            }

            // last ID returned should be 20.
            assertEquals("20", id);

            while (resultMap.hasPrev()) {
                resultMap.moveRelative(-1);
                id = resultMap.getCurrent().get("id").toString();

                assertEquals(Integer.toString(i), id);

                i--;
            }

            // last ID returned should be 1.
            assertEquals("1", id);

            resultBean = (BeanLazyScrollUpdateOutputHandler<Student>) structure.values.get("lazyBeanList");
            assertEquals("Not me", resultBean.getNext().getName());

            i = 1;
            id = null;

            while (resultBean.hasNext() == true) {
                id = resultBean.getNext().getId().toString();

                assertEquals(Integer.toString(i + 1), id);

                i++;
            }

            // last ID returned should be 20.
            assertEquals("20", id);

            while (resultBean.hasPrev()) {
                id = resultBean.getPrev().getId().toString();

                assertEquals(Integer.toString(i), id);

                i--;
            }

            // last ID returned should be 1.
            assertEquals("1", id);

            resultBean.moveTo(10);
            assertEquals("10", resultBean.getCurrent().getId().toString());

            resultBean.moveRelative(-2);
            assertEquals("8", resultBean.getCurrent().getId().toString());

            resultBean.moveRelative(2);
            assertEquals("10", resultBean.getCurrent().getId().toString());

            resultBean.moveTo(0);

            i = 0;
            id = null;

            while (resultBean.hasNext() == true) {
                resultBean.moveRelative(1);
                id = resultBean.getCurrent().getId().toString();

                assertEquals(Integer.toString(i + 1), id);

                i++;
            }

            // last ID returned should be 20.
            assertEquals("20", id);

            while (resultBean.hasPrev()) {
                resultBean.moveRelative(-1);
                id = resultBean.getCurrent().getId().toString();

                assertEquals(Integer.toString(i), id);

                i--;
            }

            // last ID returned should be 1.
            assertEquals("1", id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            closeQuietly(resultMap);
            closeQuietly(resultBean);

            structure.drop(runner);

            runner.commit();
        }
    }

    public static void queryMapLazyUpdateOutputHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        MapLazyScrollUpdateOutputHandler resultMap = null;
        BeanLazyScrollUpdateOutputHandler<Student> resultBean = null;

        try {

            structure.create(runner);

            structure.execute(runner);

            resultMap = (MapLazyScrollUpdateOutputHandler) structure.values.get("lazyMapList");
            resultBean = (BeanLazyScrollUpdateOutputHandler<Student>) structure.values.get("lazyBeanList");

            Map<String, Object> row = null;
            Student student = null;

            row = resultMap.getNext();
            assertEquals("Not me", row.get("name").toString());

            row = resultMap.getNext();
            assertEquals("Not him", row.get("name").toString());
            assertEquals("", row.get("address") == null ? "" : row.get("address").toString());

            row.put("address", "Somewhere");
            //row.remove("id");

            resultMap.updateRow(row);

            assertEquals(false, resultMap.hasNext());

            row.put("name", "Not her");
            //row.remove("id");

            resultMap.insertRow(row);

            resultMap.close();
            runner.commit();

            structure.execute(runner);

            resultMap = (MapLazyScrollUpdateOutputHandler) structure.values.get("lazyMapList");
            resultBean = (BeanLazyScrollUpdateOutputHandler<Student>) structure.values.get("lazyBeanList");

            row = resultMap.getNext();
            assertEquals("Not me", row.get("name").toString());

            row = resultMap.getNext();
            assertEquals("Not him", row.get("name").toString());
            assertEquals("Somewhere", row.get("address").toString());

            row = resultMap.getNext();
            assertEquals("Not her", row.get("name").toString());
            assertEquals("Somewhere", row.get("address").toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            closeQuietly(resultMap);
            closeQuietly(resultBean);

            structure.drop(runner);

            runner.commit();
        }
    }

    public static void queryBeanLazyUpdateOutputHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        MapLazyScrollUpdateOutputHandler resultMap = null;
        BeanLazyScrollUpdateOutputHandler<Student> resultBean = null;

        try {

            structure.create(runner);

            structure.execute(runner);

            resultMap = (MapLazyScrollUpdateOutputHandler) structure.values.get("lazyMapList");
            resultBean = (BeanLazyScrollUpdateOutputHandler<Student>) structure.values.get("lazyBeanList");

            Student student = null;

            student = resultBean.getNext();
            assertEquals("Not me", student.getName());

            student = resultBean.getNext();
            assertEquals("Not him", student.getName());
            assertEquals("", ( student.getAddress() == null ? "" : student.getAddress() ));

            student.setAddress("Somewhere");

            resultBean.updateRow(student);

            assertEquals(false, resultBean.hasNext());

            student.setName("Not her");

            resultBean.insertRow(student);

            resultMap.close();
            resultBean.close();
            runner.commit();

            //--

            structure.execute(runner);

            resultMap = (MapLazyScrollUpdateOutputHandler) structure.values.get("lazyMapList");
            resultBean = (BeanLazyScrollUpdateOutputHandler<Student>) structure.values.get("lazyBeanList");

            student = resultBean.getNext();
            assertEquals("Not me", student.getName());

            student = resultBean.getNext();
            assertEquals("Not him", student.getName());
            assertEquals("Somewhere", student.getAddress());

            student = resultBean.getNext();
            assertEquals("Not her", student.getName());
            assertEquals("Somewhere", student.getAddress());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            closeQuietly(resultMap);
            closeQuietly(resultBean);

            structure.drop(runner);

            runner.commit();
        }
    }
}
