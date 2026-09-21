/** Shape-only recording: arbitrary string values (including URL paths) are never persisted. */
export function safeShape(value: unknown, depth = 0): unknown {
  if (depth > 8) return { type: "truncated" };
  if (value === null || value === undefined) return { type: "null", present: false };
  if (typeof value === "string") return { type: "string", length: value.length, present: true, value: "[REDACTED]" };
  if (Array.isArray(value)) return { type: "array", length: value.length, items: value.slice(0, 3).map(v => safeShape(v, depth + 1)) };
  if (typeof value === "object") return Object.fromEntries(Object.entries(value).slice(0, 100).map(([key, item]) => [
    /^[a-zA-Z_][a-zA-Z0-9_]{0,63}$/.test(key) ? key : "[REDACTED_FIELD]",
    /password|passwd|secret|token|authorization|cookie|key|signature|session/i.test(key)
      ? { type: typeof item, present: item !== null && item !== undefined, value: "[REDACTED]" }
      : safeShape(item, depth + 1),
  ]));
  return { type: typeof value, present: true };
}
export function structuralTrace(component: "auth" | "catalog" | "broker" | "tvcore" | "player", event: "call" | "return" | "error", args: unknown) {
  return { timestamp: new Date().toISOString(), component, event, safeArgs: safeShape(args) };
}
