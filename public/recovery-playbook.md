# Document Service Recovery Playbook

## 목적

현재 로컬 변경사항을 지운 뒤, 팀원이 푸시한 내용을 `pull` 받고 나서 다시 같은 작업을 재적용할 수 있도록 남기는 복구용 기록입니다.

**중요**:

- 이 문서는 일부만 선택해서 복구하는 문서가 아닙니다.
- 이 문서에 기록된 변경사항은 **전체를 다시 복구해야 하는 대상**입니다.
- 즉, `수정 파일 52개 + 신규 파일 36개 + patch 파일 내용 전체`를 복구 기준으로 봐야 합니다.
- 부분 적용하면 생성/결재/후속 문서/이력/번호 정책이 서로 어긋날 수 있습니다.

이 문서는 단순 집계가 아니라 아래를 기록합니다.

- 어떤 파일이 수정되었는지
- 어떤 파일이 새로 추가되었는지
- 각 변경 묶음이 왜 필요한지
- 다시 작업할 때 어떤 순서로 재적용하면 되는지
- 코드 diff 백업 패치가 어디 있는지

## 현재 작업 트리 기준 파일 집계

- 수정된 파일: 52개
- 신규 파일: 36개

기준: `git status --short`

## 복구 범위 선언

이번 작업은 다음 중 일부만 살리는 방식으로 복구하면 안 됩니다.

- 생성 서비스만 복구
- 결재 서비스만 복구
- 엔티티만 복구
- 테스트만 복구
- 문서만 복구

반드시 아래 전체를 한 묶음으로 복구해야 합니다.

1. 수정 파일 52개
2. 신규 파일 36개
3. patch 파일 전체 diff
4. API/구현/복구 문서

이유는 이번 변경이 단일 파일 수정이 아니라 아래가 서로 연결된 구조 변경이기 때문입니다.

- 생성 흐름
- 등록 요청 흐름
- 결재 메타데이터 동기화
- linked documents
- revision history
- 후속 문서 자동 생성
- 조회 API
- 문서번호 정책
- 테스트 시나리오

## 패치 백업

실제 코드 차이 백업은 아래 파일에 저장했습니다.

- [document-service-recovery-2026-04-01.patch](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/patches/document-service-recovery-2026-04-01.patch)

### 패치 파일의 의미

- 현재 작업 트리의 실제 diff를 텍스트로 저장한 백업입니다.
- 수정 파일 diff와 신규 파일 내용이 같이 들어 있습니다.
- 문서보다 훨씬 세밀한 복구 참고 자료입니다.

### 이 프로젝트에서 패치를 어떻게 봐야 하는가

이번 상황에서는 팀원이 CQRS 패턴 적용 과정에서 폴더 구조를 바꾸고 있을 수 있습니다.
그래서 패치는 아래처럼 이해해야 합니다.

- 구조가 그대로면 일부는 바로 적용 가능
- 구조가 바뀌면 자동 적용보다 참고용 diff로 보는 게 안전

즉, 이 패치는 `그대로 붙이는 용도`보다 `무엇을 구현했는지 정확히 재현하는 근거`에 더 가깝습니다.

### 패치 적용 예시

구조가 거의 안 바뀐 경우:

```bash
git apply public/patches/document-service-recovery-2026-04-01.patch
```

구조가 바뀐 경우:

```bash
git apply --reject --whitespace=fix public/patches/document-service-recovery-2026-04-01.patch
```

이 경우 일부는 `.rej`로 떨어질 수 있고, 그때는 패치 파일과 이 문서를 같이 보면서 수동 반영해야 합니다.

### 지금 상황에서 권장 복구 방식

1. 팀원 변경사항을 먼저 `pull`
2. 새 CQRS 구조를 확인
3. 이 문서에 적힌 **전체 복구 범위**를 기준으로 누락 없이 반영
4. 패치 파일에서 세부 코드 차이를 보면서 수정 파일과 신규 파일을 모두 수동 반영
5. 테스트 실행으로 전체 복구 여부 검증

## 복구 전략

다시 작업할 때는 아래 순서로 적용하면 됩니다.

1. 문서 생성/등록 요청 흐름 정합성 복구
2. DDL 확장 엔티티/서비스 복구
3. 결재 메타데이터 동기화 복구
4. linked documents / revision history 복구
5. 문서번호 정책과 시퀀스 복구
6. 후속 문서 조회 API 복구
7. 통합 테스트/문서화 복구

## 반드시 유지해야 하는 핵심 결정

### 1. 생성과 등록 요청의 역할 분리

- `POST /api/proforma-invoices`
- `POST /api/purchase-orders`

이 두 생성 API는 문서를 `DRAFT`로 저장해야 합니다.

등록 요청 API가 실제 결재 시작점입니다.

- `POST /api/proforma-invoices/request-registration`
- `POST /api/purchase-orders/request-registration`

이 구조를 다시 만들지 않으면 생성과 결재 요청이 중복되어 흐름이 꼬입니다.

### 2. 문서번호 정책

아래 형식으로 유지합니다.

- `PI260001`
- `PO260001`
- `CI260001`
- `PL260001`
- `SO260001`
- `MO260001`

번호 발급은 `document_number_sequences` 기반으로 동시성 안전하게 처리합니다.

### 3. 후속 문서 생성 규칙

PO가 `CONFIRMED` 되면:

- `CI` 자동 생성
- `PL` 자동 생성
- `SO` 자동 생성

`MO`는 별도 선택 생성입니다.

### 4. 스냅샷 원칙

문서는 마스터를 참조만 하지 않고 작성 시점 값을 복사 저장합니다.

- `pi_items`, `po_items`
- `pi_items_snapshot`, `po_items_snapshot`
- 거래처/통화/담당자 스냅샷

### 5. 결재 메타데이터 동기화

`approval_requests`만 바뀌는 게 아니라 PI/PO 문서 본문 메타데이터도 같이 바뀌어야 합니다.

- `approvalStatus`
- `requestStatus`
- `approvalAction`
- `approvalRequestedBy`
- `approvalRequestedAt`
- `approvalReview`

### 6. linked documents / revision history 유지

반드시 유지할 것:

- `linked_documents`는 문서 간 관계를 보관
- `revision_history`는 생성, 등록요청, 승인, 반려, 자동생성, 생산지시서 생성 이벤트를 누적
- 최근 구현에서는 `before -> after` 차이도 기록

## 수정된 파일 목록

아래 파일들은 기존 파일을 수정한 것입니다.

```text
src/main/java/com/team2/documents/client/AuthUserResponse.java
src/main/java/com/team2/documents/command/controller/DocumentCommandController.java
src/main/java/com/team2/documents/command/repository/CommercialInvoiceRepository.java
src/main/java/com/team2/documents/command/repository/PackingListRepository.java
src/main/java/com/team2/documents/command/repository/ProductionOrderRepository.java
src/main/java/com/team2/documents/command/repository/ProformaInvoiceRepository.java
src/main/java/com/team2/documents/command/repository/PurchaseOrderRepository.java
src/main/java/com/team2/documents/command/repository/ShipmentOrderRepository.java
src/main/java/com/team2/documents/command/service/ApprovalRequestCommandService.java
src/main/java/com/team2/documents/command/service/ProformaInvoiceApprovalWorkflowService.java
src/main/java/com/team2/documents/command/service/ProformaInvoiceRejectionWorkflowService.java
src/main/java/com/team2/documents/command/service/ProformaInvoiceService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderApprovalWorkflowService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderCreationService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderDeletionRequestService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderDocumentGenerationService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderModificationRequestService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderProductionOrderGenerationService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderRegistrationService.java
src/main/java/com/team2/documents/command/service/PurchaseOrderRejectionWorkflowService.java
src/main/java/com/team2/documents/entity/Collection.java
src/main/java/com/team2/documents/entity/ProductionOrder.java
src/main/java/com/team2/documents/entity/ProformaInvoice.java
src/main/java/com/team2/documents/entity/Shipment.java
src/main/java/com/team2/documents/query/controller/DocumentQueryController.java
src/main/resources/mapper/CollectionQueryMapper.xml
src/main/resources/mapper/CommercialInvoiceRepository.xml
src/main/resources/mapper/PackingListRepository.xml
src/main/resources/mapper/ProductionOrderQueryMapper.xml
src/main/resources/mapper/ProformaInvoiceQueryMapper.xml
src/main/resources/mapper/ShipmentOrderRepository.xml
src/main/resources/mapper/ShipmentQueryMapper.xml
src/test/java/com/team2/documents/DocumentIntegrationTest.java
src/test/java/com/team2/documents/command/controller/DocumentCommandControllerTest.java
src/test/java/com/team2/documents/command/repository/CollectionRepositoryTest.java
src/test/java/com/team2/documents/command/repository/ProductionOrderRepositoryTest.java
src/test/java/com/team2/documents/command/repository/ShipmentRepositoryTest.java
src/test/java/com/team2/documents/command/service/ApprovalRequestCommandServiceTest.java
src/test/java/com/team2/documents/command/service/CollectionCommandServiceTest.java
src/test/java/com/team2/documents/command/service/ProductionOrderCommandServiceTest.java
src/test/java/com/team2/documents/command/service/ProformaInvoiceApprovalWorkflowServiceTest.java
src/test/java/com/team2/documents/command/service/ProformaInvoiceRejectionWorkflowServiceTest.java
src/test/java/com/team2/documents/command/service/ProformaInvoiceServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderApprovalWorkflowServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderCreationServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderDeletionRequestServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderDocumentGenerationServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderModificationRequestServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderProductionOrderGenerationServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderRegistrationServiceTest.java
src/test/java/com/team2/documents/command/service/PurchaseOrderRejectionWorkflowServiceTest.java
src/test/java/com/team2/documents/command/service/ShipmentCommandServiceTest.java
```

## 신규 파일 목록

아래 파일들은 새로 추가한 파일입니다.

```text
public/api-scenarios.md
public/implementation-progress.md
src/main/java/com/team2/documents/command/repository/CommercialInvoiceJpaRepository.java
src/main/java/com/team2/documents/command/repository/DocumentNumberSequenceRepository.java
src/main/java/com/team2/documents/command/repository/PackingListJpaRepository.java
src/main/java/com/team2/documents/command/repository/ProformaInvoiceItemRepository.java
src/main/java/com/team2/documents/command/repository/ShipmentOrderJpaRepository.java
src/main/java/com/team2/documents/command/service/ApprovalDocumentMetadataService.java
src/main/java/com/team2/documents/command/service/DocumentJsonSupportService.java
src/main/java/com/team2/documents/command/service/DocumentLinkService.java
src/main/java/com/team2/documents/command/service/DocumentNumberGeneratorService.java
src/main/java/com/team2/documents/command/service/DocumentRevisionHistoryService.java
src/main/java/com/team2/documents/command/service/ProformaInvoiceCreationService.java
src/main/java/com/team2/documents/command/service/UserSnapshotService.java
src/main/java/com/team2/documents/dto/ProformaInvoiceCreateRequest.java
src/main/java/com/team2/documents/dto/ProformaInvoiceCreateResponse.java
src/main/java/com/team2/documents/dto/ProformaInvoiceItemCreateRequest.java
src/main/java/com/team2/documents/entity/CommercialInvoice.java
src/main/java/com/team2/documents/entity/DocumentNumberSequence.java
src/main/java/com/team2/documents/entity/PackingList.java
src/main/java/com/team2/documents/entity/ProformaInvoiceItem.java
src/main/java/com/team2/documents/entity/ShipmentOrder.java
src/main/java/com/team2/documents/query/mapper/CommercialInvoiceQueryMapper.java
src/main/java/com/team2/documents/query/mapper/PackingListQueryMapper.java
src/main/java/com/team2/documents/query/mapper/ShipmentOrderQueryMapper.java
src/main/java/com/team2/documents/query/service/CommercialInvoiceQueryService.java
src/main/java/com/team2/documents/query/service/PackingListQueryService.java
src/main/java/com/team2/documents/query/service/ShipmentOrderQueryService.java
src/main/resources/mapper/CommercialInvoiceQueryMapper.xml
src/main/resources/mapper/PackingListQueryMapper.xml
src/main/resources/mapper/ShipmentOrderQueryMapper.xml
src/test/java/com/team2/documents/command/service/ApprovalDocumentMetadataServiceTest.java
src/test/java/com/team2/documents/command/service/DocumentJsonSupportServiceTest.java
src/test/java/com/team2/documents/command/service/DocumentNumberGeneratorServiceTest.java
src/test/java/com/team2/documents/command/service/ProformaInvoiceCreationServiceTest.java
src/test/java/com/team2/documents/command/service/UserSnapshotServiceTest.java
```

## 작업 묶음별 설명

### A. PI / PO 생성 구조

관련 파일:

- `PurchaseOrderCreationService`
- `ProformaInvoiceCreationService`
- 생성 request/response DTO
- 관련 테스트

핵심:

- 생성은 초안
- 품목 저장
- snapshot 저장
- linked documents / revision history 초기화

### B. 결재 동기화

관련 파일:

- `ApprovalRequestCommandService`
- `ApprovalDocumentMetadataService`
- `UserSnapshotService`
- `AuthUserResponse`

핵심:

- approval request 변경 시 PI/PO 메타데이터 같이 갱신
- 요청자명 스냅샷 저장

### C. 후속 문서

관련 파일:

- `CommercialInvoice`, `PackingList`, `ShipmentOrder`, `ProductionOrder`
- 관련 repository / mapper / query service

핵심:

- PO 확정 시 CI/PL/SO 자동 생성
- MO 선택 생성
- 후속 문서 조회 API 제공

### D. 문서 연결과 이력

관련 파일:

- `DocumentJsonSupportService`
- `DocumentLinkService`
- `DocumentRevisionHistoryService`

핵심:

- 문서 간 연결
- 이벤트별 revision 기록
- 변경 전후 diff 기록

### E. 문서번호

관련 파일:

- `DocumentNumberGeneratorService`
- `DocumentNumberSequence`
- `DocumentNumberSequenceRepository`

핵심:

- 공통 번호 정책
- 동시성 안전

## 다시 작업할 때 추천 순서

1. 신규 파일부터 생성
2. 생성 서비스와 결재 서비스 수정
3. 엔티티/매퍼 수정
4. query controller 확장
5. 테스트 복구
6. `./gradlew test` 확인

## 같이 보면 좋은 문서

- [api-scenarios.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/api-scenarios.md)
- [implementation-progress.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/implementation-progress.md)
- [REST_API_명세서.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/ddl%202/REST_API_%EB%AA%85%EC%84%B8%EC%84%9C.md)
- [document-service-recovery-2026-04-01.patch](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/patches/document-service-recovery-2026-04-01.patch)
