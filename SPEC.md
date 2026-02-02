# SimpleLibrary（図書貸出・蔵書管理）仕様書

## 0. 目的
「身近で理解しやすい題材（図書の検索・貸出・返却）」を使って、Spring Boot の基本機能を **中規模** で一通り学べるプロジェクト仕様を定義する。

- フロントエンド：React（SPA）
- バックエンド：Spring Boot（REST API）
- DB：MySQL

学習で触れる主要テーマ：
- CRUD（マスタ/在庫/貸出）
- RESTful API（DTO、ページング、検索、状態遷移）
- Spring Security（ユーザー登録/ログイン、JWT、ロール）
- Validation + 例外レスポンス統一
- トランザクション/排他（同じ本を同時に貸出できない）
- ファイルアップロード（本の表紙画像）
- Flyway（スキーマ管理）
- OpenAPI（Swagger UI）/ Actuator（運用の入口）

---

## 1. アプリ概要
### 1.1 アプリ名
SimpleLibrary（図書貸出・蔵書管理）

### 1.2 できること（最小ストーリー）
1. ユーザー登録 → ログイン
2. 図書検索（タイトル/著者/カテゴリ）
3. 図書詳細で在庫（貸出可の冊数）を確認
4. 本（冊）を借りる → 返す（履歴が残る）
5. 管理者が蔵書（本の情報・冊数）やカテゴリ/著者を管理

### 1.3 想定ユーザー
- `MEMBER`：一般ユーザー（検索/貸出/返却/自分の履歴）
- `ADMIN`：管理者（蔵書/マスタ管理、全貸出の参照）

### 1.4 対象外（非ゴール）
- 予約（順番待ち）機能
- 罰金/決済
- 外部図書 API 連携（ISBN 検索）※発展課題

---

## 2. 技術スタック（提案）
### 2.1 バックエンド
- Java 17
- Spring Boot 3.x
- Spring Web（REST）
- Spring Data JPA（Hibernate）
- MySQL 8.x
- Flyway（DB マイグレーション）
- Spring Security + JWT（認証/認可）
- Validation（jakarta.validation）
- springdoc-openapi（Swagger UI）
- Actuator
- テスト：JUnit 5 / Spring Boot Test / Testcontainers（推奨）

### 2.2 フロントエンド
- React 18+
- TypeScript
- Vite
- React Router
- TanStack Query
- Axios
- React Hook Form + Zod
- UI：MUI または Tailwind CSS（どちらか選択）

### 2.3 開発環境
- Docker Compose（MySQL）
- `.env`（フロント）、`application-*.yml`（バック）

---

## 3. 認証・認可
### 3.1 認証方式
- メール + パスワードでログイン（JWT）
- `Authorization: Bearer <access_token>`
- Refresh Token は任意（まずは Access Token のみでOK）

### 3.2 ロール
- `ADMIN`：マスタ/蔵書管理、全貸出参照
- `MEMBER`：図書検索、貸出/返却、自己履歴参照

### 3.3 認可ルール（抜粋）
- `ADMIN` のみ：本/著者/カテゴリ/冊（Copy）の作成・更新・削除
- `MEMBER`：貸出/返却は「自分の貸出」のみ操作可能
- `ADMIN`：全ユーザーの貸出一覧を閲覧可能

---

## 4. 機能要件
### 4.1 認証/ユーザー
- サインアップ（メール重複不可）
- ログイン
- 自分のプロフィール参照/更新（表示名）
- パスワード変更（旧パスワード必須）

### 4.2 カテゴリ（Category）マスタ
- 一覧/作成/更新/削除（`ADMIN`）
- 例：小説、ビジネス、技術、漫画

### 4.3 著者（Author）マスタ
- 一覧/作成/更新/削除（`ADMIN`）

### 4.4 図書（Book）マスタ
図書は「作品（タイトル情報）」として扱い、在庫は別テーブル（Copy）で管理する。

- 一覧（検索/ページング）
- 作成/更新/削除（`ADMIN`）
- 表紙画像アップロード（任意）

検索条件：
- キーワード：タイトル/概要
- 著者
- カテゴリ

### 4.5 冊（BookCopy）在庫管理
- 作品に対して「冊（Copy）」を追加できる（`ADMIN`）
- 冊の状態：`AVAILABLE` / `LOANED` / `LOST` / `RETIRED`
- `LOANED` の冊は削除不可（整合性のため）

### 4.6 貸出（Loan）
#### 4.6.1 ステータス
- `ACTIVE`：貸出中
- `RETURNED`：返却済

#### 4.6.2 ルール
- 1冊は同時に1人にしか貸し出せない（排他制御）
- 返却期限（dueDate）を設定（例：14日後）
- （任意）延長（renew）：`ACTIVE` のみ、1回まで等の制限を入れても良い

#### 4.6.3 機能
- 貸出（borrow）：`AVAILABLE` の Copy を `LOANED` にして Loan 作成
- 返却（return）：Loan を `RETURNED` にして Copy を `AVAILABLE` に戻す
- 自分の貸出一覧（`ACTIVE`）
- 自分の貸出履歴（`RETURNED` 含む）
- （ADMIN）全貸出一覧（検索/ページング）

---

## 5. 画面仕様（React SPA）
### 5.1 画面一覧
1. ログイン
2. サインアップ
3. 図書検索（一覧/フィルタ）
4. 図書詳細（在庫、表紙）
5. 自分の貸出（貸出中）
6. 自分の履歴（返却済み含む）
7. 管理（ADMIN）：カテゴリ管理
8. 管理（ADMIN）：著者管理
9. 管理（ADMIN）：図書管理（本情報・冊数・表紙アップロード）
10. 管理（ADMIN）：貸出一覧（全体）
11. プロフィール設定

### 5.2 画面要件（抜粋）
#### 図書検索
- キーワード/カテゴリ/著者で絞り込み
- ページング
- 一覧に「貸出可能冊数（availableCopies）」を表示

#### 図書詳細
- 作品情報（タイトル、概要、著者、カテゴリ）
- 在庫状況（available / total）
- 借りるボタン（貸出可能が 1冊以上のとき）

#### 管理：図書管理
- 図書の作成/更新
- 冊（Copy）追加（例：+1冊）
- 冊の状態変更（紛失/廃棄）
- 表紙画像アップロード

---

## 6. REST API 仕様（概要）
### 6.1 共通
- Base URL：`/api/v1`
- Content-Type：`application/json`
- 認証：`Authorization: Bearer <access_token>`

### 6.2 ページング（共通）
```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 0,
  "totalPages": 0
}
```

### 6.3 エラーレスポンス（統一）
```json
{
  "timestamp": "2026-02-02T12:34:56Z",
  "path": "/api/v1/books",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      { "field": "title", "reason": "must not be blank" }
    ]
  }
}
```

主な `error.code`：
- `UNAUTHORIZED`（401）
- `FORBIDDEN`（403）
- `NOT_FOUND`（404）
- `VALIDATION_ERROR`（400）
- `CONFLICT`（409：排他/重複）
- `INTERNAL_ERROR`（500）

### 6.4 エンドポイント一覧（代表）
#### Auth
- `POST /auth/signup`
- `POST /auth/login`

#### Users
- `GET /users/me`
- `PATCH /users/me`
- `POST /users/me/password`

#### Masters（ADMIN）
- `GET /categories`
- `POST /categories`
- `PATCH /categories/{categoryId}`
- `DELETE /categories/{categoryId}`
- `GET /authors`
- `POST /authors`
- `PATCH /authors/{authorId}`
- `DELETE /authors/{authorId}`

#### Books
- `GET /books`（検索/ページング：全員）
- `POST /books`（ADMIN）
- `GET /books/{bookId}`（全員）
- `PATCH /books/{bookId}`（ADMIN）
- `DELETE /books/{bookId}`（ADMIN）
- `POST /books/{bookId}/cover`（ADMIN：multipart）
- `GET /books/{bookId}/cover`（全員）

#### Copies（ADMIN）
- `GET /books/{bookId}/copies`
- `POST /books/{bookId}/copies`（冊追加）
- `PATCH /copies/{copyId}`（状態更新）

#### Loans
- `POST /copies/{copyId}/borrow`（MEMBER）
- `POST /loans/{loanId}/return`（MEMBER：自分の貸出のみ）
- `POST /loans/{loanId}/renew`（任意）
- `GET /loans/me`（MEMBER：自分の貸出）
- `GET /loans/me/history`（MEMBER）
- `GET /admin/loans`（ADMIN：全体検索）

---

## 7. API 詳細（主要）
### 7.1 `POST /auth/signup`
```json
{
  "email": "user@example.com",
  "password": "Passw0rd!",
  "displayName": "Alice"
}
```
制約：
- email：必須、メール形式
- password：必須、最小 8（ルールは任意で強化）
- displayName：必須、1..50

### 7.2 `GET /books`
Query（例）：
- `q`：キーワード（タイトル/概要）
- `authorId`
- `categoryId`
- `page/size/sort`

### 7.3 `POST /copies/{copyId}/borrow`
#### 成功（200）
```json
{
  "loanId": "lon_...",
  "copyId": "cpy_...",
  "status": "ACTIVE",
  "dueDate": "2026-02-16"
}
```
#### 競合（409）
- すでに `LOANED` の copy を借りようとした場合
- error.code 例：`CONFLICT`（details に理由）

### 7.4 `POST /loans/{loanId}/return`
#### 成功（200）
- `RETURNED` になった loan を返す

---

## 8. データモデル（MySQL）
### 8.1 共通方針
- 文字コード：`utf8mb4`
- エンジン：`InnoDB`
- 監査カラム：`created_at` / `updated_at` / `created_by` / `updated_by`
- 楽観ロック：`version`（BIGINT）
- ID：`VARCHAR(26)`（ULID 等）推奨（または `BIGINT`）

### 8.2 テーブル（要点）
#### users
- `id` PK
- `email` UNIQUE NOT NULL
- `password_hash` NOT NULL
- `display_name` NOT NULL
- `role` NOT NULL（ADMIN/MEMBER）
- `created_at` / `updated_at` / `version`

#### categories
- `id` PK
- `name` UNIQUE NOT NULL

#### authors
- `id` PK
- `name` NOT NULL

#### books
- `id` PK
- `title` NOT NULL
- `description` NULL
- `cover_path` NULL
- `created_at` / `updated_at` / `version`

#### book_authors（多対多）
- `book_id` FK NOT NULL
- `author_id` FK NOT NULL
- PRIMARY KEY(`book_id`,`author_id`)

#### book_categories（多対多）
- `book_id` FK NOT NULL
- `category_id` FK NOT NULL
- PRIMARY KEY(`book_id`,`category_id`)

#### book_copies
- `id` PK
- `book_id` FK NOT NULL
- `status` NOT NULL（AVAILABLE/LOANED/LOST/RETIRED）
- `created_at` / `updated_at` / `version`
- INDEX(`book_id`,`status`)

#### loans
- `id` PK
- `copy_id` FK NOT NULL
- `borrower_user_id` FK NOT NULL
- `status` NOT NULL（ACTIVE/RETURNED）
- `borrowed_at` NOT NULL
- `due_date` NOT NULL
- `returned_at` NULL
- `renew_count` NOT NULL DEFAULT 0（任意）
- INDEX(`borrower_user_id`,`status`)
- INDEX(`copy_id`,`status`)

#### audit_logs（任意）
- `id` PK
- `actor_user_id` NULL
- `type` NOT NULL
- `target_type` NOT NULL
- `target_id` NULL
- `payload` JSON NULL
- `created_at` NOT NULL

---

## 9. バックエンド設計（Spring Boot）
### 9.1 レイヤ構成（推奨）
- Controller（HTTP/DTO）
- Service（ユースケース、トランザクション境界）
- Repository（DB）
- Domain（Entity/Enum）
- Infrastructure（JWT、ファイル保存）

### 9.2 重要実装ポイント（学習要素）
- DTO と Entity の分離（Controller で Entity を直接返さない）
- `@Valid` + `@ControllerAdvice` による例外統一
- 貸出の排他制御
  - `book_copies` を `PESSIMISTIC_WRITE` でロックして status を確認→更新
  - 競合時は 409 を返す
- N+1 回避（一覧で著者/カテゴリを出す場合は fetch 戦略を意識）
- Flyway によるスキーマ管理
- OpenAPI（Swagger UI）で API を確認

### 9.3 設定/環境変数（例）
- `SPRING_PROFILES_ACTIVE=local`
- DB：`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- JWT：`JWT_SECRET`, `JWT_EXPIRES_SECONDS`
- Upload：`UPLOAD_DIR`, `MAX_UPLOAD_SIZE_MB`

---

## 10. フロントエンド設計（React）
### 10.1 ディレクトリ構成（例）
- `src/api`：API クライアント、型
- `src/routes`：ページ
- `src/components`：共通 UI
- `src/features`：`books/` `loans/` `admin/`
- `src/auth`：認証状態、token 保持

### 10.2 API 呼び出し
- Axios interceptor で Authorization 付与
- 401 でログアウト + `/login` へ遷移
- TanStack Query で一覧/詳細をキャッシュ

### 10.3 フォーム
- React Hook Form + Zod
- サーバ Validation エラー（field エラー）をフォームにマッピング

---

## 11. 非機能要件
- セキュリティ：bcrypt、JWT 秘密鍵は環境変数
- パフォーマンス：検索 API はページング必須
- ログ：リクエストID（Correlation ID）付与（任意）

---

## 12. 受け入れ基準（サンプル）
1. サインアップ → ログイン → 本を検索 → 借りる → 返すが一連で動く
2. 借りられている冊（Copy）は他ユーザーが借りようとすると 409 になる
3. `ADMIN` 以外はマスタ/在庫管理 API が 403 になる
4. Validation エラーが統一形式で返る
5. Swagger UI で主要 API を実行できる

---

## 13. 実装マイルストーン（推奨）
1. Docker Compose（MySQL） + Spring Boot 起動 + Flyway
2. Auth（signup/login） + Security（JWT）
3. マスタ CRUD（カテゴリ/著者/本） + OpenAPI
4. 在庫（Copy）管理 + 貸出（排他）/返却
5. React UI（ログイン→検索→貸出→返却）
6. （任意）表紙アップロード、監査ログ
