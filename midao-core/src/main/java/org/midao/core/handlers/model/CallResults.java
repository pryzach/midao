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

package org.midao.core.handlers.model;

/**
 * Wrapper for @see {@link org.midao.core.QueryRunner#call(org.midao.core.handlers.input.InputHandler, org.midao.core.handlers.output.OutputHandler)}
 * Used when needed to return Procedure/Function return and OUT parameters at once
 */
public class CallResults<T, S> {
	private T callInput;
	private S callOutput;
	private final String procedureName;
	private final boolean isFunction;

    /**
     * Creates new CallResults instance
     *
     * @param procedureName Stored Procedure/Function name
     * @param isFunction true if it is Stored Function
     */
	public CallResults(String procedureName, boolean isFunction) {
		this.procedureName = procedureName;
		this.isFunction = isFunction;
	}

	public T getCallInput() {
		return callInput;
	}
	public void setCallInput(T callInput) {
		this.callInput = callInput;
	}

	public S getCallOutput() {
		return callOutput;
	}
	public void setCallOutput(S callOutput) {
		this.callOutput = callOutput;
	}

	public String getProcedureName() {
		return procedureName;
	}
	public boolean isFunction() {
		return isFunction;
	}
}
