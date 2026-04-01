# Document Service Recovery Playbook

## 목적

이 문서는 `team2-backend-documents`를 CQRS 구조로 재정리한 뒤,
기존 DDL 기반 구현을 다시 이식한 현재 상태를 기록하는 복구 문서입니다.

용도는 두 가지입니다.

- 팀원 변경사항을 `pull` 받은 뒤 현재 복구 범위를 다시 재적용할 때 기준점으로 사용
- 지금 시점에서 무엇이 이미 복구되었고 무엇이 아직 문서화/백업만 남았는지 구분

## 현재 기준점

- 기준 날짜: `2026-04-01`
- 기준 브랜치 상태: 로컬 작업 트리
- 검증 상태: `./gradlew test` 통과

## 중요 선언

- 이 문서는 부분 적용용 문서가 아닙니다.
- 여기에 적힌 복구 범위는 서로 연결된 한 묶음입니다.
- 생성 흐름만 따로, 결재만 따로, 후속 문서만 따로 적용하면 상태 전이와 메타데이터가 어긋날 수 있습니다.

즉, 다시 복구할 때는 아래 항목을 한 세트로 봐야 합니다.

1. 생성과 등록 요청 분리
2. 문서번호 정책
3. approval 메타데이터 동기화
4. linked documents / revision history
5. CI / PL / SO / MO 생성 및 조회
6. 관련 테스트

## 현재 작업 트리 기준 집계

기준 명령: `git status --short`

- 수정 파일: `31`
- 신규 파일: `17`

이 숫자는 예전 문서에 적었던 `52 / 36`이 아니라,
**현재 CQRS 구조로 다시 이식한 시점의 실제 집계**입니다.

## 현재 복구 상태

### 복구 완료 항목

- `PI / PO` 생성 API가 다시 `DRAFT` 저장 기준으로 동작함
- 등록 요청 API가 실제 결재 시작점으로 다시 분리됨
- `document_number_sequences` 기반 문서번호 발급 복구
- `PI / PO` DDL 확장 필드 저장 구조 유지
- `approval_requests`와 PI/PO 본문 메타데이터 동기화 복구
- `approval_requested_by` 사용자명 스냅샷 복구
- `linked_documents` 누적 로직 복구
- `revision_history` 누적 로직 복구
- `PO 승인 -> CI / PL / SO 자동 생성` 복구
- `PO -> MO 선택 생성` 복구
- `CI / PL / SO` 조회 API 복구
- H2 기준 통합 테스트와 단위 테스트 통과 확인

### 아직 미정리 항목

아래는 코드 미구현이라기보다, 복구 기록 관점에서 아직 남은 항목입니다.

- 현재 CQRS 기준 diff로 `patch` 파일을 다시 생성하지는 않음
- [implementation-progress.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/implementation-progress.md)는 개요 문서이고, 파일 인벤토리 기준 문서는 이 문서가 최신본임
- 프론트 API 연결은 아직 진행하지 않음
- 실제 운영 DB 환경 검증은 아직 하지 않음

즉, **핵심 코드 복구는 완료**, 남은 것은 주로 기록 보강과 실제 연결 작업입니다.

## 반드시 유지해야 하는 핵심 결정

### 1. 생성과 등록 요청은 분리

다음 생성 API는 문서를 바로 결재 상태로 보내면 안 됩니다.

- `POST /api/proforma-invoices`
- `POST /api/purchase-orders`

생성 결과는 `DRAFT`여야 합니다.

실제 결재 시작점은 다음 등록 요청 API입니다.

- `POST /api/proforma-invoices/request-registration`
- `POST /api/purchase-orders/request-registration`

### 2. 문서번호 정책 유지

문서번호는 아래 형식을 유지합니다.

- `PI260001`
- `PO260001`
- `CI260001`
- `PL260001`
- `SO260001`
- `MO260001`

발급 방식은 `document_number_sequences` 기반입니다.

### 3. approval 메타데이터는 문서 본문과 동기화

`approval_requests`만 저장하는 구조가 아닙니다.
PI / PO 문서 본문에도 아래 값이 같이 반영되어야 합니다.

- `approvalStatus`
- `requestStatus`
- `approvalAction`
- `approvalRequestedBy`
- `approvalRequestedAt`
- `approvalReview`

### 4. linked documents / revision history 유지

- `linked_documents`는 문서 간 관계를 보관
- `revision_history`는 생성, 등록요청, 승인, 반려, 자동생성, 생산지시서 생성 이벤트를 누적
- 현재 구현은 `before -> after` 차이 요약까지 포함

### 5. 후속 문서 규칙 유지

PO가 `CONFIRMED` 되면:

- `CI` 자동 생성
- `PL` 자동 생성
- `SO` 자동 생성

`MO`는 별도 선택 생성입니다.

## 현재 수정 파일 목록

아래 파일은 현재 작업 트리에서 `수정(M)` 상태인 파일입니다.

```text
src/main/java/com/team2/documents/command/application/service/ApprovalRequestCommandService.java
src/main/java/com/team2/documents/command/application/service/ProformaInvoiceApprovalWorkflowService.java
src/main/java/com/team2/documents/command/application/service/ProformaInvoiceRejectionWorkflowService.java
src/main/java/com/team2/documents/command/application/service/ProformaInvoiceService.java
src/main/java/com/team2/documents/command/application/service/PurchaseOrderApprovalWorkflowService.java
src/main/java/com/team2/documents/command/application/service/PurchaseOrderDocumentGenerationService.java
src/main/java/com/team2/documents/command/application/service/PurchaseOrderProductionOrderGenerationService.java
src/main/java/com/team2/documents/command/application/service/PurchaseOrderRegistrationService.java
src/main/java/com/team2/documents/command/application/service/PurchaseOrderRejectionWorkflowService.java
src/main/java/com/team2/documents/command/domain/entity/CommercialInvoice.java
src/main/java/com/team2/documents/command/domain/entity/PackingList.java
src/main/java/com/team2/documents/command/domain/entity/ShipmentOrder.java
src/main/java/com/team2/documents/command/domain/repository/CommercialInvoiceRepository.java
src/main/java/com/team2/documents/command/domain/repository/PackingListRepository.java
src/main/java/com/team2/documents/command/domain/repository/ShipmentOrderRepository.java
src/main/java/com/team2/documents/command/infrastructure/client/AuthUserResponse.java
src/main/java/com/team2/documents/query/controller/DocumentQueryController.java
src/main/resources/mapper/CommercialInvoiceRepository.xml
src/main/resources/mapper/PackingListRepository.xml
src/main/resources/mapper/ShipmentOrderRepository.xml
src/test/java/com/team2/documents/DocumentIntegrationTest.java
src/test/java/com/team2/documents/command/application/controller/DocumentCommandControllerTest.java
src/test/java/com/team2/documents/command/application/service/ApprovalRequestCommandServiceTest.java
src/test/java/com/team2/documents/command/application/service/ProformaInvoiceApprovalWorkflowServiceTest.java
src/test/java/com/team2/documents/command/application/service/ProformaInvoiceRejectionWorkflowServiceTest.java
src/test/java/com/team2/documents/command/application/service/ProformaInvoiceServiceTest.java
src/test/java/com/team2/documents/command/application/service/PurchaseOrderApprovalWorkflowServiceTest.java
src/test/java/com/team2/documents/command/application/service/PurchaseOrderDocumentGenerationServiceTest.java
src/test/java/com/team2/documents/command/application/service/PurchaseOrderProductionOrderGenerationServiceTest.java
src/test/java/com/team2/documents/command/application/service/PurchaseOrderRegistrationServiceTest.java
src/test/java/com/team2/documents/command/application/service/PurchaseOrderRejectionWorkflowServiceTest.java
```

## 현재 신규 파일 목록

아래 파일은 현재 작업 트리에서 `신규(??)` 상태인 파일입니다.

```text
src/main/java/com/team2/documents/command/application/service/ApprovalDocumentMetadataService.java
src/main/java/com/team2/documents/command/application/service/DocumentJsonSupportService.java
src/main/java/com/team2/documents/command/application/service/DocumentLinkService.java
src/main/java/com/team2/documents/command/application/service/DocumentRevisionHistoryService.java
src/main/java/com/team2/documents/command/application/service/UserSnapshotService.java
src/main/java/com/team2/documents/command/domain/repository/CommercialInvoiceJpaRepository.java
src/main/java/com/team2/documents/command/domain/repository/PackingListJpaRepository.java
src/main/java/com/team2/documents/command/domain/repository/ShipmentOrderJpaRepository.java
src/main/java/com/team2/documents/query/mapper/CommercialInvoiceQueryMapper.java
src/main/java/com/team2/documents/query/mapper/PackingListQueryMapper.java
src/main/java/com/team2/documents/query/mapper/ShipmentOrderQueryMapper.java
src/main/java/com/team2/documents/query/service/CommercialInvoiceQueryService.java
src/main/java/com/team2/documents/query/service/PackingListQueryService.java
src/main/java/com/team2/documents/query/service/ShipmentOrderQueryService.java
src/main/resources/mapper/CommercialInvoiceQueryMapper.xml
src/main/resources/mapper/PackingListQueryMapper.xml
src/main/resources/mapper/ShipmentOrderQueryMapper.xml
```

## 다시 복구할 때의 권장 순서

1. 팀원 변경사항을 먼저 `pull`
2. 현재 CQRS 패키지 구조를 다시 확인
3. 이 문서의 수정 파일 31개와 신규 파일 17개를 기준으로 재적용
4. 생성/등록 요청 분리부터 확인
5. approval metadata, revision, link 로직 복구
6. CI / PL / SO / MO 생성/조회 복구
7. `./gradlew test`로 검증

## 패치 파일에 대한 현재 판단

기존 백업 패치는 아래에 있습니다.

- [document-service-recovery-2026-04-01.patch](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/patches/document-service-recovery-2026-04-01.patch)

다만 이 패치는 **이전 구조 기준 diff 성격이 강합니다**.
현재처럼 CQRS로 재배치된 상태에서는 자동 적용보다 참고용 diff로 보는 편이 안전합니다.

즉, 지금 시점의 가장 신뢰할 수 있는 문서는:

1. 이 recovery 문서
2. [implementation-progress.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/implementation-progress.md)
3. [api-scenarios.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/api-scenarios.md)
4. 현재 로컬 코드

입니다.

## 관련 문서

- [api-scenarios.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/api-scenarios.md)
- [implementation-progress.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/implementation-progress.md)
- [REST_API_명세서.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/ddl%202/REST_API_%EB%AA%85%EC%84%B8%EC%84%9C.md)
