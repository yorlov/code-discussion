package com.stiltsoft.cloud.actions;

import com.stiltsoft.cloud.ACHost;
import com.stiltsoft.cloud.Account;
import com.stiltsoft.cloud.Page;
import com.stiltsoft.cloud.PermissionService;
import com.stiltsoft.cloud.RequireAccessToPage;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.stiltsoft.cloud.AC.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ActionV4 extends Action<RequireAccessToPage> {

    private PermissionService permissionService;

    @Inject
    public ActionV4(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CompletionStage<Result> call(Request request) {
        return hasPermission(request, permissionService::canViewPage);
    }

    private CompletionStage<Result> hasPermission(Request request, Function3<ACHost, Account, Page, CompletionStage<Boolean>> canViewPage) {
        return permissionRequest(request)
                .map(canViewPage.tupled())
                .map(permission -> permission.thenComposeAsync(canView -> canView ? delegate.call(request) : completedFuture(forbidden())))
                .orElse(completedFuture(unauthorized()));
    }

    private Optional<Tuple3<ACHost, Account, Page>> permissionRequest(Request request) {
        return getAcHost(request).flatMap(acHost ->
                getAccount(request).flatMap(accountId ->
                        getPage(request).map(pageId ->
                                Tuple.of(acHost, accountId, pageId))
                ));
    }
}