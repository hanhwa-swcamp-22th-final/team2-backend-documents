# Document Service Implementation Progress

## 목적

DDL 기준으로 현재까지 반영한 구현 내용을 요약한 문서입니다.
구현 완료 항목, 구조 변경 이유, 현재 상태, 남은 작업을 빠르게 파악하기 위한 용도입니다.

## 전체 요약

현재 문서 서비스는 DDL 기준 핵심 도메인 흐름을 대부분 반영한 상태입니다.
중심 축은 다음과 같습니다.

- `PI / PO` 본문 필드 확장
- `pi_items / po_items` 저장
- 스냅샷 JSON 저장
- 결재 요청과 문서 메타데이터 동기화
- 문서 간 연결과 수정 이력 누적
- 후속 문서 `CI / PL / SO / MO` 생성과 조회
- `collections / shipments` DDL 필드 반영
- 문서번호 정책 정리와 동시성 안전한 발급

## 왜 이런 방향으로 구현했는가

핵심 원칙은 두 가지였습니다.

1. 문서는 마스터 데이터를 참조만 하지 않고 작성 시점 값을 스냅샷으로 보존해야 합니다.
2. 결재와 후속 문서 생성은 문서 상태 전이 중심으로 일관되게 연결되어야 합니다.

그래서 정규화 테이블과 JSON 스냅샷을 함께 유지하는 구조를 택했습니다.

## 반영한 주요 변경

### 1. 빌드/버전 정합성 수정

- Spring Boot와 Spring Cloud 버전 충돌을 정리했습니다.
- `./gradlew test`가 정상 동작하는 조합으로 맞췄습니다.

### 2. PI / PO DDL 확장

- `proforma_invoices`, `purchase_orders`의 주요 DDL 컬럼을 엔티티에 반영했습니다.
- 거래처, 통화, 담당자, 결재 메타데이터, 스냅샷, 이력 필드를 포함합니다.

### 3. 품목 테이블 반영

- `pi_items`, `po_items`를 실제 엔티티와 저장 로직으로 연결했습니다.
- 품목은 문서 작성 시점 값 기준으로 저장됩니다.

### 4. 스냅샷 JSON 저장

- `pi_items_snapshot`, `po_items_snapshot`
- `linked_documents`
- `revision_history`

생성 시점에 자동 저장되도록 구현했습니다.

### 5. 결재 메타데이터 동기화

- `approval_requests`와 PI/PO 문서 본문 메타데이터가 같이 움직이도록 맞췄습니다.
- 등록 요청, 승인, 반려 시 문서 쪽 `approvalStatus`, `requestStatus`, `approvalAction`, `approvalReview`가 동기화됩니다.

### 6. 사용자명 스냅샷

- `approval_requested_by`는 더 이상 단순 ID 문자열이 아니라 auth 조회 기반 사용자명 우선으로 저장합니다.
- 외부 조회 실패 시 ID 문자열로 폴백합니다.

### 7. 후속 문서 DDL 반영

- `commercial_invoices`
- `packing_lists`
- `shipment_orders`
- `production_orders`

PO 확정 시 자동 생성 또는 선택 생성되며, PO 스냅샷을 복사 저장합니다.

### 8. collections / shipments 반영

- `collections`, `shipments`도 DDL 기준 컬럼을 코드에 반영했습니다.
- 일부 화면 편의 필드는 `Transient`로 유지했습니다.

### 9. linked_documents / revision_history 고도화

- 문서 간 연결 관계를 양방향으로 저장합니다.
- 생성, 등록 요청, 승인, 반려, 자동 문서 생성, 생산지시서 생성 이벤트를 이력에 남깁니다.
- 최근에는 `before -> after` 변경 요약도 함께 남기도록 확장했습니다.

### 10. 문서번호 정책 정리

- `PI260001`
- `PO260001`
- `CI260001`
- `PL260001`
- `SO260001`
- `MO260001`

형식으로 통일했습니다.

### 11. 동시성 안전한 번호 발급

- 기존 `max + 1` 방식 대신 `document_number_sequences` 기반 발급으로 바꿨습니다.
- 동시 요청 시 중복 번호가 나지 않도록 락 기반으로 처리합니다.

### 12. 조회 API 확장

아래 문서군의 목록/상세 조회를 열었습니다.

- PI
- PO
- CI
- PL
- SO
- MO
- shipments
- collections
- approval requests

## 생성과 등록 요청을 분리한 이유

중간에 중요한 수정이 있었습니다.

기존에는 staff가 문서를 생성하면 바로 `APPROVAL_PENDING`으로 들어가고 결재 요청도 같이 생성됐습니다.
그런데 이 구조는 `등록 요청 API`와 역할이 겹칩니다.

그래서 현재는 아래처럼 정리했습니다.

- 생성: `DRAFT`
- 등록 요청: `APPROVAL_PENDING` 또는 팀장 즉시 `CONFIRMED`

이 구조가 실제 사용자 흐름과 API 역할 분리에 더 맞습니다.

## 현재 기준 상태

### 작업 트리 기준 파일 집계

- 수정된 파일: 52개
- 신규 파일: 36개

위 숫자는 현재 `git status --short` 기준입니다.

### 구현 완료에 가까운 영역

- PI/PO 생성
- PI/PO 등록 요청
- PI/PO 승인/반려
- approval request 생성/조회/처리
- CI/PL/SO 자동 생성
- MO 선택 생성
- shipments / collections 조회 및 상태 변경
- 문서번호 정책
- 문서 링크
- 수정 이력

### 테스트로 검증한 시나리오

- 단위 테스트 전반
- H2 기반 통합 테스트
- `PI 초안 생성 -> 등록 요청 -> 승인 -> PO 초안 생성 -> 등록 요청 -> 승인 -> CI/PL/SO 조회`

## 현재 남은 작업

아래는 “DDL 미반영”보다는 “실제 연결/제품화” 쪽 작업입니다.

- 프론트 화면별 API 연결 문서 고도화
- 실제 DB 환경에서의 검증
- 외부 서비스와의 실연동 검증
- 필요 시 예외 응답 포맷 정리
- 필요 시 조회 DTO 분리

## 관련 문서

- [api-scenarios.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/api-scenarios.md)
- [document_service.md](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/public/ddl%202/document_service.md)
