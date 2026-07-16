export default function StorageBar({ segments, height = 10 }) {
  const total = segments.reduce((sum, s) => sum + s.value, 0) || 1;

  return (
    <div className="storage-bar" style={{ height }}>
      {segments.map((s, i) => (
        <div
          key={i}
          className="storage-bar-segment"
          style={{
            width: `${(s.value / total) * 100}%`,
            background: s.color,
          }}
          title={`${s.label}: ${s.value}`}
        />
      ))}
    </div>
  );
}
