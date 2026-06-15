import { API_BASE_URL } from '../config';
import {
  ApiEquipment,
  ApiKart,
  ApiTrack,
  CreatePaymentRequest,
  CreateRaceRequest,
  CreateRentalRequest,
  Customer,
  Employee,
  KartTypePrice,
  Payment,
  Race,
  RaceEquipment,
  RaceKart,
  Rental,
} from '../types';

async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const message = await response.text().catch(() => response.statusText);
    throw new Error(message || `Request failed (${response.status})`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const customerApi = {
  async getAll(): Promise<Customer[]> {
    return apiFetch<Customer[]>('/api/customers/get/all');
  },

  async getById(id: number): Promise<Customer> {
    return apiFetch<Customer>(`/api/customers/get/${id}`);
  },
};

export const employeeApi = {
  async getAll(): Promise<Employee[]> {
    return apiFetch<Employee[]>('/api/employees/get/all');
  },
};

export const trackApi = {
  async getAll(): Promise<ApiTrack[]> {
    return apiFetch<ApiTrack[]>('/api/tracks/get/all');
  },
};

export const kartApi = {
  async getAll(): Promise<ApiKart[]> {
    return apiFetch<ApiKart[]>('/api/karts/get/all');
  },
};

export const equipmentApi = {
  async getAll(): Promise<ApiEquipment[]> {
    return apiFetch<ApiEquipment[]>('/api/equipments/get/all');
  },
};

export const kartTypePriceApi = {
  async getAll(): Promise<KartTypePrice[]> {
    return apiFetch<KartTypePrice[]>('/api/kart-type-prices/get/all');
  },
};

export const rentalApi = {
  async getAll(): Promise<Rental[]> {
    return apiFetch<Rental[]>('/api/rentals/get/all');
  },

  async getById(id: number): Promise<Rental> {
    return apiFetch<Rental>(`/api/rentals/get/${id}`);
  },

  async create(rental: CreateRentalRequest): Promise<Rental> {
    return apiFetch<Rental>('/api/rentals/create', {
      method: 'POST',
      body: JSON.stringify(rental),
    });
  },

  async update(id: number, rental: Partial<Rental>): Promise<Rental> {
    return apiFetch<Rental>(`/api/rentals/update/${id}`, {
      method: 'PUT',
      body: JSON.stringify(rental),
    });
  },
};

export const raceApi = {
  async getAll(): Promise<Race[]> {
    return apiFetch<Race[]>('/api/races/get/all');
  },

  async create(race: CreateRaceRequest): Promise<Race> {
    return apiFetch<Race>('/api/races/create', {
      method: 'POST',
      body: JSON.stringify(race),
    });
  },
};

export const raceKartApi = {
  async create(raceKart: RaceKart): Promise<RaceKart> {
    return apiFetch<RaceKart>('/api/race-karts/create', {
      method: 'POST',
      body: JSON.stringify(raceKart),
    });
  },
};

export const raceEquipmentApi = {
  async create(raceEquipment: RaceEquipment): Promise<RaceEquipment> {
    return apiFetch<RaceEquipment>('/api/race-equipments/create', {
      method: 'POST',
      body: JSON.stringify(raceEquipment),
    });
  },
};

export const paymentApi = {
  async getAll(): Promise<Payment[]> {
    return apiFetch<Payment[]>('/api/payments/get/all');
  },

  async create(payment: CreatePaymentRequest): Promise<Payment> {
    return apiFetch<Payment>('/api/payments/create', {
      method: 'POST',
      body: JSON.stringify(payment),
    });
  },
};

/** First active employee, used as default staff for web bookings. */
export async function getDefaultEmployeeId(): Promise<number> {
  const employees = await employeeApi.getAll();
  const active = employees.find(e => e.status === 'active');
  if (!active) {
    throw new Error('No active employee found in the system');
  }
  return active.id;
}
