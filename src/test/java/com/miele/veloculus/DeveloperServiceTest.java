package com.miele.veloculus;


import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperRepository;
import com.miele.backend.developer.DeveloperServiceImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeveloperServiceTest {
    @Mock
    DeveloperRepository developerRepository;

    @InjectMocks
    DeveloperServiceImp developerServiceImp;

    @Test
    public void SaveTest() {
        Developer developer = Developer.builder().name("Narcis").build();
        Mockito.when(developerRepository.save(developer)).thenReturn(developer);
        Developer result = developerServiceImp.save(developer);

        Assert.assertEquals(developer, result);
    }

    @Test
    public void GetByNameTest() {
        Developer developer = Developer.builder().name("Narcis").build();
        Mockito.when(developerRepository.findByName(developer.getName())).thenReturn(developer);

        Developer result = developerServiceImp.getByName(developer.getName());
        Assert.assertEquals(developer, result);


    }

}
