package com.yrgo.services.calls;

import com.yrgo.domain.Action;
import com.yrgo.domain.Call;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import com.yrgo.services.diary.DiaryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class CallHandlingServiceImpl implements CallHandlingService{

    private CustomerManagementService customerManagementService;

    private DiaryManagementService diaryManagementService;

    public CallHandlingServiceImpl(CustomerManagementService cms, DiaryManagementService dms) {
        this.customerManagementService = cms;
        this.diaryManagementService = dms;
    }
    @Override
    public void recordCall(String customerId, Call newCall, Collection<Action> actions) throws CustomerNotFoundException {
        customerManagementService.recordCall(customerId, newCall);

        for (Action action : actions) {
            diaryManagementService.recordAction(action);
        }
    }
}
