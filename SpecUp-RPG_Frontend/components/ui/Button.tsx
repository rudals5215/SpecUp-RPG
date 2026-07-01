interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  type?: "button" | "submit";
  disabled?: boolean;
  loading?: boolean;
  variant?: "primary" | "ghost";
}

export function Button({
  children,
  onClick,
  type = "button",
  disabled = false,
  loading = false,
  variant = "primary",
}: ButtonProps) {
  const baseStyle =
    "w-full rounded-card py-3 font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed";

  const variantStyle =
    variant === "primary"
      ? "bg-primary text-base hover:bg-primary-dim"
      : "bg-transparent text-ink-muted border border-border hover:bg-surface-hover";

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`${baseStyle} ${variantStyle}`}
    >
      {loading ? "처리 중..." : children}
    </button>
  );
}
