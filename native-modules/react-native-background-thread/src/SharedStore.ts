export interface ISharedStore {
  set(key: string, value: string | number | boolean): void;
  get(key: string): string | number | boolean | undefined;
  has(key: string): boolean;
  delete(key: string): boolean;
  keys(): string[];
  clear(): void;
  readonly size: number;
}

declare global {
  var sharedStore: ISharedStore | undefined;
}

export function getSharedStore(): ISharedStore | undefined {
  return globalThis.sharedStore;
}
