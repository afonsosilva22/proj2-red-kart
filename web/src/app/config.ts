export const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

/** Customer whose rentals are shown (no auth yet). Override via VITE_CUSTOMER_ID. */
export const DEFAULT_CUSTOMER_ID = Number(import.meta.env.VITE_CUSTOMER_ID) || 4;

export const IVA_RATE = 23.0;

/** Equipment rental prices — backend has no per-item rental price field. */
export const EQUIPMENT_RENTAL_PRICES: Record<string, number> = {
  helmet: 5,
  suit: 8,
  gloves: 3,
  protection: 4,
};

export const UPCOMING_STATUSES = ['scheduled', 'fully_payed'] as const;
export const ACTIVE_STATUSES = ['active', 'in_progress'] as const;
export const COMPLETED_STATUSES = ['finished', 'completed', 'cancelled'] as const;
