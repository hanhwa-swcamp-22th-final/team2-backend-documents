package com.team2.documents;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.CollectionRepository;
import com.team2.documents.repository.CommercialInvoiceRepository;
import com.team2.documents.repository.PackingListRepository;
import com.team2.documents.repository.ProductionOrderRepository;
import com.team2.documents.repository.ProformaInvoiceRepository;
import com.team2.documents.repository.PurchaseOrderRepository;
import com.team2.documents.repository.ShipmentRepository;
import com.team2.documents.repository.ShipmentOrderRepository;
import com.team2.documents.repository.UserPositionRepository;

@SpringBootTest
@ActiveProfiles("test")
class DocumentsApplicationTests {

    @MockitoBean
    private ApprovalRequestRepository approvalRequestRepository;

    @MockitoBean
    private CollectionRepository collectionRepository;

    @MockitoBean
    private CommercialInvoiceRepository commercialInvoiceRepository;

    @MockitoBean
    private PackingListRepository packingListRepository;

    @MockitoBean
    private ProductionOrderRepository productionOrderRepository;

    @MockitoBean
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @MockitoBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockitoBean
    private ShipmentRepository shipmentRepository;

    @MockitoBean
    private ShipmentOrderRepository shipmentOrderRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void mainRunsSpringApplication() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            springApplication.when(() -> SpringApplication.run(DocumentsApplication.class, new String[]{}))
                    .thenReturn(context);

            DocumentsApplication.main(new String[]{});

            springApplication.verify(() -> SpringApplication.run(DocumentsApplication.class, new String[]{}));
        }
    }
}
