import { Rental, Customer, Track, Kart, Equipment } from '../types';

// Mock current customer (in a real app this comes from authentication)
export const mockCustomer: Customer = {
  id: 1,
  name: "John Doe",
  email: "john.doe@example.com",
  phone: "+351 912 345 678"
};

export const mockTracks: Track[] = [
  {
    id: 1,
    name: "Grand Prix Circuit",
    pricePerHour: 120.00,
    lengthKm: 1.2,
    kartLimit: 12,
    status: "available"
  },
  {
    id: 2,
    name: "Sprint Loop",
    pricePerHour: 80.00,
    lengthKm: 0.6,
    kartLimit: 6,
    status: "available"
  },
  {
    id: 3,
    name: "Junior Track",
    pricePerHour: 60.00,
    lengthKm: 0.4,
    kartLimit: 8,
    status: "available"
  }
];

export const mockKarts: Kart[] = [
  { id: 1, name: "Kart #01", model: "Sodi RT8", status: "available", maxSpeedKmh: 70 },
  { id: 2, name: "Kart #02", model: "Sodi RT8", status: "available", maxSpeedKmh: 70 },
  { id: 3, name: "Kart #03", model: "Sodi SR4", status: "available", maxSpeedKmh: 80 },
  { id: 4, name: "Kart #04", model: "Sodi SR4", status: "available", maxSpeedKmh: 80 },
  { id: 5, name: "Kart #05", model: "Birel ART N35", status: "available", maxSpeedKmh: 90 },
  { id: 6, name: "Kart #06", model: "Birel ART N35", status: "available", maxSpeedKmh: 90 },
  { id: 7, name: "Kart #07", model: "Sodi RT8", status: "maintenance" },
  { id: 8, name: "Kart #08", model: "Sodi RT8", status: "available", maxSpeedKmh: 70 },
];

export const mockEquipment: Equipment[] = [
  { id: 1, name: "Full-Face Helmet", type: "helmet", pricePerUnit: 5.00, stockQuantity: 20 },
  { id: 2, name: "Racing Suit", type: "suit", pricePerUnit: 8.00, stockQuantity: 15 },
  { id: 3, name: "Racing Gloves", type: "gloves", pricePerUnit: 3.00, stockQuantity: 25 },
  { id: 4, name: "Neck Brace", type: "protection", pricePerUnit: 4.00, stockQuantity: 10 },
  { id: 5, name: "Rib Protector", type: "protection", pricePerUnit: 6.00, stockQuantity: 12 },
];

export const mockRentals: Rental[] = [
  {
    id: 1,
    plannedStartDatetime: "2026-06-20T14:00:00Z",
    plannedEndDatetime: "2026-06-20T15:00:00Z",
    actualStartDatetime: null,
    actualEndDatetime: null,
    basePrice: 45.00,
    discount: 0,
    complaint: null,
    type: "kart",
    status: "scheduled",
    customer: mockCustomer,
    employee: { id: 1, name: "Staff Member" }
  },
  {
    id: 2,
    plannedStartDatetime: "2026-06-15T10:00:00Z",
    plannedEndDatetime: "2026-06-15T11:00:00Z",
    actualStartDatetime: "2026-06-15T10:05:00Z",
    actualEndDatetime: "2026-06-15T11:02:00Z",
    basePrice: 45.00,
    discount: 5.00,
    complaint: null,
    type: "kart",
    status: "completed",
    customer: mockCustomer,
    employee: { id: 2, name: "Staff Member" }
  },
  {
    id: 3,
    plannedStartDatetime: "2026-06-10T16:00:00Z",
    plannedEndDatetime: "2026-06-10T17:30:00Z",
    actualStartDatetime: "2026-06-10T16:00:00Z",
    actualEndDatetime: "2026-06-10T17:25:00Z",
    basePrice: 120.00,
    discount: 10.00,
    complaint: null,
    type: "track",
    status: "completed",
    customer: mockCustomer,
    employee: { id: 1, name: "Staff Member" }
  },
  {
    id: 4,
    plannedStartDatetime: "2026-06-25T18:00:00Z",
    plannedEndDatetime: "2026-06-25T19:00:00Z",
    actualStartDatetime: null,
    actualEndDatetime: null,
    basePrice: 120.00,
    discount: 0,
    complaint: null,
    type: "track",
    status: "scheduled",
    customer: mockCustomer,
    employee: { id: 3, name: "Staff Member" }
  }
];

export const PRICING = {
  kart: {
    halfHour: 25.00,
    oneHour: 45.00,
    ninetyMin: 65.00,
  },
  track: {
    perHour: 120.00,
  }
};

export const IVA_RATE = 23.00;
