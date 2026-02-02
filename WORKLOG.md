# Work Log

## 2026-02-02
- Initialized git repository (`main`), added remote origin.
- Replaced project specification with **SimpleLibrary** (図書貸出・蔵書管理) and removed reservation feature.
- Planning: Start backend/ frontend implementation and infrastructure setup.
- Added backend scaffolding (Spring Boot, Maven, application config, Flyway).
- Implemented backend core:
  - JWT認証（signup/login）、セキュリティ設定、CORS、例外統一レスポンス
  - ユーザー（me取得/更新/パスワード変更）
  - カテゴリ/著者マスタCRUD（ADMINのみ作成/更新/削除）
  - 図書CRUD（著者・カテゴリ紐付け、表紙アップロード）
  - 冊（Copy）追加・状態更新
  - 貸出/返却、在庫排他（PESSIMISTIC_WRITE）
  - ファイル保存（表紙）と Request-ID フィルタ
  - 初期管理者自動作成（環境変数）
- Added frontend scaffolding (Vite + React + TS):
  - 認証（ログイン/サインアップ）
  - 図書検索/詳細/貸出
  - 自分の貸出/履歴
  - 管理者向け簡易画面（カテゴリ/著者/図書 + コピー追加）
- Added infrastructure/docs:
  - Docker Compose（MySQL + Adminer）
  - README、.env.example、.gitignore
- Validation:
  - `backend`: `mvn -DskipTests package`
  - `frontend`: `npm install` / `npm run build`（npm auditで中程度の脆弱性が2件）
