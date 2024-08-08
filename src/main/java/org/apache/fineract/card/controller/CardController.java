package org.apache.fineract.card.controller;

import com.baasflow.commons.events.EventLogLevel;
import com.baasflow.commons.events.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.card.dto.CardTransactionDetail;
import org.apache.fineract.card.dto.CardTransactionDto;
import org.apache.fineract.card.entity.CardTransaction;
import org.apache.fineract.card.entity.PaymentScheme;
import org.apache.fineract.card.repository.CardTransactionRepository;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.operations.*;
import org.apache.fineract.operations.converter.TimestampToStringConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1")
@Slf4j
public class CardController {

    @Autowired
    private CardTransactionRepository cardTransactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EventService eventService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.addConverter(new TimestampToStringConverter());
    }

    @GetMapping("/cardTransaction/{workflowInstanceKey}")
    public CardTransactionDetail cardTransactionDetails(@PathVariable String workflowInstanceKey) {
        log.info("cardTransactionDetails called with workflowInstanceKey {}", workflowInstanceKey);
        return eventService.auditedEvent(event -> event
                .setEvent("cardTransactionDetails invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(workflowInstanceKey)
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            // TODO this.context.jwt().validateHasReadPermission(TRANSACTION_DETAILS_RESOURCE_NAME);
            return loadCardTransactionDetail(workflowInstanceKey);
        });
    }

    private CardTransactionDetail loadCardTransactionDetail(String workflowInstanceKey) {
        CardTransaction cardTransaction = cardTransactionRepository.findByWorkflowInstanceKey(workflowInstanceKey);
        CardTransactionDto cardTransactionDto = modelMapper.map(cardTransaction, CardTransactionDto.class);
        List<TaskDto> tasks;
        List<VariableDto> variables;
        if (PaymentScheme.CARD_CLEARING.equals(cardTransaction.getPaymentScheme())) {
            tasks = taskRepository.findByWorkflowInstanceKeyOrderByTimestamp(Long.valueOf(workflowInstanceKey))
                    .stream()
                    .map(t -> modelMapper.map(t, TaskDto.class))
                    .toList();
            variables = variableRepository.findByWorkflowInstanceKeyOrderByName(Long.valueOf(workflowInstanceKey))
                    .stream()
                    .map(v -> modelMapper.map(v, VariableDto.class))
                    .toList();
        } else {
            tasks = Collections.emptyList();
            variables = Collections.emptyList();
        }
        return new CardTransactionDetail(cardTransactionDto, tasks, variables);
    }

    @GetMapping("/cardTransactions")
    public Page<CardTransactionDto> cardTransactions(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "direction") String direction) {
        log.info("cardTransactions called with page {} size {} direction {}", page, size, direction);
        return eventService.auditedEvent(event -> event
                .setEvent("cardTransactions list invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            // TODO this.context.jwt().validateHasReadPermission(TRANSACTION_RESOURCE_NAME);
            return loadCardTransactions(page,
                    size,
                    direction);
        });
    }

    private Page<CardTransactionDto> loadCardTransactions(Integer page,
                                                          Integer size,
                                                          String direction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CardTransaction> mainQuery = criteriaBuilder.createQuery(CardTransaction.class);
        List<Predicate> predicates = buildFilters(criteriaBuilder, mainQuery, direction);
        mainQuery.where(predicates.toArray(Predicate[]::new));
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDateTime").descending());
        List<CardTransactionDto> results = loadCardTransactions(mainQuery, pageable)
                .map(c -> modelMapper.map(c, CardTransactionDto.class))
                .toList();
        return new PageImpl<>(results, pageable, getTotal(criteriaBuilder, predicates));
    }

    private List<Predicate> buildFilters(CriteriaBuilder criteriaBuilder,
                                         CriteriaQuery<CardTransaction> mainQuery,
                                         String direction) {
        Root<CardTransaction> root = mainQuery.from(CardTransaction.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("direction"), direction));
        return predicates;
    }

    private Stream<CardTransaction> loadCardTransactions(CriteriaQuery<CardTransaction> mainQuery, Pageable pageable) {
        TypedQuery<CardTransaction> query = entityManager.createQuery(mainQuery);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultStream();
    }

    private long getTotal(CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<FileTransport> countRoot = countQuery.from(FileTransport.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}