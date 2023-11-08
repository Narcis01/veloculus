package com.miele.backend.daysOff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class DaysOffServiceImp implements DaysOffService{

    private final DaysOffRepository daysOffService;

    @Override
    public DaysOff save(DaysOff daysOff) {
         return daysOffService.save(daysOff);
    }
}
