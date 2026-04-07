package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ShipmentOrderQueryMapper;
import com.team2.documents.query.model.ShipmentOrderView;

@ExtendWith(MockitoExtension.class)
class ShipmentOrderQueryServiceTest {

    @Mock
    private ShipmentOrderQueryMapper shipmentOrderQueryMapper;

    @InjectMocks
    private ShipmentOrderQueryService shipmentOrderQueryService;

    @Test
    @DisplayName("SO ID로 조회 시 해당 SO를 반환한다")
    void findById_whenShipmentOrderExists_thenReturnsShipmentOrder() {
        ShipmentOrderView so = new ShipmentOrderView();
        so.setShipmentOrderId("SO260001");
        so.setStatus("READY");
        when(shipmentOrderQueryMapper.findById("SO260001")).thenReturn(so);

        ShipmentOrderView result = shipmentOrderQueryService.findById("SO260001");

        assertEquals("SO260001", result.getShipmentOrderId());
    }

    @Test
    @DisplayName("존재하지 않는 SO ID로 조회 시 예외를 던진다")
    void findById_whenShipmentOrderNotExists_thenThrowsException() {
        when(shipmentOrderQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentOrderQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 SO 목록을 조회한다")
    void findAll_whenShipmentOrdersExist_thenReturnsAll() {
        ShipmentOrderView so = new ShipmentOrderView();
        so.setShipmentOrderId("SO260001");
        so.setStatus("READY");
        when(shipmentOrderQueryMapper.findAll()).thenReturn(List.of(so));

        List<ShipmentOrderView> result = shipmentOrderQueryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
