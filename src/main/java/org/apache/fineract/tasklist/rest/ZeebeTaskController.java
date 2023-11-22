/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fineract.tasklist.rest;

import com.nimbusds.jwt.SignedJWT;
import org.apache.fineract.tasklist.dto.ClaimTasksResponse;
import org.apache.fineract.tasklist.dto.ZeebeTaskListDto;
import org.apache.fineract.tasklist.service.ZeebeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.fineract.utils.JwtUtil.getRoles;
import static org.apache.fineract.utils.JwtUtil.getSignedJwt;
import static org.apache.fineract.utils.JwtUtil.getUsername;

@RestController
@RequestMapping("/api/v1/tasks")
public class ZeebeTaskController {

    @Autowired
    private ZeebeTaskService zeebeTaskService;

    @RequestMapping(path = "/{id}/submit", method = RequestMethod.POST)
    public void completeTask(@PathVariable("id") long id,
                             @RequestBody String variablesJson,
                             @RequestHeader(name = "Authorization") String jwtToken) {

        String username = getUsername(jwtToken);
        zeebeTaskService.completeTask(id, variablesJson, username);
    }


    @RequestMapping(path = "/{id}/claim", method = RequestMethod.POST)
    public void claimTask(@PathVariable("id") Long id,
                          @RequestHeader(name = "Authorization") String jwtToken) {

        SignedJWT signedJwt = getSignedJwt(jwtToken);
        String username = getUsername(signedJwt);
        List<String> userRoles = getRoles(signedJwt);

        zeebeTaskService.claimTask(id, username, userRoles);
    }



    @RequestMapping(path = "/claim", method = RequestMethod.POST)
    public ClaimTasksResponse claimTasks(@RequestHeader(name = "Authorization") String jwtToken,
                           @RequestBody List<Long> ids) {
        SignedJWT signedJwt = getSignedJwt(jwtToken);
        String userName = getUsername(signedJwt);
        List<String> userRoles = getRoles(signedJwt);
        return zeebeTaskService.claimTasks(userName, userRoles, ids);
    }

    @RequestMapping(path = "/unclaim", method = RequestMethod.POST)
    public ClaimTasksResponse unclaimTasks(@RequestHeader(name = "Authorization") String jwtToken,
                             @RequestBody List<Long> ids) {
        String userName = getUsername(jwtToken);
        return zeebeTaskService.unClaimTasks(ids, userName);
    }



    @GetMapping(path = "/list")
    public Page<ZeebeTaskListDto> getTasks(@RequestHeader(name = "Authorization") String jwtToken,
                                           @PageableDefault(size = 10) Pageable pageable,
                                           @RequestParam(required = false) String taskState,
                                           @RequestParam(required = false) String assignee,
                                           @RequestParam(required = false) String candidateRole,
                                           @RequestParam(required = false) String previousSubmitter,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String businessKey) {

        String username = getUsername(jwtToken);
        return zeebeTaskService.getTasks(pageable, taskState, assignee, candidateRole, previousSubmitter, name, businessKey, username);
    }



    @GetMapping(path = "/me")
    public Page<ZeebeTaskListDto> myTaskList(@RequestHeader(name = "Authorization") String jwtToken,
                                             @PageableDefault(size = 10) Pageable pageable,
                                             @RequestParam(required = false) String name) {

        String username = getUsername(jwtToken);
        return zeebeTaskService.myTaskList(pageable, name, username);
    }



    @GetMapping(path = "/{id}")
    public ZeebeTaskListDto getTask(@RequestHeader(name = "Authorization") String jwtToken,
                                   @PathVariable("id") Long id) {

        final String username = getUsername(jwtToken);
        return zeebeTaskService.getTask(id, username);
    }
}
