export default function HashPill({ value, label = 'SHA-256' }) {
  if (!value) return <span className="hash-pill hash-pill-empty mono">not computed</span>;
  const short = value.length > 16 ? `${value.slice(0, 8)}…${value.slice(-6)}` : value;

  const copy = () => {
    navigator.clipboard?.writeText(value).catch(() => {});
  };

  return (
    <button className="hash-pill mono" onClick={copy} title={`${label} · click to copy full checksum`}>
      <span className="hash-pill-label">{label}</span>
      <span className="hash-pill-value">{short}</span>
    </button>
  );
}
