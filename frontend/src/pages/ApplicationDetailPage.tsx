import { FormEvent, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  approveApplication,
  getApplication,
  getApplicationHistory,
  rejectApplication,
  submitApplication,
  updateApplication
} from '../api/applicationApi';
import { extractApiError } from '../api/client';
import type { ApplicationDetail, ApplicationHistory } from '../api/types';
import { useAuth } from '../features/auth/AuthContext';
import { validateApplicationForm } from '../utils/validation';

export function ApplicationDetailPage() {
  const { id } = useParams();
  const appId = Number(id);
  const auth = useAuth();

  const [detail, setDetail] = useState<ApplicationDetail | null>(null);
  const [history, setHistory] = useState<ApplicationHistory[]>([]);

  const [currentAddress, setCurrentAddress] = useState('');
  const [newAddress, setNewAddress] = useState('');
  const [reason, setReason] = useState('');

  const [comment, setComment] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(appId)) {
      setError('不正なIDです');
      return;
    }
    void load();
  }, [appId]);

  async function load() {
    try {
      setLoading(true);
      setError('');
      const [d, h] = await Promise.all([getApplication(appId), getApplicationHistory(appId)]);
      setDetail(d);
      setHistory(h);
      setCurrentAddress(d.currentAddress);
      setNewAddress(d.newAddress);
      setReason(d.reason);
    } catch (e) {
      setError(extractApiError(e));
    } finally {
      setLoading(false);
    }
  }

  async function onUpdate(event: FormEvent) {
    event.preventDefault();
    if (!detail) return;

    const nextErrors = validateApplicationForm({ currentAddress, newAddress, reason });
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    try {
      const updated = await updateApplication(detail.id, { currentAddress, newAddress, reason });
      setDetail(updated);
    } catch (e) {
      setError(extractApiError(e));
    }
  }

  async function onSubmitApplication() {
    if (!detail) return;
    try {
      const submitted = await submitApplication(detail.id, { comment });
      setDetail(submitted);
      await load();
    } catch (e) {
      setError(extractApiError(e));
    }
  }

  async function onApprove() {
    if (!detail) return;
    try {
      const approved = await approveApplication(detail.id, { comment });
      setDetail(approved);
      await load();
    } catch (e) {
      setError(extractApiError(e));
    }
  }

  async function onReject() {
    if (!detail) return;
    if (!rejectionReason.trim()) {
      setError('却下理由は必須です');
      return;
    }
    try {
      const rejected = await rejectApplication(detail.id, { comment, rejectionReason });
      setDetail(rejected);
      await load();
    } catch (e) {
      setError(extractApiError(e));
    }
  }

  if (loading && !detail) {
    return <div className="card">読み込み中...</div>;
  }

  if (!detail) {
    return <div className="card error">{error || 'データを取得できませんでした'}</div>;
  }

  const editable = auth.user?.role === 'USER' && (detail.status === 'DRAFT' || detail.status === 'REJECTED');
  const canSubmit = auth.user?.role === 'USER' && (detail.status === 'DRAFT' || detail.status === 'REJECTED');
  const canAdminDecision = auth.user?.role === 'ADMIN' && detail.status === 'SUBMITTED';

  return (
    <div className="stack">
      <div className="card stack">
        <h2>申請詳細 #{detail.id}</h2>
        <div className="muted">状態: <span className={`status ${detail.status}`}>{detail.status}</span></div>

        <form onSubmit={onUpdate} className="stack">
          <label>
            現住所
            <input value={currentAddress} onChange={(e) => setCurrentAddress(e.target.value)} disabled={!editable} />
            {errors.currentAddress ? <span className="error">{errors.currentAddress}</span> : null}
          </label>

          <label>
            新住所
            <input value={newAddress} onChange={(e) => setNewAddress(e.target.value)} disabled={!editable} />
            {errors.newAddress ? <span className="error">{errors.newAddress}</span> : null}
          </label>

          <label>
            変更理由
            <textarea value={reason} onChange={(e) => setReason(e.target.value)} disabled={!editable} />
            {errors.reason ? <span className="error">{errors.reason}</span> : null}
          </label>

          {editable ? (
            <div className="actions">
              <button className="secondary" type="submit">内容を更新</button>
            </div>
          ) : null}
        </form>

        <label>
          コメント（履歴に残ります）
          <textarea value={comment} onChange={(e) => setComment(e.target.value)} />
        </label>

        {canSubmit ? (
          <div className="actions">
            <button className="primary" type="button" onClick={onSubmitApplication}>申請を提出</button>
          </div>
        ) : null}

        {canAdminDecision ? (
          <div className="stack">
            <label>
              却下理由（却下時は必須）
              <textarea value={rejectionReason} onChange={(e) => setRejectionReason(e.target.value)} />
            </label>
            <div className="actions">
              <button className="primary" type="button" onClick={onApprove}>承認</button>
              <button className="danger" type="button" onClick={onReject}>却下</button>
            </div>
          </div>
        ) : null}

        {detail.rejectionReason ? <div className="error">却下理由: {detail.rejectionReason}</div> : null}
        {error ? <div className="error">{error}</div> : null}
      </div>

      <div className="card">
        <h3>状態遷移履歴</h3>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>日時</th>
                <th>遷移</th>
                <th>実行者</th>
                <th>コメント</th>
              </tr>
            </thead>
            <tbody>
              {history.map((h) => (
                <tr key={h.id}>
                  <td>{new Date(h.actedAt).toLocaleString()}</td>
                  <td>{h.fromStatus} → {h.toStatus}</td>
                  <td>{h.actedByUserId}</td>
                  <td>{h.comment || '-'}</td>
                </tr>
              ))}
              {history.length === 0 ? (
                <tr>
                  <td colSpan={4} className="muted">履歴はまだありません</td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
