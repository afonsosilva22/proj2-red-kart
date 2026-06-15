// Types matching the Spring Boot backend models

export interface Customer {
  id: number;
  name: string;
  email: string;
  phone?: string | null;
  status?: string;
}

export interface Employee {
  id: number;
  name: string;
  status?: string;
}

export interface Track {
  id: number;
  name: string;
  pricePerHour: number;
  lengthKm: number;
  kartLimit: number;
  status: string;
}

export interface ApiTrack {
  id: number;
  name: string;
  pricePerHour: number | string;
  lengthKm: number | string;
  kartLimit: number;
  status: string;
}

export interface KartTypePrice {
  type: string;
  pricePerHour: number | string;
}

/** Raw kart shape returned by the backend. */
export interface ApiKart {
  id: number;
  kartNumber: string;
  mileage: number;
  manufactureYear: number;
  lastServiceDate?: string | null;
  status: string;
  type?: KartTypePrice;
}

/** Kart enriched for UI display. */
export interface Kart {
  id: number;
  name: string;
  model: string;
  status: string;
  kartNumber: string;
  mileage: number;
  manufactureYear: number;
  lastServiceDate?: string | null;
  type?: KartTypePrice;
}

/** Raw equipment shape returned by the backend. */
export interface ApiEquipment {
  id: number;
  size: string;
  brand?: string | null;
  color?: string | null;
  acquisitionDate?: string | null;
  type: string;
  status: string;
}

/** Equipment enriched for UI display. */
export interface Equipment {
  id: number;
  name: string;
  size: string;
  brand?: string | null;
  color?: string | null;
  acquisitionDate?: string | null;
  type: string;
  pricePerUnit: number;
  stockQuantity: number;
  status: string;
}

export interface Rental {
  id?: number;
  plannedStartDatetime: string;
  plannedEndDatetime: string;
  actualStartDatetime?: string | null;
  actualEndDatetime?: string | null;
  basePrice: number;
  discount?: number;
  complaint?: string | null;
  type: 'kart' | 'track';
  status: string;
  customer?: Customer;
  employee?: Employee;
}

export interface Race {
  id?: number;
  startDatetime: string;
  endDatetime: string;
  status: string;
  rental?: Rental;
  track?: Track;
  raceKarts?: RaceKart[];
  raceEquipments?: RaceEquipment[];
}

export interface RaceKart {
  id?: {
    raceId: number;
    kartId: number;
  };
  race?: { id: number };
  kart: ApiKart;
}

export interface RaceEquipment {
  id?: {
    raceId: number;
    equipmentId: number;
  };
  race?: { id: number };
  equipment: ApiEquipment;
  quantity: number;
}

export interface Payment {
  id?: number;
  paymentDate: string;
  amountPaid: number;
  ivaRate: number;
  paymentMethod: string;
  rental?: Rental;
}

export interface CreateRentalRequest {
  plannedStartDatetime: string;
  plannedEndDatetime: string;
  basePrice: number;
  discount?: number;
  type: 'kart' | 'track';
  status: string;
  customer: { id: number };
  employee: { id: number };
}

export interface CreateRaceRequest {
  startDatetime: string;
  endDatetime: string;
  status: string;
  rental: { id: number };
  employee: { id: number };
  track: { id: number };
  raceKarts?: RaceKart[];
  raceEquipments?: RaceEquipment[];
}

export interface CreatePaymentRequest {
  paymentDate: string;
  amountPaid: number;
  ivaRate: number;
  paymentMethod: string;
  rental: { id: number };
}
