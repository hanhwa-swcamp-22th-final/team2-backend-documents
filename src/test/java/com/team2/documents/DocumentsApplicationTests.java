package com.team2.documents;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private ApprovalRequestRepository approvalRequestRepository;

    @MockBean
    private CollectionRepository collectionRepository;

    @MockBean
    private CommercialInvoiceRepository commercialInvoiceRepository;

    @MockBean
    private PackingListRepository packingListRepository;

    @MockBean
    private ProductionOrderRepository productionOrderRepository;

    @MockBean
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private ShipmentRepository shipmentRepository;

    @MockBean
    private ShipmentOrderRepository shipmentOrderRepository;

    @MockBean
    private UserPositionRepository userPositionRepository;

    @Test
    void contextLoads() {
    }
}
