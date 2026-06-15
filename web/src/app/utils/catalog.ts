import { EQUIPMENT_RENTAL_PRICES } from '../config';
import { ApiEquipment, ApiKart, ApiTrack, Equipment, Kart, KartTypePrice, Track } from '../types';

function mapKartTypePrice(type?: KartTypePrice): KartTypePrice | undefined {
  if (!type) return undefined;

  return {
    ...type,
    pricePerHour: normalizeNumber(type.pricePerHour),
  };
}

export function mapTrackFromApi(track: ApiTrack): Track {
  return {
    ...track,
    pricePerHour: normalizeNumber(track.pricePerHour),
    lengthKm: normalizeNumber(track.lengthKm),
  };
}

export function mapKartFromApi(kart: ApiKart): Kart {
  const typeName = kart.type?.type ?? 'Kart';
  return {
    id: kart.id,
    kartNumber: kart.kartNumber,
    mileage: kart.mileage,
    name: `Kart #${kart.kartNumber}`,
    model: `${typeName} (${kart.manufactureYear})`,
    status: kart.status,
    manufactureYear: kart.manufactureYear,
    lastServiceDate: kart.lastServiceDate,
    type: mapKartTypePrice(kart.type),
  };
}

export function mapEquipmentFromApi(item: ApiEquipment): Equipment {
  const label = [item.type, item.brand, item.size].filter(Boolean).join(' · ');
  return {
    id: item.id,
    name: label.charAt(0).toUpperCase() + label.slice(1),
    size: item.size,
    brand: item.brand,
    color: item.color,
    acquisitionDate: item.acquisitionDate,
    type: item.type,
    pricePerUnit: EQUIPMENT_RENTAL_PRICES[item.type] ?? 5,
    stockQuantity: item.status === 'available' ? 10 : 0,
    status: item.status,
  };
}

export function normalizeNumber(value: unknown): number {
  if (typeof value === 'number') return value;
  if (typeof value === 'string') return parseFloat(value) || 0;
  return 0;
}
