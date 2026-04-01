# Document API Scenarios

## 목적

프론트와 API를 실제로 연결할 때 필요한 호출 순서와 상태 변화를 정리한 문서입니다.
기준 시나리오는 `PI 생성 -> PI 등록 요청 -> PI 승인 -> PO 생성 -> PO 등록 요청 -> PO 승인 -> 후속 문서 조회`입니다.

## 공통 규칙

- 생성 API는 문서를 바로 결재 상태로 보내지 않고 `DRAFT`로 저장합니다.
- 등록 요청 API가 실제 결재 흐름의 시작점입니다.
- 일반 직원은 등록 요청 시 `approval_requests`가 생성됩니다.
- 팀장은 등록 요청 시 결재를 거치지 않고 즉시 `CONFIRMED` 처리됩니다.
- PO 승인 완료 시 `CI`, `PL`, `SO`가 자동 생성됩니다.
- 생산지시서 `MO`는 별도 선택 생성입니다.

## 1. PI 초안 생성

### Request

`POST /api/proforma-invoices`

```json
{
  "piId": "PI260001",
  "issueDate": "2026-04-01",
  "clientId": 10,
  "currencyId": 1,
  "managerId": 2,
  "deliveryDate": "2026-04-20",
  "incotermsCode": "FOB",
  "namedPlace": "Busan",
  "totalAmount": null,
  "clientName": "ABC Trading",
  "clientAddress": "Seoul",
  "country": "KR",
  "currencyCode": "USD",
  "managerName": "Kim",
  "userId": 2,
  "items": [
    {
      "itemId": 100,
      "itemName": "Bolt",
      "quantity": 5,
      "unit": "EA",
      "unitPrice": 10.00,
      "amount": null,
      "remark": "urgent"
    }
  ]
}
```

### Response

```json
{
  "message": "PI 생성 요청이 처리되었습니다.",
  "piId": "PI260001"
}
```

### 확인 포인트

- `GET /api/proforma-invoices/PI260001`
- 기대 상태: `status = DRAFT`

## 2. PI 등록 요청

### Request

`POST /api/proforma-invoices/request-registration`

```json
{
  "piId": "PI260001",
  "userId": 2
}
```

### 기대 결과

- PI 상태가 `APPROVAL_PENDING`으로 변경됩니다.
- `approval_requests`에 결재 요청이 1건 생성됩니다.
- 문서 메타데이터에 결재 요청 정보가 반영됩니다.

### 조회 API

- `GET /api/proforma-invoices/PI260001`
- `GET /api/approval-requests/document/PI/PI260001/status/PENDING`

## 3. PI 승인

### Request

먼저 `approvalRequestId`를 조회합니다.

`GET /api/approval-requests/document/PI/PI260001/status/PENDING`

그 다음 승인합니다.

`PUT /api/approval-requests/{approvalRequestId}`

```json
{
  "status": "APPROVED",
  "comment": "PI 승인"
}
```

### 기대 결과

- 결재 요청 상태: `APPROVED`
- PI 상태: `CONFIRMED`
- PI 결재 메타데이터: `approvalStatus = 승인`

### 조회 API

- `GET /api/proforma-invoices/PI260001`

## 4. PO 초안 생성

### Request

`POST /api/purchase-orders`

```json
{
  "poId": "PO260001",
  "piId": "PI260001",
  "issueDate": "2026-04-02",
  "clientId": 10,
  "currencyId": 1,
  "managerId": 2,
  "deliveryDate": "2026-04-25",
  "incotermsCode": "FOB",
  "namedPlace": "Busan",
  "sourceDeliveryDate": "2026-04-20",
  "deliveryDateOverride": false,
  "totalAmount": null,
  "clientName": "ABC Trading",
  "clientAddress": "Seoul",
  "country": "KR",
  "currencyCode": "USD",
  "managerName": "Kim",
  "userId": 2,
  "items": [
    {
      "itemId": 100,
      "itemName": "Bolt",
      "quantity": 5,
      "unit": "EA",
      "unitPrice": 10.00,
      "amount": null,
      "remark": "urgent"
    }
  ]
}
```

### Response

```json
{
  "message": "PO 생성 요청이 처리되었습니다.",
  "poId": "PO260001"
}
```

### 확인 포인트

- `GET /api/purchase-orders/PO260001`
- 기대 상태: `status = DRAFT`

## 5. PO 등록 요청

### Request

`POST /api/purchase-orders/request-registration`

```json
{
  "poId": "PO260001",
  "userId": 2
}
```

### 기대 결과

- PO 상태가 `APPROVAL_PENDING`으로 변경됩니다.
- `approval_requests`에 PO 결재 요청이 생성됩니다.

### 조회 API

- `GET /api/purchase-orders/PO260001`
- `GET /api/approval-requests/document/PO/PO260001/status/PENDING`

## 6. PO 승인

### Request

먼저 `approvalRequestId`를 조회합니다.

`GET /api/approval-requests/document/PO/PO260001/status/PENDING`

그 다음 승인합니다.

`PUT /api/approval-requests/{approvalRequestId}`

```json
{
  "status": "APPROVED",
  "comment": "PO 승인"
}
```

### 기대 결과

- 결재 요청 상태: `APPROVED`
- PO 상태: `CONFIRMED`
- PO 승인 시 `CI`, `PL`, `SO`가 자동 생성됩니다.

### 조회 API

- `GET /api/purchase-orders/PO260001`
- `GET /api/commercial-invoices`
- `GET /api/packing-lists`
- `GET /api/shipment-orders`

## 7. 자동 생성 문서 조회

### CI 목록

`GET /api/commercial-invoices`

### PL 목록

`GET /api/packing-lists`

### SO 목록

`GET /api/shipment-orders`

### 기대 포인트

- 각 문서가 `poId = PO260001`을 참조합니다.
- `linked_documents`에 상위 문서 연결 정보가 들어갑니다.

## 8. 생산지시서 선택 생성

### Request

`POST /api/purchase-orders/PO260001/generate-production-order`

### 기대 결과

- `MO` 문서번호로 생산지시서가 1건 생성됩니다.

### 조회 API

- `GET /api/production-orders`
- `GET /api/production-orders/{productionOrderId}`

## 프론트 화면 기준 최소 호출 순서

### PI 화면

1. `POST /api/proforma-invoices`
2. `GET /api/proforma-invoices/{piId}`
3. `POST /api/proforma-invoices/request-registration`
4. `GET /api/approval-requests/document/PI/{piId}/status/PENDING`

### 결재 화면

1. `GET /api/approval-requests`
2. `GET /api/approval-requests/{approvalRequestId}`
3. `PUT /api/approval-requests/{approvalRequestId}`

### PO 화면

1. `POST /api/purchase-orders`
2. `GET /api/purchase-orders`
3. `GET /api/purchase-orders/{poId}`
4. `POST /api/purchase-orders/request-registration`
5. `GET /api/approval-requests/document/PO/{poId}/status/PENDING`

### 후속 문서 화면

1. `GET /api/commercial-invoices`
2. `GET /api/packing-lists`
3. `GET /api/shipment-orders`
4. `POST /api/purchase-orders/{poId}/generate-production-order`
5. `GET /api/production-orders`

## 비고

- 문서번호 정책은 `PI260001`, `PO260001`, `CI260001`, `PL260001`, `SO260001`, `MO260001` 형식입니다.
- 품목 `amount`가 비어 있으면 서버에서 `quantity * unitPrice`로 계산합니다.
- `revision_history`와 `linked_documents`는 서버에서 자동 관리합니다.
