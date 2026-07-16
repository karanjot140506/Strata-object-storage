const CONFIG = {
  ACTIVE: { label: 'Active', varName: '--active', dim: '--active-dim' },
  ARCHIVED: { label: 'Archived', varName: '--archived', dim: '--archived-dim' },
  DELETED: { label: 'Deleted', varName: '--deleted', dim: '--deleted-dim' },
};

export default function StatusBadge({ status }) {
  const cfg = CONFIG[status] || CONFIG.ACTIVE;
  return (
    <span
      className="status-badge"
      style={{
        color: `var(${cfg.varName})`,
        background: `var(${cfg.dim})`,
        borderColor: `var(${cfg.varName})`,
      }}
    >
      <span className="status-dot" style={{ background: `var(${cfg.varName})` }} />
      {cfg.label}
    </span>
  );
}
