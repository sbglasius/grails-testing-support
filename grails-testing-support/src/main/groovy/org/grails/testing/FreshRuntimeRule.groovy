/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.grails.testing

import grails.test.runtime.FreshRuntime
import grails.test.runtime.TestRuntime
import grails.test.runtime.TestRuntimeFactory
import groovy.transform.CompileStatic
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@CompileStatic
class FreshRuntimeRule implements TestRule {

    @Override
    Statement apply(Statement statement, Description description) {
        return new Statement() {
            public void evaluate() throws Throwable {
                Class testClass = description.getTestClass()
                TestRuntime runtime = TestRuntimeFactory.getRuntimeForTestClass(testClass)
                handleFreshContextAnnotation(runtime, description)
                try {
                    statement.evaluate()
                } catch (Throwable t) {
                }
            }
        }
    }

    protected handleFreshContextAnnotation(TestRuntime runtime, Description description, Map eventArguments = [:]) {
        if (doesRequireFreshContext(description)) {
            runtime.publishEvent('requestFreshRuntime', eventArguments, [immediateDelivery: true])
        }
    }

    protected boolean doesRequireFreshContext(Description testDescription) {
        if (testDescription?.getAnnotation(FreshRuntime) || testDescription?.getTestClass()?.isAnnotationPresent(FreshRuntime)) {
            return true
        }
        return false
    }
}