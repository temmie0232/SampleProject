import { useEffect, useState } from 'react';
import { searchAuditLogs } from '../api/auditApi';
import { extractApiError } from '../api/client';
import type { AuditLog, PageResponse } from '../api/types';

const pageSize = 20;

export function AuditLogPage() {
  const [rows, setRows] = useState<AuditLog[]>([]);
  const [actorUserId, setActorUserId] = useState('');
  const [actionType, setActionType] = useState('');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [page, setPage] = useState(0);
  const [meta, setMeta] = useState<PageResponse<AuditLog> | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    void fetchLogs();
  }, [page]);

  async function fetchLogs() {
    try {
      setLoading(true);
      setError('');
      const response = await searchAuditLogs({
        actorUserId: actorUserId ? Number(actorUserId) : undefined,
        actionType: actionType || undefined,
        from: from ? new Date(from).toISOString() : undefined,
        to: to ? new Date(to).toISOString() : undefined,
        page,
        size: pageSize
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
    await fetchLogs();
  }

  return (
    <div className="stack">
      <div className="card stack">
        <h2>監査ログ</h2>
        <div className="form-grid">
          <label>
            実行ユーザーID
            <input value={actorUserId} onChange={(e) => setActorUserId(e.target.value)} />
          </label>
          <label>
            アクション種別
            <input value={actionType} onChange={(e) => setActionType(e.target.value)} placeholder="APPLICATION_APPROVE 等" />
          </label>
          <label>
            開始日時
            <input type="datetime-local" value={from} onChange={(e) => setFrom(e.target.value)} />
          </label>
          <label>
            終了日時
            <input type="datetime-local" value={to} onChange={(e) => setTo(e.target.value)} />
          </label>
        </div>
        <div className="actions">
          <button className="primary" type="button" onClick={onSearch} disabled={loading}>検索</button>
        </div>
        {error ? <div className="error">{error}</div> : null}
      </div>

      <div className="card">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>時刻</th>
                <th>requestId</th>
                <th>actor</th>
                <th>action</th>
                <th>target</th>
                <th>result</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.id}>
                  <td>{new Date(row.createdAt).toLocaleString()}</td>
                  <td>{row.requestId}</td>
                  <td>{row.actorUserId ?? '-'}</td>
                  <td>{row.actionType}</td>
                  <td>{row.targetType}:{row.targetId}</td>
                  <td>{row.result}</td>
                </tr>
              ))}
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={6} className="muted">データがありません</td>
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
