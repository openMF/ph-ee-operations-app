/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.commands.domain;

import java.util.HashMap;
import java.util.Map;

public final class CommandActionContext {

    private final String entityName;
    private final String actionName;
    private Map<String, Object> attributes;

    private CommandActionContext(String entityName, String actionName) {
        this.entityName = entityName;
        this.actionName = actionName;
        this.attributes = new HashMap<>();
    }

    public static CommandActionContext create(String entityName, String actionName) {
        return new CommandActionContext(entityName, actionName);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getActionName() {
        return actionName;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
