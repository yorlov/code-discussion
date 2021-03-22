package com.stiltsoft.cloud;

import java.util.concurrent.CompletionStage;

public interface PermissionService {

    CompletionStage<Boolean> canViewPage(ACHost acHost, Account account, Page page);

}
