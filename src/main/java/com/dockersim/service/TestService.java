package com.dockersim.service;

import com.dockersim.model.TestEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    public void save(TestEntity userInfo) {
    }

    public List<TestEntity> findAll() {
        return List.of();
    }
}
