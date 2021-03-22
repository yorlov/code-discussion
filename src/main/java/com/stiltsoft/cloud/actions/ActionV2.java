package com.stiltsoft.cloud.actions;

import com.stiltsoft.cloud.PermissionService;
import com.stiltsoft.cloud.RequireAccessToPage;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static com.stiltsoft.cloud.AC.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ActionV2 extends Action<RequireAccessToPage> {

    private PermissionService permissionService;

    @Inject
    public ActionV2(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CompletionStage<Result> call(Request request) {
        return getAcHost(request).flatMap(acHost ->
                getAccount(request).flatMap(account ->
                        getPage(request).map(page ->
                                permissionService.canViewPage(acHost, account, page))))
                .map(permission -> permission.thenComposeAsync(canView -> canView ? delegate.call(request) : completedFuture(forbidden())))
                .orElse(completedFuture(unauthorized()));
    }
}