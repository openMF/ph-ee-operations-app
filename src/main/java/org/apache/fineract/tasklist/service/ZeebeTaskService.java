package org.apache.fineract.tasklist.service;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.apache.fineract.tasklist.dto.ClaimTasksResponse;
import org.apache.fineract.tasklist.dto.ZeebeTaskListDto;
import org.apache.fineract.tasklist.entity.ZeebeTaskCandidateRole;
import org.apache.fineract.tasklist.entity.ZeebeTaskEntity;
import org.apache.fineract.tasklist.entity.ZeebeTaskSubmitter;
import org.apache.fineract.tasklist.repository.ZeebeTaskRepository;
import org.apache.fineract.utils.StringUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ZeebeTaskService {


    @Autowired
    private ZeebeTaskRepository zeebeTaskRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ZeebeClient zeebeClient;

    @Transactional
    public void completeTask(long id, String variablesJson, String username) {
        final ZeebeTaskEntity task =
                zeebeTaskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("No task found with id: " + id));

        if (!username.equals(task.getAssignee())) {
            throw new RuntimeException("Only the assignee can submit the task: " + id);
        }

        Set<ZeebeTaskSubmitter> previousSubmitters = task.getPreviousSubmitters();

        JSONObject variables = new JSONObject(variablesJson);
        final Map<String, Object> variableMap = new HashMap<>();
        variables.keySet().forEach(s -> variableMap.put(s, variables.get(s)));


        List<String> submitterUserIds = new ArrayList<>(previousSubmitters
                .stream()
                .map(zeebeTaskSubmitter -> zeebeTaskSubmitter.getId().getUserName()).toList());
        submitterUserIds.add(username);
        variableMap.put(StringUtil.submitterVariableName(task.getName()), String.join(",", submitterUserIds));
        zeebeClient.newCompleteCommand(id).variables(variableMap).send().join();
        zeebeTaskRepository.delete(task);
    }

    @Transactional
    public Page<ZeebeTaskListDto> getTasks(Pageable pageable, String taskState, String assignee, String candidateRole, String previousSubmitter, String name, String username) {
        AppUser appUser = appUserRepository.findAppUserByName(username);
        final List<String> roles = appUser.getRoles().stream().map(Role::getName).toList();
        Page<ZeebeTaskEntity> tasks;
        if (StringUtils.isEmpty(taskState) || "ALL".equals(taskState)) {
            tasks = zeebeTaskRepository.findAll(assignee, candidateRole, previousSubmitter, name, pageable);
        } else {
            if (roles.isEmpty()) {
                tasks = zeebeTaskRepository.findAllClaimableTask(username, assignee, candidateRole, previousSubmitter, name, pageable);
            } else {
                tasks = zeebeTaskRepository.findAllClaimableTask(username, roles, assignee, candidateRole, previousSubmitter, name, pageable);
            }
        }
        return tasks.map(zeebeTaskEntity -> mapTaskEntityToDto(zeebeTaskEntity, username, roles));
    }

    @Transactional
    public Page<ZeebeTaskListDto> myTaskList(Pageable pageable, String name, String username) {
        Page<ZeebeTaskEntity> tasks = zeebeTaskRepository.findMyTasks(username, name, pageable);
        return tasks.map(ZeebeTaskService::mapTaskEntityToDto);
    }

    @Transactional
    public ClaimTasksResponse claimTasks(String userName, List<String> userRoles, List<Long> ids) {
        List<ZeebeTaskEntity> tasks = zeebeTaskRepository.findAllByIdIn(ids);
        Set<String> failed = new HashSet<>();
        tasks.forEach(task -> {
            if (!isAssigneeEmpty(task) || isUserPreviousSubmitter(task, userName) || !userHaveCandidateRole(task, userRoles)) {
                failed.add(task.getEndToEndId());
                tasks.remove(task);
            } else {
                task.setAssignee(userName);
            }
        });
        zeebeTaskRepository.saveAll(tasks);

        ClaimTasksResponse claimTasksResponse = new ClaimTasksResponse();
        claimTasksResponse.setFailed(failed);
        claimTasksResponse.setSuccessful(tasks.stream().map(ZeebeTaskEntity::getEndToEndId).collect(Collectors.toSet()));
        return claimTasksResponse;
    }

    @Transactional
    public void claimTask(Long id, String username, List<String> userRoles) {
        final ZeebeTaskEntity task =
                zeebeTaskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("No task found with id: " + id));

        if (StringUtils.isNotEmpty(task.getAssignee())) {
            throw new RuntimeException("The task is already assigned: " + id);
        }

        Set<ZeebeTaskCandidateRole> candidateRoles = task.getCandidateRoles();

        boolean userHasProperRole = candidateRoles.stream().anyMatch(zeebeTaskCandidateRole -> userRoles.contains(zeebeTaskCandidateRole.getId().getRoleName()));

        if (!userHasProperRole) {
            throw new RuntimeException("User does not have an appropriate role to claim task: " + id);
        }

        Set<ZeebeTaskSubmitter> previousSubmitters = task.getPreviousSubmitters();
        boolean isUserPreviousSubmitter = previousSubmitters.stream().anyMatch(zeebeTaskSubmitter -> zeebeTaskSubmitter.getId().getUserName().equals(username));

        if (isUserPreviousSubmitter) {
            throw new RuntimeException("This user already submitted the task previously: " + id);
        }
        task.setAssignee(username);
        zeebeTaskRepository.save(task);
    }

    @Transactional
    public ClaimTasksResponse unClaimTasks(List<Long> ids, String userName) {
        List<ZeebeTaskEntity> tasks = zeebeTaskRepository.findAllByIdIn(ids);

        Set<String> failed = new HashSet<>();
        tasks.forEach(task -> {
            if (!userName.equals(task.getAssignee())) {
                failed.add(task.getEndToEndId());
                tasks.remove(task);
            } else {
                task.setAssignee(null);
            }
        });
        zeebeTaskRepository.saveAll(tasks);

        ClaimTasksResponse claimTasksResponse = new ClaimTasksResponse();
        claimTasksResponse.setFailed(failed);
        claimTasksResponse.setSuccessful(tasks.stream().map(ZeebeTaskEntity::getEndToEndId).collect(Collectors.toSet()));
        return claimTasksResponse;
    }

    @Transactional
    public ZeebeTaskListDto getTask(Long id, String username) {
        AppUser appUser = appUserRepository.findAppUserByName(username);
        final List<String> roles = appUser.getRoles().stream().map(Role::getName).toList();
        //TODO: runtimeexception should be replaced and return 404
        ZeebeTaskEntity zeebeTaskEntity = zeebeTaskRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        return mapTaskEntityToDto(zeebeTaskEntity, username, roles);
    }

    private static ZeebeTaskListDto mapTaskEntityToDto(ZeebeTaskEntity zeebeTaskEntity) {
        ZeebeTaskListDto zeebeTaskListDto = new ZeebeTaskListDto();
        zeebeTaskListDto.setId(zeebeTaskEntity.getId());
        zeebeTaskListDto.setName(zeebeTaskEntity.getName());
        zeebeTaskListDto.setDescription(zeebeTaskEntity.getDescription());
        zeebeTaskListDto.setTaskForm(zeebeTaskEntity.getTaskForm());
        zeebeTaskListDto.setFormData(zeebeTaskEntity.getFormData());
        zeebeTaskListDto.setTimestamp(zeebeTaskEntity.getTimestamp());
        zeebeTaskListDto.setAssignee(zeebeTaskEntity.getAssignee());
        zeebeTaskListDto.setEndToEndId(zeebeTaskEntity.getEndToEndId());
        zeebeTaskListDto.setCandidateRoles(zeebeTaskEntity.getCandidateRoles().stream().map(zeebeTaskCandidateRole -> zeebeTaskCandidateRole.getId().getRoleName()).collect(Collectors.toList()));
        zeebeTaskListDto.setPreviousSubmitters(zeebeTaskEntity.getPreviousSubmitters().stream().map(submitter -> submitter.getId().getUserName()).collect(Collectors.toList()));
        return zeebeTaskListDto;
    }

    private static ZeebeTaskListDto mapTaskEntityToDto(ZeebeTaskEntity zeebeTaskEntity, String userName, List<String> userRoles) {
        ZeebeTaskListDto zeebeTaskListDto = mapTaskEntityToDto(zeebeTaskEntity);

        boolean isAssigneeEmpty = isAssigneeEmpty(zeebeTaskEntity);
        boolean isUserPreviousSubmitter = isUserPreviousSubmitter(zeebeTaskEntity, userName);
        boolean userHaveCandidateRole = userHaveCandidateRole(zeebeTaskEntity, userRoles);
        zeebeTaskListDto.setAssignable(isAssigneeEmpty && !isUserPreviousSubmitter && userHaveCandidateRole);

        zeebeTaskListDto.setNotAssignableReason(new ArrayList<>());
        if (!isAssigneeEmpty) {
            zeebeTaskListDto.getNotAssignableReason().add("This task is already assigned");
        }

        if (isUserPreviousSubmitter) {
            zeebeTaskListDto.getNotAssignableReason().add("You already submitted this task");
        }

        if (!userHaveCandidateRole) {
            zeebeTaskListDto.getNotAssignableReason().add("You does not have the required role");
        }

        return zeebeTaskListDto;
    }

    private static boolean userHaveCandidateRole(ZeebeTaskEntity zeebeTaskEntity, List<String> userRoles) {
        return zeebeTaskEntity.getCandidateRoles() == null || zeebeTaskEntity.getCandidateRoles().isEmpty() || (userRoles != null &&
                zeebeTaskEntity.getCandidateRoles()
                        .stream()
                        .anyMatch(zeebeTaskCandidateRole -> userRoles.contains(zeebeTaskCandidateRole.getId().getRoleName())));
    }

    private static boolean isUserPreviousSubmitter(ZeebeTaskEntity zeebeTaskEntity, String userName) {
        return zeebeTaskEntity.getPreviousSubmitters().stream().anyMatch(submitter -> submitter.getId().getUserName().equals(userName));
    }

    private static boolean isAssigneeEmpty(ZeebeTaskEntity zeebeTaskEntity) {
        return StringUtils.isEmpty(zeebeTaskEntity.getAssignee());
    }
}
