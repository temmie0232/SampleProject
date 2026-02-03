# SimpleLibrary

図書貸出・蔵書管理の学習用サンプル（React + Spring Boot + MySQL）

## 1. 起動手順（ローカル）
### 1) DB 起動
```bash
docker compose up -d
```

### 2) バックエンド起動
```bash
cd backend
mvn spring-boot:run
```

### 3) フロントエンド起動
```bash
cd frontend
npm install
npm run dev
```

`frontend/.env.example` を `.env` にコピーすると API の接続先を変更できます。

## 2. 環境変数（バックエンド）
| 変数 | 役割 | 既定値 |
| --- | --- | --- |
| `DB_HOST` | MySQL ホスト | `localhost` |
| `DB_PORT` | MySQL ポート | `3306` |
| `DB_NAME` | DB 名 | `simplelibrary` |
| `DB_USER` | DB ユーザー | `root` |
| `DB_PASSWORD` | DB パスワード | `password` |
| `JWT_SECRET` | JWT 署名鍵 | `dev-secret-change-me` |
| `JWT_EXPIRES_SECONDS` | JWT 期限（秒） | `900` |
| `UPLOAD_DIR` | 表紙保存ディレクトリ | `./uploads` |
| `MAX_UPLOAD_SIZE_MB` | アップロード上限 | `10` |
| `BOOTSTRAP_ENABLED` | 初期管理者作成 | `true` |
| `BOOTSTRAP_ADMIN_EMAIL` | 管理者メール | `admin@example.com` |
| `BOOTSTRAP_ADMIN_PASSWORD` | 管理者パスワード | `Admin123!` |
| `BOOTSTRAP_ADMIN_DISPLAY_NAME` | 管理者名 | `Admin` |
| `CORS_ALLOWED_ORIGINS` | CORS 許可 | `http://localhost:5173` |

## 3. API ドキュメント
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## 4. テスト
バックエンドのテストは Testcontainers（Docker）を使用します。

```bash
cd backend
mvn test
```

## 5. 管理者ログイン
初回起動時、管理者が自動生成されます（環境変数で変更可能）。

```
email: admin@example.com
password: Admin123!
```
