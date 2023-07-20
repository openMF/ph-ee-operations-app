package org.apache.fineract.test;

import org.apache.fineract.api.UsersApi;
import org.apache.fineract.organisation.user.AppUser;
import org.apache.fineract.organisation.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class UsersApiTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UsersApi usersApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test that assigning user currencies works")
    void testUserCurrenciesAssignment() {
        Long userId = 1L;
        List<String> currencies = Arrays.asList("USD", "EUR");

        AppUser existingUser = new AppUser();
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        HttpServletResponse response = mock(HttpServletResponse.class);

        usersApi.userCurrenciesAssignment(userId, currencies, response);

        verify(appUserRepository, times(1)).findById(userId);
        verify(appUserRepository, times(1)).saveAndFlush(existingUser);

    }

    @Test
    @DisplayName("Test that assigning user PayeePartyIds works")
    void testUserPayeePartyIdsAssignment() {
        Long userId = 1L;
        List<String> payeePartyIds = Arrays.asList("12345", "67890");

        AppUser existingUser = new AppUser();
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        HttpServletResponse response = mock(HttpServletResponse.class);

        usersApi.userPayeePartyIdsAssignment(userId, payeePartyIds, response);

        verify(appUserRepository, times(1)).findById(userId);
        verify(appUserRepository, times(1)).saveAndFlush(existingUser);
    }
}

