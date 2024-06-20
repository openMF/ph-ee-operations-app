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
package org.apache.fineract.organisation.monetary.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
@Component
public class MoneyHelper {

    private static RoundingMode roundingMode = null;
    private static MathContext mathContext;
    private static final int PRECISION = 12;


    public static RoundingMode getRoundingMode() {
        if (roundingMode == null) {
            roundingMode = RoundingMode.valueOf(6);
        }
        return roundingMode;
    }

    public static MathContext getMathContext() {
        if (mathContext == null) {
            mathContext = new MathContext(PRECISION, getRoundingMode());
        }
        return mathContext;
    }

}
