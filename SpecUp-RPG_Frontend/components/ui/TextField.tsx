interface TextFieldProps {
  label: string;
  type?: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  error?: string;
}

// 시그니처 디테일: 라벨 앞에 "$" 프롬프트 기호를 붙여서
// "개발자를 위한 RPG"라는 정체성을 로그인 폼에서부터 드러내요
export function TextField({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  error,
}: TextFieldProps) {
  return (
    <div className="flex flex-col gap-2">
      <label className="font-mono text-sm text-ink-muted">
        <span className="text-primary">$</span> {label}
      </label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className={`
          w-full rounded-card border bg-surface px-4 py-3 text-ink
          placeholder:text-ink-faint
          focus:outline-none focus:ring-2 focus:ring-primary/50
          transition-colors
          ${error ? "border-red-500" : "border-border focus:border-primary"}
        `}
      />
      {error && <p className="text-sm text-red-400">{error}</p>}
    </div>
  );
}
