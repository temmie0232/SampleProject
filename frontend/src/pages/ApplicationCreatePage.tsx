import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createApplication } from '../api/applicationApi';
import { extractApiError } from '../api/client';
import { validateApplicationForm } from '../utils/validation';

export function ApplicationCreatePage() {
  const navigate = useNavigate();

  const [currentAddress, setCurrentAddress] = useState('');
  const [newAddress, setNewAddress] = useState('');
  const [reason, setReason] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [submitError, setSubmitError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitError('');

    const nextErrors = validateApplicationForm({ currentAddress, newAddress, reason });
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    try {
      setSubmitting(true);
      const created = await createApplication({ currentAddress, newAddress, reason });
      navigate(`/applications/${created.id}`);
    } catch (e) {
      setSubmitError(extractApiError(e));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="card stack">
      <h2>住所変更申請の作成</h2>
      <form onSubmit={onSubmit} className="stack">
        <label>
          現住所
          <input value={currentAddress} onChange={(e) => setCurrentAddress(e.target.value)} />
          {errors.currentAddress ? <span className="error">{errors.currentAddress}</span> : null}
        </label>

        <label>
          新住所
          <input value={newAddress} onChange={(e) => setNewAddress(e.target.value)} />
          {errors.newAddress ? <span className="error">{errors.newAddress}</span> : null}
        </label>

        <label>
          変更理由
          <textarea value={reason} onChange={(e) => setReason(e.target.value)} />
          {errors.reason ? <span className="error">{errors.reason}</span> : null}
        </label>

        {submitError ? <div className="error">{submitError}</div> : null}
        <div className="actions">
          <button className="primary" type="submit" disabled={submitting}>
            {submitting ? '登録中...' : '申請を作成'}
          </button>
        </div>
      </form>
    </div>
  );
}
