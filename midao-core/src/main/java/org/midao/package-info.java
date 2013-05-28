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

package org.midao;
/*
 * @author ZP (Zakhar Prykhoda)
 * 
 * This DAO Framework (will) support next features:
 * - Automatic Connection handling
 * - Automatic ResultSet handling
 * - Initial ORM functionality
 * - Asynchronous calls 
 * - Connection Pool
 * - Proper logging system
 * 
 * TODO:
 * ?? Remove @Depricated methods
 * Remove inconsistances - in QueryRunner some functions have Object param and Object... param, but other just Object... param
 * Clean my code and rename variables. Add comments!
 * Implement toString in InputHandlers!! All of them!!
 * QueryRunners tests DOES NOT INCLUDE operations with InputHandlers!!!
 * Add @SurpressWarning where applicable
 * Remove unused imports
 * !! Refactor variable names in Processor and all input handlers!!
 * Maybe retain 100% compatibility with dbutils, so it would ensure easier migration. Offer: leave interfaces - move core
 * Modify new functionality so it could avoid usage of %fieldname%
 * When I can avoid %fieldname% - what about list %1% ??
 * Modify Asynchronous calls so the use new ORM functionality
 * Pool has default values. Implement override in form properties and pojo
 * Implement limit and package size like noSql dbs
 * 
 * Implement wrapper for Properties. Detect present libraries and attach them (look at slf4j)
 * Implement wrapper for Logging. Idea is the same as with Properties.
 * 
 * MinDI Framework??
 */

/*
 * Goal of this project is not provide you with many ways to solve one problem, but rather offer you one, the most optimal way, to solve many problems.
So project focuses on delivering one optimized way to solve the problem.
Unfortunately, as side effect, this framework would be most suitable for new development and/or Apache Commons DBUtil based projects.

After the transition to this DAO Framework - amount of DAO related code can be lowered to 10-20% from original.

Benefits:
1) Ease of use, and ability to reuse existing knowledge for those who have experience with Apache Commons DBUtil.
2) Offers only one way to solve the problem, which simplifies learning curve.
3) Is well tested and based on proven solution from Apache Foundation.
4) Is fast and light.
5) Supports ORM mapping in it's simpliest form.
6) Build according to tested code qualities standards.
7) Unit tests
*/