package com.team2.documents;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.team2.documents.repository.ShipmentRepository;
import com.team2.documents.repository.UserPositionRepository;

@SpringBootTest
@ActiveProfiles("test")
class DocumentsApplicationTests {

    @MockBean
    private ShipmentRepository shipmentRepository;

    @MockBean
    private UserPositionRepository userPositionRepository;

    @Test
    void contextLoads() {
    }
}
