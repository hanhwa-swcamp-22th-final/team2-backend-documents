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

import com.team2.documents.query.mapper.ApprovalRequestQueryMapper;
import com.team2.documents.query.mapper.CollectionQueryMapper;
import com.team2.documents.query.mapper.ProductionOrderQueryMapper;
import com.team2.documents.query.mapper.ProformaInvoiceQueryMapper;
import com.team2.documents.query.mapper.PurchaseOrderQueryMapper;
import com.team2.documents.query.mapper.ShipmentQueryMapper;
import com.team2.documents.command.repository.ApprovalRequestRepository;
import com.team2.documents.command.repository.CollectionRepository;
import com.team2.documents.command.repository.CommercialInvoiceRepository;
import com.team2.documents.command.repository.PackingListRepository;
import com.team2.documents.command.repository.ProductionOrderRepository;
import com.team2.documents.command.repository.ProformaInvoiceRepository;
import com.team2.documents.command.repository.PurchaseOrderRepository;
import com.team2.documents.command.repository.ShipmentRepository;
import com.team2.documents.command.repository.ShipmentOrderRepository;
import com.team2.documents.command.repository.UserPositionRepository;

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

    @MockitoBean
    private ApprovalRequestQueryMapper approvalRequestQueryMapper;

    @MockitoBean
    private CollectionQueryMapper collectionQueryMapper;

    @MockitoBean
    private ProductionOrderQueryMapper productionOrderQueryMapper;

    @MockitoBean
    private ProformaInvoiceQueryMapper proformaInvoiceQueryMapper;

    @MockitoBean
    private PurchaseOrderQueryMapper purchaseOrderQueryMapper;

    @MockitoBean
    private ShipmentQueryMapper shipmentQueryMapper;

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
