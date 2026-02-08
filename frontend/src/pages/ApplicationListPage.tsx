import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { searchApplications } from '../api/applicationApi';
import { extractApiError } from '../api/client';
import type { ApplicationStatus, ApplicationSummary, PageResponse } from '../api/types';

const pageSize = 10;

export function ApplicationListPage() {
  const [rows, setRows] = useState<ApplicationSummary[]>([]);
  const [q, setQ] = useState('');
  const [status, setStatus] = useState<ApplicationStatus | ''>('');
  const [sort, setSort] = useState('createdAt,desc');
  const [page, setPage] = useState(0);
  const [meta, setMeta] = useState<PageResponse<ApplicationSummary> | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    void fetchList();
  }, [page, sort]);

  async function fetchList() {
    try {
      setLoading(true);
      setError('');
      const response = await searchApplications({
        q: q || undefined,
        status: status || undefined,
        page,
        size: pageSize,
        sort
      });
      setRows(response.content);
      setMeta(response);
    } catch (e) {
      setError(extractApiError(e));
    } finally {
      setLoading(false);
    }
  }

  async function onSearch() {
    setPage(0);
    await fetchList();
  }

  return (
    <div className="stack">
      <div className="card stack">
        <h2>申請一覧</h2>
        <div className="form-grid">
          <label>
            キーワード
            <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="住所・理由" />
          </label>
          <label>
            ステータス
            <select value={status} onChange={(e) => setStatus(e.target.value as ApplicationStatus | '')}>
              <option value="">すべて</option>
              <option value="DRAFT">DRAFT</option>
              <option value="SUBMITTED">SUBMITTED</option>
              <option value="APPROVED">APPROVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>
          </label>
          <label>
            ソート
            <select value={sort} onChange={(e) => setSort(e.target.value)}>
              <option value="createdAt,desc">作成日 降順</option>
              <option value="createdAt,asc">作成日 昇順</option>
              <option value="updatedAt,desc">更新日 降順</option>
              <option value="updatedAt,asc">更新日 昇順</option>
            </select>
          </label>
        </div>
        <div className="actions">
          <button className="primary" type="button" onClick={onSearch} disabled={loading}>検索</button>
          <Link to="/applications/new">
            <button className="secondary" type="button">新規申請</button>
          </Link>
        </div>
        {error ? <div className="error">{error}</div> : null}
      </div>

      <div className="card">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>新住所</th>
                <th>状態</th>
                <th>更新日時</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.id}>
                  <td>{row.id}</td>
                  <td>{row.newAddress}</td>
                  <td><span className={`status ${row.status}`}>{row.status}</span></td>
                  <td>{new Date(row.updatedAt).toLocaleString()}</td>
                  <td><Link to={`/applications/${row.id}`}>詳細</Link></td>
                </tr>
              ))}
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={5} className="muted">データがありません</td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
        <div className="actions" style={{ marginTop: 12 }}>
          <button className="secondary" type="button" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
            前へ
          </button>
          <span className="muted">{(meta?.page ?? 0) + 1} / {Math.max(meta?.totalPages ?? 1, 1)} ページ</span>
          <button
            className="secondary"
            type="button"
            onClick={() => setPage((p) => p + 1)}
            disabled={meta ? page >= meta.totalPages - 1 : true}
          >
            次へ
          </button>
        </div>
      </div>
    </div>
  );
}
