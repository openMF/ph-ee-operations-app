package org.apache.fineract.api;

import com.baasflow.commons.events.EventLogLevel;
import com.baasflow.commons.events.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.data.ErrorResponse;
import org.apache.fineract.exception.WriteToCsvException;
import org.apache.fineract.operations.*;
import org.apache.fineract.operations.TransferDto;
import org.apache.fineract.utils.CsvUtility;
import org.apache.fineract.utils.SortOrder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.fineract.core.service.OperatorUtils.dateFormat;


@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "auth")
@Tag(name = "Operations Detailed API")
@SecurityRequirement(name = "api")
public class OperationsDetailedApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${payment.internal-account-id-prefix}")
    private String internalAccountIdPrefix;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransactionRequestRepository transactionRequestRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;


    @PreAuthorize("hasAuthority('ALL_FUNCTIONS') and hasRole('Admin')")
    @GetMapping("/transfers")
    public Page<TransferDto> transfers(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "payerPartyId", required = false) String _payerPartyId,
            @RequestParam(value = "payerDfspId", required = false) String payerDfspId,
            @RequestParam(value = "payeePartyId", required = false) String _payeePartyId,
            @RequestParam(value = "payeeDfspId", required = false) String payeeDfspId,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "businessProcessStatus", required = false) String businessProcessStatus,
            @RequestParam(value = "paymentScheme", required = false) String paymentScheme,
            @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "startFrom", required = false) String startFrom,
            @RequestParam(value = "startTo", required = false) String startTo,
            @RequestParam(value = "acceptanceDateFrom", required = false) String acceptanceDateFrom,
            @RequestParam(value = "acceptanceDateTo", required = false) String acceptanceDateTo,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "sortedBy", required = false) String sortedBy,
            @RequestParam(value = "partyId", required = false) String _partyId,
            @RequestParam(value = "partyIdType", required = false) String partyIdType,
            @RequestParam(value = "sortedOrder", required = false, defaultValue = "DESC") String sortedOrder,
            @RequestParam(value = "endToEndIdentification", required = false) String endToEndIdentification) {
        return eventService.auditedEvent(event -> event
                .setEvent("transfers list invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> loadTransfers(Transfer.TransferType.TRANSFER, page, size, _payerPartyId, payerDfspId, _payeePartyId, payeeDfspId, transactionId, status, null, null, businessProcessStatus, paymentScheme, currency, amountFrom, amountTo, startFrom, startTo, acceptanceDateFrom, acceptanceDateTo, direction, sortedBy, _partyId, partyIdType, sortedOrder, endToEndIdentification, null));
    }

    @GetMapping("/recalls")
    public Page<TransferDto> recalls(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "payerPartyId", required = false) String _payerPartyId,
            @RequestParam(value = "payerDfspId", required = false) String payerDfspId,
            @RequestParam(value = "payeePartyId", required = false) String _payeePartyId,
            @RequestParam(value = "payeeDfspId", required = false) String payeeDfspId,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "recallStatus", required = false) String recallStatus,
            @RequestParam(value = "recallDirection", required = false) String recallDirection,
            @RequestParam(value = "businessProcessStatus", required = false) String businessProcessStatus,
            @RequestParam(value = "paymentScheme", required = false) String paymentScheme,
            @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "startFrom", required = false) String startFrom,
            @RequestParam(value = "startTo", required = false) String startTo,
            @RequestParam(value = "acceptanceDateFrom", required = false) String acceptanceDateFrom,
            @RequestParam(value = "acceptanceDateTo", required = false) String acceptanceDateTo,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "sortedBy", required = false) String sortedBy,
            @RequestParam(value = "partyId", required = false) String _partyId,
            @RequestParam(value = "partyIdType", required = false) String partyIdType,
            @RequestParam(value = "sortedOrder", required = false, defaultValue = "DESC") String sortedOrder,
            @RequestParam(value = "endToEndIdentification", required = false) String endToEndIdentification) {
        return eventService.auditedEvent(event -> event
                .setEvent("recalls list invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event ->
                loadTransfers(Transfer.TransferType.RECALL, page, size, _payerPartyId, payerDfspId, _payeePartyId, payeeDfspId, transactionId, status, recallStatus, recallDirection, businessProcessStatus, paymentScheme, currency, amountFrom, amountTo, startFrom, startTo, acceptanceDateFrom, acceptanceDateTo, direction, sortedBy, _partyId, partyIdType, sortedOrder, endToEndIdentification, null));
    }

    @GetMapping("/transactionRequests")
    public Page<TransferDto> transactionRequests(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "payerPartyId", required = false) String _payerPartyId,
            @RequestParam(value = "payeePartyId", required = false) String _payeePartyId,
            @RequestParam(value = "payeeDfspId", required = false) String payeeDfspId,
            @RequestParam(value = "payerDfspId", required = false) String payerDfspId,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "startFrom", required = false) String startFrom,
            @RequestParam(value = "startTo", required = false) String startTo,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "rtpDirection", required = false) String rtpDirection,
            @RequestParam(value = "sortedBy", required = false) String sortedBy,
            @RequestParam(value = "sortedOrder", required = false, defaultValue = "DESC") String sortedOrder) {
        return eventService.auditedEvent(event -> event
                .setEvent("recalls list invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event ->
                loadTransfers(Transfer.TransferType.REQUEST_TO_PAY, page, size, _payerPartyId, payerDfspId, _payeePartyId, payeeDfspId, transactionId, status, null, null, null, null, currency, amountFrom, amountTo, startFrom, startTo, null, null, direction, sortedBy, null, null, sortedOrder, null, rtpDirection));
    }

    private Page<TransferDto> loadTransfers(Transfer.TransferType transferType, Integer page, Integer size, String _payerPartyId, String payerDfspId, String _payeePartyId, String payeeDfspId, String transactionId, String status, String recallStatus, String recallDirection, String businessProcessStatus, String paymentScheme, String currency, BigDecimal amountFrom, BigDecimal amountTo, String startFrom, String startTo, String acceptanceDateFrom, String acceptanceDateTo, String direction, String sortedBy, String _partyId, String partyIdType, String sortedOrder, String endToEndIdentification, String rtpDirection) {
        String payerPartyId = _payerPartyId;
        String payeePartyId = _payeePartyId;
        String partyId = _partyId;

        List<Specification<Transfer>> specs = new ArrayList<>();

        specs.add((Specification<Transfer>) (root, query, cb) -> {
            Predicate finalPredicate = null;
            switch (transferType) {
                case TRANSFER:
                    finalPredicate = cb.and(cb.isNull(root.get(Transfer_.recallDirection)), cb.isNull(root.get(Transfer_.rtpDirection)));
                    break;
                case RECALL:
                    if (recallDirection == null) {
                        finalPredicate = cb.and(cb.or(cb.isNotNull(root.get(Transfer_.recallDirection))), cb.isNull(root.get(Transfer_.rtpDirection)));
                    } else {
                        finalPredicate = cb.and(cb.or(cb.equal(root.get(Transfer_.recallDirection), recallDirection)), cb.isNull(root.get(Transfer_.rtpDirection)));
                    }
                    break;
                case REQUEST_TO_PAY:
                    finalPredicate = cb.equal(root.get(Transfer_.rtpDirection), rtpDirection);
                    break;
            }
            return finalPredicate;
        });

        if (StringUtils.isNotBlank(payerPartyId)) {
            payerPartyId = urlDecode(payerPartyId, "Decoded payerPartyId: ");

            if (payerPartyId.length() == 8) {
                String likeTerm = "%" + payerPartyId;
                if (StringUtils.isNotBlank(internalAccountIdPrefix)) {
                    likeTerm = internalAccountIdPrefix + likeTerm;
                }
                logger.info("PayerPartyId is 8 characters long, using LIKE search to match internalAccountId: {}", likeTerm);
                specs.add(TransferSpecs.like(Transfer_.payerPartyId, likeTerm));
            } else {
                specs.add(TransferSpecs.match(Transfer_.payerPartyId, payerPartyId));
            }
        }
        if (StringUtils.isNotBlank(payeePartyId)) {
            payeePartyId = urlDecode(payeePartyId, "Decoded payeePartyId: ");

            if (payeePartyId.length() == 8) {
                String likeTerm = "%" + payeePartyId;
                if (StringUtils.isNotBlank(internalAccountIdPrefix)) {
                    likeTerm = internalAccountIdPrefix + likeTerm;
                }
                logger.info("PayeePartyId is 8 characters long, using LIKE search to match internalAccountId: {}", likeTerm);
                specs.add(TransferSpecs.like(Transfer_.payeePartyId, likeTerm));
            } else {
                specs.add(TransferSpecs.match(Transfer_.payeePartyId, payeePartyId));
            }
        }
        if (StringUtils.isNotBlank(payeeDfspId)) {
            specs.add(TransferSpecs.match(Transfer_.payeeDfspId, payeeDfspId));
        }
        if (StringUtils.isNotBlank(payerDfspId)) {
            specs.add(TransferSpecs.match(Transfer_.payerDfspId, payerDfspId));
        }
        if (StringUtils.isNotBlank(transactionId)) {
            specs.add(TransferSpecs.match(Transfer_.transactionId, transactionId));
        }
        if (status != null && parseStatus(status) != null) {
            specs.add(TransferSpecs.match(Transfer_.status, parseStatus(status)));
        }
        if (StringUtils.isNotBlank(recallStatus)) {
            specs.add(TransferSpecs.match(Transfer_.recallStatus, recallStatus));
        }
        if (StringUtils.isNotBlank(businessProcessStatus)) {
            specs.add(TransferSpecs.match(Transfer_.businessProcessStatus, businessProcessStatus));
        }
        if (amountFrom != null) {
            specs.add(TransferSpecs.greaterThanOrEqualTo(Transfer_.amount, amountFrom));
        }
        if (amountTo != null) {
            specs.add(TransferSpecs.lessThanOrEqualTo(Transfer_.amount, amountTo));
        }
        if (StringUtils.isNotBlank(currency)) {
            specs.add(TransferSpecs.match(Transfer_.currency, currency));
        }
        if (StringUtils.isNotBlank(direction)) {
            if (Transfer.TransferType.TRANSFER.equals(transferType)) {
                if (StringUtils.isNotBlank(paymentScheme)) {
                    specs.add(TransferSpecs.match(Transfer_.direction, direction));
                    specs.add((Specification<Transfer>) (root, query, cb) -> {
                        Join<Transfer, Variable> transferVariables = root.join(Transfer_.variables, JoinType.LEFT);
                        transferVariables.on(cb.equal(transferVariables.get(Variable_.name), "paymentScheme"));
                        return cb.equal(transferVariables.get(Variable_.value), paymentScheme);
                    });
                } else {
                    specs.add((Specification<Transfer>) (root, query, cb) -> {
                        Join<Transfer, Variable> transferVariables = root.join(Transfer_.variables, JoinType.LEFT);
                        transferVariables.on(cb.equal(transferVariables.get(Variable_.name), "paymentScheme"));
                        return cb.or(cb.equal(transferVariables.get(Variable_.value), "ON_US"), cb.equal(root.get("direction"), direction));
                    });
                }
            } else {
                specs.add(TransferSpecs.match(Transfer_.direction, direction));
            }
        }
        if (StringUtils.isNotBlank(paymentScheme) && Transfer.TransferType.RECALL.equals(transferType)) {
            specs.add((Specification<Transfer>) (root, query, cb) -> {
                Join<Transfer, Variable> transferVariables = root.join(Transfer_.variables, JoinType.LEFT);
                transferVariables.on(cb.equal(transferVariables.get(Variable_.name), "paymentScheme"));
                return cb.equal(transferVariables.get(Variable_.value), paymentScheme);
            });
        }
        if (StringUtils.isNotBlank(partyIdType)) {
            specs.add(TransferSpecs.multiMatch(Transfer_.payeePartyIdType, Transfer_.payerPartyIdType, partyIdType));
        }
        if (StringUtils.isNotBlank(partyId)) {
            partyId = urlDecode(partyId, "Decoded PartyId: ");
            specs.add(TransferSpecs.multiMatch(Transfer_.payerPartyId, Transfer_.payeePartyId, partyId));
        }
        try {
            if (startFrom != null && startTo != null) {
                specs.add(TransferSpecs.between(Transfer_.startedAt, dateFormat().parse(startFrom), dateFormat().parse(startTo)));
            } else if (startFrom != null) {
                specs.add(TransferSpecs.later(Transfer_.startedAt, dateFormat().parse(startFrom)));
            } else if (startTo != null) {
                specs.add(TransferSpecs.earlier(Transfer_.startedAt, dateFormat().parse(startTo)));
            }
        } catch (Exception e) {
            logger.warn("failed to parse dates {} / {}", startFrom, startTo);
        }

        try {
            if (acceptanceDateFrom != null && acceptanceDateTo != null) {
                specs.add(TransferSpecs.between(Transfer_.acceptanceDate, dateFormat().parse(acceptanceDateFrom), dateFormat().parse(acceptanceDateTo)));
            } else if (acceptanceDateFrom != null) {
                specs.add(TransferSpecs.later(Transfer_.acceptanceDate, dateFormat().parse(acceptanceDateFrom)));
            } else if (acceptanceDateTo != null) {
                specs.add(TransferSpecs.earlier(Transfer_.acceptanceDate, dateFormat().parse(acceptanceDateTo)));
            }
        } catch (Exception e) {
            logger.warn("failed to parse dates {} / {}", acceptanceDateFrom, acceptanceDateTo);
        }

        if (StringUtils.isNotBlank(endToEndIdentification)) {
            specs.add(TransferSpecs.match(Transfer_.endToEndIdentification, endToEndIdentification));
        }

        logger.info("finding transfers based on {} specs", specs.size());

        Specification<Transfer> combinedSpec = Specification.where(null);

        for (Specification<Transfer> spec : specs) {
            combinedSpec = combinedSpec.and(spec);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        Root<Transfer> root = cq.from(Transfer.class);

        List<Selection<?>> tupleList = new ArrayList<>();
        tupleList.add(root.get(Transfer_.workflowInstanceKey).alias(Transfer_.workflowInstanceKey.getName()));
        tupleList.add(root.get(Transfer_.startedAt).alias(Transfer_.startedAt.getName()));
        tupleList.add(root.get(Transfer_.completedAt).alias(Transfer_.completedAt.getName()));
        tupleList.add(root.get(Transfer_.acceptanceDate).alias(Transfer_.acceptanceDate.getName()));
        tupleList.add(root.get(Transfer_.transactionId).alias(Transfer_.transactionId.getName()));
        tupleList.add(root.get(Transfer_.payerPartyId).alias(Transfer_.payerPartyId.getName()));
        tupleList.add(root.get(Transfer_.payeePartyId).alias(Transfer_.payeePartyId.getName()));
        tupleList.add(root.get(Transfer_.payerDfspId).alias(Transfer_.payerDfspId.getName()));
        tupleList.add(root.get(Transfer_.payeeDfspId).alias(Transfer_.payeeDfspId.getName()));
        tupleList.add(root.get(Transfer_.amount).alias(Transfer_.amount.getName()));
        tupleList.add(root.get(Transfer_.currency).alias(Transfer_.currency.getName()));
        tupleList.add(root.get(Transfer_.status).alias(Transfer_.status.getName()));
        tupleList.add(root.get(Transfer_.recallStatus).alias(Transfer_.recallStatus.getName()));
        tupleList.add(root.get(Transfer_.recallDirection).alias(Transfer_.recallDirection.getName()));

        if (Transfer.TransferType.TRANSFER.equals(transferType)) {
            Join<Transfer, Transfer> transferRecalls = root.join(Transfer_.recalls, JoinType.LEFT);
            transferRecalls.on(cb.isNotNull(transferRecalls.get(Transfer_.recallDirection)));
            tupleList.add(cb.count(transferRecalls).alias(Transfer_.recalls.getName()));
            cq.groupBy(root.get(Transfer_.workflowInstanceKey), transferRecalls.get(Transfer_.transactionId));
        }

        if (StringUtils.isEmpty(sortedBy)) {
            sortedBy = Transfer_.startedAt.getName();
        }

        cq.select(cb.tuple((tupleList.toArray(new Selection[]{}))))
                .where(combinedSpec.toPredicate(root, cq, cb))
                .orderBy(getSortDirection(cb, root, sortedOrder, sortedBy));

        List<Tuple> resultList = entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
        Root<Transfer> countRoot = cqCount.from(Transfer.class);
        Long count = entityManager.createQuery(cqCount.select(cb.count(countRoot)).where(combinedSpec.toPredicate(countRoot, cqCount, cb))).getSingleResult();

        return new PageImpl<>(resultList.stream()
                .map(t -> new TransferDto(t)).collect(Collectors.toList()), PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortedOrder), sortedBy)), count);
    }

    private Order getSortDirection(CriteriaBuilder cb, Root<?> root, String direction, String sortedBy) {
        if (SortOrder.DESC.name().equalsIgnoreCase(direction)) {
            return cb.desc(root.get(sortedBy));
        } else {
            return cb.asc(root.get(sortedBy));
        }
    }

    private String urlDecode(String variable, String logPrefix) {
        if (variable.contains("%2B")) {
            try {
                variable = URLDecoder.decode(variable, "UTF-8");
                logger.info(logPrefix + variable);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return variable;
    }

    /**
     * Filter the [TransactionRequests] based on multiple type of ids
     *
     * @param response    instance of HttpServletResponse
     * @param page        the count/number of page which we want to fetch
     * @param size        the size of the single page defaults to [10000]
     * @param sortedOrder the order of sorting [ASC] or [DESC], defaults to [DESC]
     * @param startFrom   use for filtering records after this date, format: "yyyy-MM-dd HH:mm:ss"
     * @param startTo     use for filtering records before this date
     * @param state       filter based on state of the transaction
     */
    @PostMapping("/transactionRequests")
    public Map<String, String> filterTransactionRequests(
            HttpServletResponse response,
            @RequestParam(value = "command", required = false, defaultValue = "export") String command,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10000") Integer size,
            @RequestParam(value = "sortedOrder", required = false, defaultValue = "DESC") String sortedOrder,
            @RequestParam(value = "startFrom", required = false) String startFrom,
            @RequestParam(value = "startTo", required = false) String startTo,
            @RequestParam(value = "state", required = false) String state,
            @RequestBody Map<String, List<String>> body) {

        return eventService.auditedEvent(event -> event
                .setEvent("transaction requests search invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            if (!command.equalsIgnoreCase("export")) {
                return new ErrorResponse.Builder()
                        .setErrorCode("" + HttpServletResponse.SC_NOT_FOUND)
                        .setErrorDescription(command + " not supported")
                        .setDeveloperMessage("Possible supported command is " + command).build();
            }

            List<String> filterByList = new ArrayList<>(body.keySet());

            List<Specification<TransactionRequest>> specs = new ArrayList<>();
            if (state != null && parseState(state) != null) {
                specs.add(TransactionRequestSpecs.match(TransactionRequest_.state, parseState(state)));
                logger.info("State filter added");
            }
            try {
                specs.add(getDateSpecification(startTo, startFrom));
                logger.info("Date filter parsed successful");
            } catch (Exception e) {
                logger.warn("failed to parse dates {} / {}", startFrom, startTo);
            }

            Specification<TransactionRequest> spec = null;
            List<TransactionRequest> data = new ArrayList<>();
            for (String filterBy : filterByList) {
                List<String> ids = body.get(filterBy);
                if (ids.isEmpty()) {
                    continue;
                }
                Filter filter;
                try {
                    filter = parseFilter(filterBy);
                    logger.info("Filter parsed successfully " + filter.name());
                } catch (Exception e) {
                    logger.info("Unable to parse filter " + filterBy + " skipping");
                    continue;
                }
                spec = getFilterSpecs(filter, ids);
                Page<TransactionRequest> result = executeRequest(spec, specs, page, size, sortedOrder);
                data.addAll(result.getContent());
                logger.info("Result for " + filter + " : " + data);
            }
            if (data.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new ErrorResponse.Builder()
                        .setErrorCode("" + HttpServletResponse.SC_NOT_FOUND)
                        .setErrorDescription("Empty response")
                        .setDeveloperMessage("Empty response").build();
            }
            try {
                CsvUtility.writeToCsv(response, data);
            } catch (WriteToCsvException e) {
                return new ErrorResponse.Builder()
                        .setErrorCode(e.getErrorCode())
                        .setErrorDescription(e.getErrorDescription())
                        .setDeveloperMessage(e.getDeveloperMessage()).build();
            }
            return null;
        });
    }

    /*
     * Returns respective [TransactionRequest] Specification based on filter
     * @param filter the filter we want to apply
     * @param listOfValues the values to which we want to apply filter
     */
    private Specification<TransactionRequest> getFilterSpecs(Filter filter, List<String> listOfValues) {
        Specification<TransactionRequest> spec = null;
        switch (filter) {
            case TRANSACTIONID:
                spec = TransactionRequestSpecs.in(TransactionRequest_.transactionId, listOfValues);
                break;
            case PAYERID:
                spec = TransactionRequestSpecs.in(TransactionRequest_.payerPartyId, listOfValues);
                break;
            case PAYEEID:
                spec = TransactionRequestSpecs.in(TransactionRequest_.payeePartyId, listOfValues);
                break;
            case WORKFLOWINSTANCEKEY:
                spec = TransactionRequestSpecs.in(TransactionRequest_.workflowInstanceKey, listOfValues);
                break;
            case STATE:
                spec = TransactionRequestSpecs.in(TransactionRequest_.state, parseStates(listOfValues));
                break;
            case ERRORDESCRIPTION:
                spec = TransactionRequestSpecs.filterByErrorDescription(parseErrorDescription(listOfValues));
                break;
            case EXTERNALID:
                spec = TransactionRequestSpecs.in(TransactionRequest_.externalId, listOfValues);
                break;
        }
        return spec;
    }

    /*
     * Parse the date filter and return the specification accordingly
     * @param startTo date before which we want all the records, in format "yyyy-MM-dd HH:mm:ss"
     * @param startFrom date after which we want all the records, in format "yyyy-MM-dd HH:mm:ss"
     */
    private Specification<TransactionRequest> getDateSpecification(String startTo, String startFrom) throws Exception {
        if (startFrom != null && startTo != null) {
            return TransactionRequestSpecs.between(TransactionRequest_.startedAt, dateFormat().parse(startFrom), dateFormat().parse(startTo));
        } else if (startFrom != null) {
            return TransactionRequestSpecs.later(TransactionRequest_.startedAt, dateFormat().parse(startFrom));
        } else if (startTo != null) {
            return TransactionRequestSpecs.earlier(TransactionRequest_.startedAt, dateFormat().parse(startTo));
        } else {
            throw new Exception("Both dates(startTo, startFrom empty, skipping");
        }
    }

    /*
     * Executes the transactionRequest api request with Specification and returns the paged result
     * @param baseSpec the base specification in which all the other spec needed to be merged
     * @param extraSpecs the list of specification which is required to be merged in [baseSpec]
     * @param page the page number we want to fetch
     * @param size the size of single page or number of elements in single page
     * @param sortedOrder the order of sorting to be applied ASC OR DESC
     */
    private Page<TransactionRequest> executeRequest(
            Specification<TransactionRequest> baseSpec, List<Specification<TransactionRequest>> extraSpecs,
            int page, int size, String sortedOrder) {
        PageRequest pager = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sortedOrder), "startedAt"));
        Page<TransactionRequest> result;
        if (baseSpec == null) {
            result = transactionRequestRepository.findAll(pager);
            logger.info("Getting data without spec");
        } else {
            Specification<TransactionRequest> combineSpecs = combineSpecs(baseSpec, extraSpecs);
            result = transactionRequestRepository.findAll(combineSpecs, pager);
        }
        return result;
    }

    /*
     * Combines the multiple Specification into one using and clause
     * @param baseSpec the base specification in which all the other spec needed to be merged
     * @param specs the list of specification which is required to be merged in [baseSpec]
     */
    private <T> Specification<T> combineSpecs(Specification<T> baseSpec,
                                              List<Specification<T>> specs) {
        logger.info("Combining specs " + specs.size());
        for (Specification<T> Specification : specs) {
            baseSpec = baseSpec.and(Specification);
        }
        return baseSpec;
    }

    /*
     * Generates the exhaustive errorDescription list by prefixing and suffixing it with double quotes (")
     *
     * Example: [ "AMS Local is disabled"] => [ "AMS Local is disabled", "\"AMS Local is disabled\""]
     */
    private List<String> parseErrorDescription(List<String> description) {
        List<String> errorDesc = new ArrayList<>(description);
        for (String s : description) {
            errorDesc.add(String.format("\"%s\"", s));
        }
        return errorDesc;
    }

    /*
     * Parses the [Filter] enum from filter string
     */
    private Filter parseFilter(String filterBy) {
        return filterBy == null ? null : Filter.valueOf(filterBy.toUpperCase());
    }

    /*
     * Parses the [TransferStatus] enum from transactionStatus string
     */
    private TransferStatus parseStatus(@RequestParam(value = "transactionStatus", required = false) String
                                               transactionStatus) {
        try {
            return transactionStatus == null ? null : TransferStatus.valueOf(transactionStatus);
        } catch (Exception e) {
            logger.warn("failed to parse transaction status {}, ignoring it", transactionStatus);
            return null;
        }
    }

    /*
     * Parses the [TransactionRequestState] enum from transactionState string
     */
    private TransactionRequestState parseState(String state) {
        try {
            return state == null ? null : TransactionRequestState.valueOf(state);
        } catch (Exception e) {
            logger.warn("failed to parse TransactionRequestState {}, ignoring it", state);
            return null;
        }
    }

    /*
     * Parses the list of [TransactionRequestState] enum from list of transactionState string
     */
    private List<TransactionRequestState> parseStates(List<String> states) {
        List<TransactionRequestState> stateList = new ArrayList<>();
        for (String state : states) {
            stateList.add(parseState(state));
        }
        return stateList;
    }
}
