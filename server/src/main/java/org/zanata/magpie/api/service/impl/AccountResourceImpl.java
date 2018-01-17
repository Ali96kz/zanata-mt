/*
 * Copyright 2017, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.magpie.api.service.impl;

import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.zanata.magpie.annotation.CheckRole;
import org.zanata.magpie.api.dto.APIResponse;
import org.zanata.magpie.api.dto.AccountDto;
import org.zanata.magpie.api.dto.CredentialDto;
import org.zanata.magpie.security.UnsetInitialPassword;
import org.zanata.magpie.service.AccountService;

import com.webcohesion.enunciate.metadata.rs.RequestHeader;
import com.webcohesion.enunciate.metadata.rs.RequestHeaders;
import com.webcohesion.enunciate.metadata.rs.ResourceLabel;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Path("/account")
@RequestHeaders({
        @RequestHeader(name = "X-Auth-User",
                description = "The authentication user."),
        @RequestHeader(name = "X-Auth-Token",
                description = "The authentication token.") })
@ResourceLabel("Account")
@Produces(MediaType.APPLICATION_JSON)
@CheckRole("admin")
public class AccountResourceImpl {
    private AccountService accountService;
    private Validator validator;
    private Event<UnsetInitialPassword> unsetInitialPasswordEvent;

    @Context
    UriInfo uriInfo;

    @Inject
    public AccountResourceImpl(AccountService accountService,
            Validator validator, Event<UnsetInitialPassword> unsetInitialPasswordEvent) {
        this.accountService = accountService;
        this.validator = validator;
        this.unsetInitialPasswordEvent = unsetInitialPasswordEvent;
    }

    @SuppressWarnings("unused")
    public AccountResourceImpl() {
    }

    @GET
    public List<AccountDto>
            getAllAccounts(@QueryParam("enabledOnly") boolean enabledOnly) {
        return accountService.getAllAccounts(!enabledOnly);
    }

    @POST
    public Response registerNewAccount(AccountDto accountDto) {
        Set<ConstraintViolation<AccountDto>> violations =
                validator.validate(accountDto);

        if (!violations.isEmpty()) {
            String message =
                    violations.stream().map(
                            v -> String.format("%s %s", v.getPropertyPath(), v.getMessage()))
                            .reduce("error: ", (a, b) -> a + b + "; ");
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new APIResponse(Response.Status.BAD_REQUEST, message))
                    .build();
        }
        // TODO only support one credential for now
        CredentialDto credentialDto = accountDto.getCredentials().iterator().next();
        AccountDto dto = accountService.registerNewAccount(accountDto,
                credentialDto.getUsername(), credentialDto.getSecret());

        // as soon as we have a user, we should remove initial password
        unsetInitialPasswordEvent.fire(new UnsetInitialPassword());
        return Response.created(uriInfo.getRequestUriBuilder().path("id")
                .path(dto.getId().toString()).build()).build();
    }
}
