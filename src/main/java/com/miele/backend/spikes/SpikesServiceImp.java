package com.miele.backend.spikes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class SpikesServiceImp implements SpikesService {

    private final SpikesRepository spikesService;

    @Override
    public Spikes save(Spikes spikes) {
        return spikesService.save(spikes);
    }
}
