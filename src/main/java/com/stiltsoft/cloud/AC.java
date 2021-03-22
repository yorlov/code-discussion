package com.stiltsoft.cloud;

import play.libs.typedmap.TypedKey;
import play.mvc.Http.Request;

import java.util.Optional;

public class AC {

    private static TypedKey<ACHost> AC_HOST = TypedKey.create("ac_host");
    private static TypedKey<Account> AC_ACCOUNT = TypedKey.create("ac_account");
    private static TypedKey<Page> AC_PAGE = TypedKey.create("ac_page");

    public static Optional<ACHost> getAcHost(Request request) {
        return attr(request, AC_HOST);
    }

    public static Optional<Account> getAccount(Request request) {
        return attr(request, AC_ACCOUNT);
    }

    public static Optional<Page> getPage(Request request) {
        return attr(request, AC_PAGE);
    }

    private static <T> Optional<T> attr(Request request, TypedKey<T> key) {
        return request.attrs().getOptional(key);
    }
}