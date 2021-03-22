package com.stiltsoft.cloud.actions;

import com.stiltsoft.cloud.ACHost;
import com.stiltsoft.cloud.Account;
import com.stiltsoft.cloud.Page;
import com.stiltsoft.cloud.PermissionService;
import com.stiltsoft.cloud.RequireAccessToPage;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.stiltsoft.cloud.AC.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ActionV1 extends Action<RequireAccessToPage> {

    private PermissionService permissionService;

    @Inject
    public ActionV1(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CompletionStage<Result> call(Request request) {
        Optional<ACHost> acHostOpt = getAcHost(request);
        Optional<Account> accountOpt = getAccount(request);
        Optional<Page> pageOpt = getPage(request);

        if (acHostOpt.isPresent() && accountOpt.isPresent() && pageOpt.isPresent()) {
            return permissionService.canViewPage(acHostOpt.get(), accountOpt.get(), pageOpt.get()).thenComposeAsync(canView -> {
                if (canView) {
                    return delegate.call(request);
                } else {
                    return completedFuture(forbidden());
                }
            });
        } else {
            return completedFuture(unauthorized());
        }
    }
}