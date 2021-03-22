package com.stiltsoft.cloud.actions;

import com.stiltsoft.cloud.*;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static com.stiltsoft.cloud.AC.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

public class ActionV5 extends Action<RequireAccessToPage> {

    private PermissionService permissionService;

    @Inject
    public ActionV5(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CompletionStage<Result> call(Request request) {
        ACHost acHost = getAcHost(request).orElse(null);
        Account account = getAccount(request).orElse(null);
        Page page = getPage(request).orElse(null);

        if (anyNull(acHost, account, page)) {
            return completedFuture(unauthorized());
        }

        return permissionService.canViewPage(acHost, account, page)
                .thenComposeAsync(canView -> canView ? delegate.call(request) : completedFuture(forbidden()));
    }
}