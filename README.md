# SalesBoost Documents Service

> PO（Purchase Order）を基準に貿易書類を生成・追跡し、書類間リンクとスナップショット履歴を一元管理する海外B2B営業管理システムの文書サービスです。

---

## 概要

このリポジトリは SalesBoost の文書ドメインを担当するバックエンドサービスです。  
PI / PO の作成と承認フローを起点に、CI / PL / Shipment Order / Production Order の生成、出荷・入金状況の管理、文書スナップショット履歴の保存を扱います。

主な目的は以下のとおりです。

- 同一情報の再入力を減らし、文書作成業務を自動化する
- PO を中心に下位文書を連携し、文書間のトレーサビリティを確保する
- `docs_revision` ベースのスナップショット履歴により、生成時点・変更時点の文書状態を保存する
- Command / Query を分離し、状態変更と照会の責務を明確にする

---

## 主要機能

### 1. 文書作成・登録
- PI（Proforma Invoice）下書き作成
- PO（Purchase Order）下書き作成
- PI / PO 登録依頼
- 文書番号自動採番
  - 例: `PO260001`, `PI260001`, `CI260001`, `PL260001`, `SO260001`, `MO260001`

### 2. 承認ワークフロー
- 承認依頼作成
- 承認 / 差戻し処理
- 文書本文への承認メタデータ同期
- 承認履歴・変更履歴の記録

### 3. 下位文書自動生成
- PO 確定時に以下の文書を自動生成
  - Commercial Invoice
  - Packing List
  - Shipment Order
- 必要に応じて Production Order を生成

### 4. 書類リンク・履歴管理
- PI ↔ PO 間リンク
- PO ↔ CI / PL / SO / MO 間リンク
- `docs_revision` テーブルに文書スナップショットとリビジョン履歴を保存

### 5. 業務進捗管理
- 出荷状態更新
- 入金 / 未収管理
- 承認依頼一覧・文書一覧・詳細照会

---

## 技術スタック

### Backend
- Java 21
- Spring Boot 3.4
- Spring Web
- Spring Validation
- Spring Data JPA
- MyBatis
- Spring Security
- Spring Boot Mail
- Spring Boot Actuator
- Spring Cloud OpenFeign
- Lombok

### Data
- MariaDB
- H2

### Build / Test
- Gradle
- JUnit 5
- Spring Boot Test
- MyBatis Spring Boot Starter Test
- Spring Security Test
- JaCoCo

---

## アーキテクチャ

このサービスは Command / Query 分離を意識した構成で実装されています。

```text
src/main/java/com/team2/documents
├── command
│   ├── application
│   │   ├── controller
│   │   ├── dto
│   │   └── service
│   ├── domain
│   │   ├── entity
│   │   └── repository
│   └── infrastructure
│       ├── client
│       └── repository
├── query
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   └── service
└── common
    └── error
```

### Command
- 文書作成
- 承認依頼
- 承認 / 差戻し
- 下位文書生成
- 出荷 / 入金状態変更

### Query
- 文書一覧 / 詳細照会
- 承認依頼照会
- 出荷 / 入金状況照会
- `docs_revision` 履歴照会

---

## ドメインモデル

主なエンティティは以下のとおりです。

- `PurchaseOrder`
- `ProformaInvoice`
- `CommercialInvoice`
- `PackingList`
- `ShipmentOrder`
- `ProductionOrder`
- `Shipment`
- `Collection`
- `ApprovalRequest`
- `DocsRevision`

設計上の特徴:

- 文書内部 PK と外部文書コードを分離
- 文書間参照はコード / ID ベースで管理
- 文書スナップショットと変更履歴は `docs_revision` に集約

---

## スナップショット / 履歴戦略

このサービスでは文書履歴の権威ある保存先として `docs_revision` を使用します。

- 文書生成時: `SNAPSHOT`
- 文書状態変更時: `REVISION`
- 承認 / 差戻し時: 履歴イベントを追加保存

これにより、文書本体テーブルに過度な履歴カラムを持たせず、生成時点・変更時点の文脈を JSON で保存します。

---

## 外部連携

- Auth Service
  - OpenFeign によるユーザー情報・役職情報参照
- Mail
  - SMTP 設定ベースのメール送信

---

## テスト構成

テストは以下の層に分けて構成しています。

- Unit Test
  - Service
  - Query Service
- Slice Test
  - Command Controller
  - Query Controller
- Repository Test
  - JPA Repository
  - Query Mapper
- Integration Test
  - Workflow Integration
  - Shipment / Collection / ProductionOrder / ApprovalRequest Integration

代表的な検証内容:

- PO 作成 → 登録依頼 → 確定 → CI / PL / SO 生成
- `docs_revision` スナップショット保存
- MyBatis Query Mapper の実 DB(H2) ベース検証
- コントローラの Command / Query 分離テスト

---

## 実行設定

基本設定は [application.yml](/Users/gangseonghun/be22-final-team2-project/team2-backend-documents/src/main/resources/application.yml) にあります。

- アプリケーション名: `team2-backend-documents`
- 基本ポート: `8084`
- 基本プロファイル: `dev`
- DB ドライバ: `org.mariadb.jdbc.Driver`

ローカル開発時は以下の環境変数を使用します。

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `AUTH_SERVICE_URL`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

---

## 現在の実装ポイント

- PO / PI 下書き生成と登録依頼フロー
- 承認依頼・承認 / 差戻しワークフロー
- PO 確定時の CI / PL / SO 自動生成
- Production Order 生成
- 出荷 / 入金状態管理
- `docs_revision` ベースのスナップショット履歴管理
- Query モデル分離による CQRS ライクな構成

---

## 補足

この README は、現在の `team2-backend-documents` リポジトリ実装基準で更新されています。  
インフラ全体構成やフロントエンド技術スタックは、このリポジトリ単体ではなくシステム全体リポジトリ基準で別途管理することを推奨します。
