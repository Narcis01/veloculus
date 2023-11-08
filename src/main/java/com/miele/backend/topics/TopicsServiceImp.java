package com.miele.backend.topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class TopicsServiceImp implements TopicsService{

    private final TopicsRepository topicsService;

    @Override
    public Topics save(Topics topics) {
        return topicsService.save(topics);
    }
}
