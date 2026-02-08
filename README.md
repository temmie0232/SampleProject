# SampleProject: 住所変更申請ワークフロー（学習用）

新人向けに、**React + Spring Boot + MySQL** で「現場っぽい業務アプリ」の型を学ぶためのお手本プロジェクトです。

- Frontend: React + TypeScript (Vite)
- Backend: Spring Boot (Java 17) + Spring Web + Spring Data JPA + Validation
- DB: MySQL
- Infra: Docker / Docker Compose
- Docs: OpenAPI (Swagger)

## 題材

公共/金融系でよくある「申請ワークフロー」を題材にしています。

- 一般ユーザー: 住所変更申請を作成・提出
- 管理者: 提出済み申請を承認/却下
- 監査ログ: 誰がいつ何を操作したかを記録

## 画面一覧

1. `/login` ログイン
2. `/applications` 申請一覧（検索・ページング・ソート）
3. `/applications/new` 申請作成
4. `/applications/:id` 申請詳細（状態遷移・履歴）
5. `/audit-logs` 監査ログ一覧（管理者のみ）

## API一覧（MVP）

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `GET /api/v1/applications`
- `POST /api/v1/applications`
- `GET /api/v1/applications/{id}`
- `PUT /api/v1/applications/{id}`
- `POST /api/v1/applications/{id}/submit`
- `POST /api/v1/applications/{id}/approve`
- `POST /api/v1/applications/{id}/reject`
- `GET /api/v1/applications/{id}/history`
- `GET /api/v1/audit-logs`

Swagger UI:

- `http://localhost:8080/swagger-ui/index.html`

## ER（テキスト）

- `users`
- `applications`
- `workflow_transition_rules`
- `application_status_history`
- `audit_logs`

状態遷移は `workflow_transition_rules` に登録し、`applications.status` と `application_status_history` を同時更新します。

## 状態遷移

- `DRAFT -> SUBMITTED` (USER)
- `REJECTED -> SUBMITTED` (USER)
- `SUBMITTED -> APPROVED` (ADMIN)
- `SUBMITTED -> REJECTED` (ADMIN)

## ディレクトリ構成

```text
.
├── frontend/
├── backend/
├── docker-compose.yml
├── Makefile
└── README.md
```

## 起動方法（Docker Compose）

```bash
make up
```

起動確認:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

停止:

```bash
make down
```

ログ確認:

```bash
make logs
```

## ローカル実行（任意）

### Backend

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./backend/gradlew -p backend --project-cache-dir ./.gradle-project-cache bootRun
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## テスト

```bash
make backend-test
```

## 初期ユーザー

- 管理者: `admin01 / admin123`
- 一般: `user01 / user123`

## 学習ポイント（どこを読むか）

1. 共通レスポンス/例外処理
- `backend/src/main/java/com/example/workflow/common/ApiResponse.java`
- `backend/src/main/java/com/example/workflow/common/GlobalExceptionHandler.java`

2. 認証・認可（JWT + RBAC）
- `backend/src/main/java/com/example/workflow/config/SecurityConfig.java`
- `backend/src/main/java/com/example/workflow/auth/JwtAuthenticationFilter.java`

3. 業務ロジック（申請と状態遷移）
- `backend/src/main/java/com/example/workflow/application/ApplicationService.java`
- `backend/src/main/java/com/example/workflow/workflow/WorkflowService.java`

4. フロントのAPI分離と画面実装
- `frontend/src/api/`
- `frontend/src/pages/ApplicationListPage.tsx`
- `frontend/src/pages/ApplicationDetailPage.tsx`

## 環境変数

Backend:

- `SPRING_PROFILES_ACTIVE` (`dev`/`prod`)
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `APP_JWT_SECRET`, `APP_JWT_EXPIRATION_SECONDS`
- `APP_CORS_ALLOWED_ORIGINS`

Frontend:

- `VITE_API_BASE_URL`

## Git運用（学習推奨）

- `main` へ直接pushしない
- 機能ごとに `feat/*` ブランチを切る
- 日本語コミットで「何を」「なぜ」を短く残す

例:

- `feat: 認証基盤とJWTフィルタを追加`
- `feat: 住所変更申請のCRUD APIを追加`
- `feat: 申請の承認・却下フローと履歴記録を追加`
- `feat: フロントに申請一覧・詳細・作成画面を追加`
