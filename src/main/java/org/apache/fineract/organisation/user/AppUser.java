/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.organisation.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.fineract.organisation.office.Office;
import org.apache.fineract.organisation.parent.AbstractPersistableCustom;
import org.apache.fineract.organisation.permission.Permission;
import org.apache.fineract.organisation.role.Role;
import org.apache.fineract.organisation.staff.Staff;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "m_appuser")
public class AppUser extends AbstractPersistableCustom<Long> implements UserDetails {

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nonexpired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "nonlocked", nullable = false)
    private boolean accountNonLocked;

    @Column(name = "nonexpired_credentials", nullable = false)
    private boolean credentialsNonExpired;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "firsttime_login_remaining", nullable = false)
    private boolean firstTimeLoginRemaining;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "m_appuser_role", joinColumns = @JoinColumn(name = "appuser_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @Column(name = "last_time_password_updated")
    @Temporal(TemporalType.DATE)
    private Date lastTimePasswordUpdated;

    @Column(name = "password_never_expires", nullable = false)
    private boolean passwordNeverExpires;

    /**
     * Comma-separated string of payee party IDs / DUKAS assigned with the user.
     * Return empty array to show that user is allowed to view all
     */
    @Column(name = "payee_party_ids")
    private String payeePartyIds;
    /**
     * Comma-separated string of currencies assigned to the user.
     * Return empty array to show that user is not allowed to view any
     */
    @Column(name = "currencies")
    private String currencies;

    /**
     * Comma-separated string of payee party ID types / SOURCE / MNOs assigned to the user.
     * Return empty array to show that user is not allowed to view any
     */
    @Column(name = "payee_party_id_types")
    private String payeePartyIdTypes;

    public List<String> getPayeePartyIdsList() {
        if (payeePartyIds != null && !payeePartyIds.isEmpty()) {
            return Arrays.stream(payeePartyIds.split(","))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public void setPayeePartyIdsList(List<String> newPayeePartyIds) {
        if (newPayeePartyIds != null && !newPayeePartyIds.isEmpty()) {
            payeePartyIds = String.join(",", newPayeePartyIds);
        } else {
            payeePartyIds = null;
        }
    }

    public List<String> getCurrenciesList() {
        if (currencies != null && !currencies.isEmpty()) {
            return Arrays.stream(currencies.split(","))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public void setCurrenciesList(List<String> currenciesList) {
        if (currenciesList != null && !currenciesList.isEmpty()) {
            currencies = String.join(",", currenciesList);
        } else {
            currencies = null;
        }
    }

    public List<String> getPayeePartyIdTypesList() {
        if (payeePartyIdTypes != null && !payeePartyIdTypes.isEmpty()) {
            if (payeePartyIdTypes.equalsIgnoreCase("*"))
                return new ArrayList<>();
            return Arrays.stream(payeePartyIdTypes.split(","))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void setPayeePartyIdTypesList(List<String> newPayeePartyIdTypes) {
        if (payeePartyIdTypes != null && !payeePartyIdTypes.isEmpty()) {
            payeePartyIdTypes = String.join(",", newPayeePartyIdTypes);
        } else {
            payeePartyIdTypes = null;
        }
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        List<Permission> finalPermissions = new ArrayList<>();
        for (final Role role : this.getRoles()) {
            if (!role.getDisabled()) {
                final Collection<Permission> permissions = role.getPermissions();
                for (final Permission permission : permissions) {
                    if (!finalPermissions.contains(permission)) {
                        finalPermissions.add(permission);
                    }
                }
            }
        }

        return finalPermissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFirstTimeLoginRemaining() {
        return firstTimeLoginRemaining;
    }

    public void setFirstTimeLoginRemaining(boolean firstTimeLoginRemaining) {
        this.firstTimeLoginRemaining = firstTimeLoginRemaining;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @JsonIgnore
    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    @JsonIgnore
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @JsonIgnore
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Date getLastTimePasswordUpdated() {
        return lastTimePasswordUpdated;
    }

    public void setLastTimePasswordUpdated(Date lastTimePasswordUpdated) {
        this.lastTimePasswordUpdated = lastTimePasswordUpdated;
    }

    public boolean isPasswordNeverExpires() {
        return passwordNeverExpires;
    }

    public void setPasswordNeverExpires(boolean passwordNeverExpires) {
        this.passwordNeverExpires = passwordNeverExpires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(username, appUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}