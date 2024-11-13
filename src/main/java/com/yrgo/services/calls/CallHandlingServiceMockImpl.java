package com.yrgo.services.calls;

import com.yrgo.domain.Action;
import com.yrgo.domain.Call;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import com.yrgo.services.diary.DiaryManagementService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

public class CallHandlingServiceMockImpl implements CallHandlingService{
    private final CustomerManagementService customerManagementService;
    private final DiaryManagementService diaryManagementService;

    // Constructor injection
    public CallHandlingServiceMockImpl(CustomerManagementService customerManagementService,
                                       DiaryManagementService diaryManagementService) {
        this.customerManagementService = customerManagementService;
        this.diaryManagementService = diaryManagementService;
    }
    @Override
    public void recordCall(String customerId, Call newCall, Collection<Action> actions) throws CustomerNotFoundException {
        customerManagementService.recordCall(customerId, newCall);

        for (Action action : actions) {
            diaryManagementService.recordAction(action);
        }
    }
}
