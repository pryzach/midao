/*
 * Copyright 2013 Zakhar Prykhoda
 *
 *    midao.org
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

package org.midao.core.metadata;

import junit.framework.Assert;
import org.junit.Test;

/**
 */
public class StoredProcedureTest {

    @Test
    public void testConstructor() {
        String catalog = "";
        String schema = "";
        String name = "";

        StoredProcedure proc = new StoredProcedure(catalog, schema, name);

        Assert.assertEquals(catalog, proc.getCatalog());
        Assert.assertEquals(schema, proc.getSchema());
        Assert.assertEquals(name, proc.getProcedureName());
    }

    @Test
    public void testEquals() throws Exception {
        String catalog = "catalog";
        String schema = "schema";
        String name = "executeproc";

        StoredProcedure proc = new StoredProcedure(catalog, schema, name);
        StoredProcedure procCompare = new StoredProcedure(null, null, name);

        // proc has filled catalog and schema and is not equals to null
        Assert.assertEquals(false, proc.equals(procCompare));
        // procCompare has catalog and schema set as null and during compare those values are not compared
        Assert.assertEquals(true, procCompare.equals(proc));

        Assert.assertEquals(true, proc.equals(proc));
        Assert.assertEquals(true, procCompare.equals(procCompare));
        Assert.assertEquals(false, proc.equals(new StoredProcedure(null, null, "something_else")));
    }

    @Test(expected = Exception.class)
    public void testEqualsException() throws Exception {
        String catalog = "catalog";
        String schema = "schema";
        String name = "executeproc";

        StoredProcedure proc = new StoredProcedure(catalog, schema, name);
        StoredProcedure procCompare = new StoredProcedure(null, null, null);

        Assert.assertEquals(true, proc.equals(procCompare));
    }

    @Test
    public void testCompareTo() throws Exception {
        String catalog = "catalog";
        String schema = "schema";
        String name = "executeproc";

        StoredProcedure proc = new StoredProcedure(catalog, schema, name);
        StoredProcedure procCompare = new StoredProcedure(null, null, name);

        // proc has filled catalog and schema and will be bigger
        Assert.assertEquals(1, proc.compareTo(procCompare));
        // procCompare has catalog and schema set as null and will be less
        Assert.assertEquals(-1, procCompare.compareTo(proc));

        Assert.assertEquals(0, proc.compareTo(proc));
        Assert.assertEquals(0, procCompare.compareTo(procCompare));
        Assert.assertEquals(1, proc.compareTo(new StoredProcedure(null, null, "something_else")));
    }

    @Test
    public void testToString() throws Exception {
        String catalog = "catalog";
        String schema = "schema";
        String name = "executeproc";

        String expectedString = catalog + "." + schema + "." + name;

        StoredProcedure proc = new StoredProcedure(catalog, schema, name);

        Assert.assertEquals(expectedString, proc.toString());
    }
}
