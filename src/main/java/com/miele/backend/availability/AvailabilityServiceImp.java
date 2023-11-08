package com.miele.backend.availability;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class AvailabilityServiceImp implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    @Override
    public Availability save(Availability availability) {
            return availabilityRepository.save(availability);
    }
}
