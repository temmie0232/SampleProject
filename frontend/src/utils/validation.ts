export function required(value: string, label: string): string | null {
  if (!value || value.trim().length === 0) {
    return `${label}は必須です`;
  }
  return null;
}

export function maxLength(value: string, max: number, label: string): string | null {
  if (value.length > max) {
    return `${label}は${max}文字以内で入力してください`;
  }
  return null;
}

export function validateApplicationForm(values: {
  currentAddress: string;
  newAddress: string;
  reason: string;
}) {
  const errors: Record<string, string> = {};

  const currentRequired = required(values.currentAddress, '現住所');
  if (currentRequired) errors.currentAddress = currentRequired;
  const currentMax = maxLength(values.currentAddress, 255, '現住所');
  if (currentMax) errors.currentAddress = currentMax;

  const newRequired = required(values.newAddress, '新住所');
  if (newRequired) errors.newAddress = newRequired;
  const newMax = maxLength(values.newAddress, 255, '新住所');
  if (newMax) errors.newAddress = newMax;

  const reasonRequired = required(values.reason, '変更理由');
  if (reasonRequired) errors.reason = reasonRequired;
  const reasonMax = maxLength(values.reason, 500, '変更理由');
  if (reasonMax) errors.reason = reasonMax;

  return errors;
}
