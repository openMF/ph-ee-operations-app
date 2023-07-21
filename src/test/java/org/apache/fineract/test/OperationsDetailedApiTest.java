package org.apache.fineract.test;

import org.apache.fineract.api.OperationsDetailedApi;
import org.apache.fineract.operations.TransactionRequest;
import org.apache.fineract.operations.TransactionRequestRepository;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

class OperationsDetailedApiTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private TransactionRequestRepository transactionRequestRepository;

    @InjectMocks
    private OperationsDetailedApi operationsDetailedApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setupSecurityContext(AppUser principal) {
        Authentication authentication = new PreAuthenticatedAuthenticationToken(principal, "token", null);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    void testTransactionRequestFilter_UserNotAuthenticated() {
        // Arrange
        List<Specifications<TransactionRequest>> specs = Collections.emptyList();
        String sortedBy = null;
        String sortedOrder = "ASC";
        Integer page = 0;
        Integer size = 10;
        String currency = "USD";
        String payeePartyId = "12345";

        // Arrange
        // Set up specs, sortedBy, sortedOrder, page, size, currency, payeePartyId, etc.
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Page<TransactionRequest> result = operationsDetailedApi.transactionRequestFilter(specs, sortedBy, sortedOrder, page, size, currency, payeePartyId);

        // Assert
        verifyZeroInteractions(appUserRepository, transactionRequestRepository);
        assertEquals(0, result.getTotalElements());
        // Add more assertions based on your logic when the user is not authenticated
    }

    @Test
    void testTransactionRequestFilter_UserNotAuthorizedForCurrencies() {
        List<Specifications<TransactionRequest>> specs = Collections.emptyList();
        String sortedBy = null;
        String sortedOrder = "ASC";
        Integer page = 0;
        Integer size = 10;
        String currency = "USD";
        String payeePartyId = "12345";

        AppUser currentUser = new AppUser();
        when(appUserRepository.findAppUserByName(any())).thenReturn(currentUser);
        currentUser.setCurrenciesList(null); // User not authorized for currencies

        setupSecurityContext(currentUser);
        // Act
        Page<TransactionRequest> result = operationsDetailedApi.transactionRequestFilter(specs, sortedBy, sortedOrder, page, size, currency, payeePartyId);

        // Assert
        verify(appUserRepository, times(1)).findAppUserByName(any());
        verifyZeroInteractions(transactionRequestRepository);
        assertEquals(0, result.getTotalElements());
        // Add more assertions based on your logic when the user is not authorized for currencies
    }

    @Test
    void testTransactionRequestFilter_UserNotAuthorizedForPayeePartyIds() {
        List<Specifications<TransactionRequest>> specs = Collections.emptyList();
        String sortedBy = null;
        String sortedOrder = "ASC";
        Integer page = 0;
        Integer size = 10;
        String currency = "USD";
        String payeePartyId = "12345";

        AppUser currentUser = new AppUser();
        when(appUserRepository.findAppUserByName(any())).thenReturn(currentUser);
        currentUser.setPayeePartyIdsList(null); // User not authorized for currencies

        setupSecurityContext(currentUser);
        // Act
        Page<TransactionRequest> result = operationsDetailedApi.transactionRequestFilter(specs, sortedBy, sortedOrder, page, size, currency, payeePartyId);

        // Assert
        verify(appUserRepository, times(1)).findAppUserByName(any());
        verifyZeroInteractions(transactionRequestRepository);
        assertEquals(0, result.getTotalElements());
        // Add more assertions based on your logic when the user is not authorized for currencies
    }

    @Test
    void testTransactionRequestFilter_Successful() {
        List<Specifications<TransactionRequest>> specs = Collections.emptyList();
        String sortedBy = null;
        String sortedOrder = "ASC";
        Integer page = 0;
        Integer size = 10;
        String currency = null;
        String payeePartyId = null;
        PageRequest pager = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sortedOrder), "startedAt"));


        AppUser currentUser = new AppUser();
        currentUser.setPayeePartyIdsList(Collections.singletonList("*"));
        currentUser.setCurrenciesList(Collections.singletonList("*"));

        setupSecurityContext(currentUser);
        when(appUserRepository.findAppUserByName(any())).thenReturn(currentUser);

        // Act
        Page<TransactionRequest> result = operationsDetailedApi.transactionRequestFilter(specs, sortedBy, sortedOrder, page, size, currency, payeePartyId);

        // Assert
        verify(appUserRepository, times(1)).findAppUserByName(any());

        // You can also directly assert on the result returned by the method if it is equal to the expectedPage
//        Assertions.assertNotNull(result);
        verify(transactionRequestRepository, times(1)).findAll(pager);
    }
}

