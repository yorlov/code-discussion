package com.stiltsoft.cloud.actions;

import com.stiltsoft.cloud.ACHost;
import com.stiltsoft.cloud.Account;
import com.stiltsoft.cloud.Page;
import com.stiltsoft.cloud.PermissionService;
import com.stiltsoft.cloud.RequireAccessToPage;
import io.vavr.Function3;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.stiltsoft.cloud.AC.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ActionV3 extends Action<RequireAccessToPage> {

    private PermissionService permissionService;

    @Inject
    public ActionV3(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CompletionStage<Result> call(Request request) {
        Function3<ACHost, Account, Page, CompletionStage<Boolean>> canViewPage = permissionService::canViewPage;

        return Optional.of(canViewPage.curried())
                .flatMap(acHostFn -> getAcHost(request).map(acHostFn))
                .flatMap(accountFn -> getAccount(request).map(accountFn))
                .flatMap(pageFn -> getPage(request).map(pageFn))
                .map(permission -> permission.thenComposeAsync(canView -> canView ? delegate.call(request) : completedFuture(forbidden())))
                .orElse(completedFuture(unauthorized()));
    }
}