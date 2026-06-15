import { useEffect, useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';
import { Badge } from './ui/badge';
import { Calendar, CreditCard, Flag, CheckCircle2, Circle, Minus, Plus, Loader2 } from 'lucide-react';
import { Rental, Track, Kart, Equipment } from '../types';
import { IVA_RATE } from '../config';
import {
  equipmentApi,
  getDefaultEmployeeId,
  kartApi,
  paymentApi,
  raceEquipmentApi,
  raceApi,
  raceKartApi,
  rentalApi,
  trackApi,
} from '../services/api';
import { mapEquipmentFromApi, mapKartFromApi, mapTrackFromApi, normalizeNumber } from '../utils/catalog';
import { toast } from 'sonner';

interface NewRentalDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onRentalCreated: (rental: Rental) => void;
  customerId: number;
}

type Step = 'rental' | 'race' | 'payment';

interface EquipmentSelection {
  equipmentId: number;
  quantity: number;
}

export function NewRentalDialog({ open, onOpenChange, onRentalCreated, customerId }: NewRentalDialogProps) {
  const [step, setStep] = useState<Step>('rental');

  const [rentalType, setRentalType] = useState<'kart' | 'track'>('kart');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [duration, setDuration] = useState<'30' | '60' | '90'>('60');

  const [tracks, setTracks] = useState<Track[]>([]);
  const [karts, setKarts] = useState<Kart[]>([]);
  const [equipment, setEquipment] = useState<Equipment[]>([]);
  const [isCatalogLoading, setIsCatalogLoading] = useState(false);
  const [employeeId, setEmployeeId] = useState<number | null>(null);

  const [selectedTrackId, setSelectedTrackId] = useState<number | null>(null);
  const [selectedKartIds, setSelectedKartIds] = useState<number[]>([]);
  const [equipmentSelections, setEquipmentSelections] = useState<EquipmentSelection[]>([]);

  const [paymentMethod, setPaymentMethod] = useState<'credit_card' | 'debit_card' | 'cash'>('credit_card');
  const [cardNumber, setCardNumber] = useState('');
  const [cardExpiry, setCardExpiry] = useState('');
  const [cardCvv, setCardCvv] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    if (!open) return;

    const loadCatalog = async () => {
      setIsCatalogLoading(true);
      try {
        const [trackData, kartData, equipmentData, defaultEmployeeId] = await Promise.all([
          trackApi.getAll(),
          kartApi.getAll(),
          equipmentApi.getAll(),
          getDefaultEmployeeId(),
        ]);

        setTracks(trackData.map(mapTrackFromApi));
        setKarts(kartData.map(mapKartFromApi));
        setEquipment(equipmentData.map(mapEquipmentFromApi));
        setEmployeeId(defaultEmployeeId);
      } catch (error) {
        console.error('Failed to load booking catalog:', error);
        toast.error('Failed to load tracks and karts', {
          description: 'Make sure the backend is running.',
        });
      } finally {
        setIsCatalogLoading(false);
      }
    };

    loadCatalog();
  }, [open]);

  const selectedTrack = tracks.find(t => t.id === selectedTrackId);
  const durationHours = parseInt(duration) / 60;

  const basePrice = (() => {
    if (rentalType === 'kart') {
      const selectedKarts = karts.filter(k => selectedKartIds.includes(k.id));
      const hourlyRate = selectedKarts.reduce(
        (sum, kart) => sum + normalizeNumber(kart.type?.pricePerHour),
        0
      );
      return hourlyRate * durationHours;
    }
    return normalizeNumber(selectedTrack?.pricePerHour) * durationHours;
  })();

  const equipmentTotal = equipmentSelections.reduce((sum, sel) => {
    const eq = equipment.find(e => e.id === sel.equipmentId);
    return sum + (eq?.pricePerUnit ?? 0) * sel.quantity;
  }, 0);

  const subtotal = basePrice + equipmentTotal;
  const ivaAmount = subtotal * IVA_RATE / 100;
  const total = subtotal + ivaAmount;

  const toggleKart = (kartId: number) => {
    setSelectedKartIds(prev =>
      prev.includes(kartId) ? prev.filter(id => id !== kartId) : [...prev, kartId]
    );
  };

  const setEquipmentQty = (equipmentId: number, qty: number) => {
    if (qty <= 0) {
      setEquipmentSelections(prev => prev.filter(s => s.equipmentId !== equipmentId));
    } else {
      setEquipmentSelections(prev => {
        const existing = prev.find(s => s.equipmentId === equipmentId);
        if (existing) return prev.map(s => s.equipmentId === equipmentId ? { ...s, quantity: qty } : s);
        return [...prev, { equipmentId, quantity: qty }];
      });
    }
  };

  const getEquipmentQty = (equipmentId: number) =>
    equipmentSelections.find(s => s.equipmentId === equipmentId)?.quantity ?? 0;

  const handleStep1Continue = () => {
    if (!selectedDate || !selectedTime) {
      alert('Please select a date and time.');
      return;
    }
    setStep('race');
  };

  const handleStep2Continue = () => {
    if (!selectedTrackId) {
      alert('Please select a track.');
      return;
    }
    if (selectedKartIds.length === 0) {
      alert('Please select at least one kart.');
      return;
    }
    setStep('payment');
  };

  const handlePaymentSubmit = async () => {
    if (paymentMethod !== 'cash' && (!cardNumber || !cardExpiry || !cardCvv)) {
      alert('Please fill in all card details.');
      return;
    }
    if (!employeeId) {
      toast.error('No staff member available to process the booking.');
      return;
    }

    setIsProcessing(true);

    try {
      const startDateTime = new Date(`${selectedDate}T${selectedTime}:00`);
      const endDateTime = new Date(startDateTime.getTime() + parseInt(duration) * 60000);

      const createdRental = await rentalApi.create({
        plannedStartDatetime: startDateTime.toISOString(),
        plannedEndDatetime: endDateTime.toISOString(),
        basePrice: subtotal,
        discount: 0,
        type: rentalType,
        status: 'scheduled',
        customer: { id: customerId },
        employee: { id: employeeId },
      });

      const createdRace = await raceApi.create({
        startDatetime: startDateTime.toISOString(),
        endDatetime: endDateTime.toISOString(),
        status: 'scheduled',
        rental: { id: createdRental.id! },
        employee: { id: employeeId },
        track: { id: selectedTrackId! },
      });

      const selectedKarts = selectedKartIds
        .map(kartId => karts.find(kart => kart.id === kartId))
        .filter((kart): kart is Kart => Boolean(kart));

      await Promise.all([
        ...selectedKarts.map(kart =>
          raceKartApi.create({
            id: { raceId: createdRace.id!, kartId: kart.id },
            race: { id: createdRace.id! },
            kart: {
              id: kart.id,
              kartNumber: kart.kartNumber,
              mileage: kart.mileage,
              manufactureYear: kart.manufactureYear,
              lastServiceDate: kart.lastServiceDate,
              status: kart.status,
              type: kart.type,
            },
          })
        ),
        ...equipmentSelections
          .map(sel => {
            const item = equipment.find(eq => eq.id === sel.equipmentId);
            if (!item) return null;

            return raceEquipmentApi.create({
              id: { raceId: createdRace.id!, equipmentId: item.id },
              race: { id: createdRace.id! },
              equipment: {
                id: item.id,
                size: item.size,
                brand: item.brand,
                color: item.color,
                acquisitionDate: item.acquisitionDate,
                type: item.type,
                status: item.status,
              },
              quantity: sel.quantity,
            });
          })
          .filter((request): request is Promise<unknown> => Boolean(request)),
      ]);

      await paymentApi.create({
        paymentDate: new Date().toISOString(),
        amountPaid: total,
        ivaRate: IVA_RATE,
        paymentMethod,
        rental: { id: createdRental.id! },
      });

      onRentalCreated(createdRental);
      resetForm();
      onOpenChange(false);
    } catch (error) {
      console.error('Failed to create booking:', error);
      toast.error('Failed to create booking', {
        description: error instanceof Error ? error.message : 'Please try again.',
      });
    } finally {
      setIsProcessing(false);
    }
  };

  const resetForm = () => {
    setStep('rental');
    setRentalType('kart');
    setSelectedDate('');
    setSelectedTime('');
    setDuration('60');
    setSelectedTrackId(null);
    setSelectedKartIds([]);
    setEquipmentSelections([]);
    setPaymentMethod('credit_card');
    setCardNumber('');
    setCardExpiry('');
    setCardCvv('');
  };

  const handleClose = () => {
    resetForm();
    onOpenChange(false);
  };

  const stepIndex = step === 'rental' ? 0 : step === 'race' ? 1 : 2;
  const steps = ['Rental', 'Race Setup', 'Payment'];

  const availableTracks = tracks.filter(t => t.status === 'available');
  const availableKarts = karts.filter(k => k.status === 'available');
  const availableEquipment = equipment.filter(e => e.status === 'available');

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-lg max-h-[92vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            {step === 'rental' && <><Calendar className="w-5 h-5 text-primary" /> Book a Rental</>}
            {step === 'race' && <><Flag className="w-5 h-5 text-primary" /> Race Setup</>}
            {step === 'payment' && <><CreditCard className="w-5 h-5 text-primary" /> Payment</>}
          </DialogTitle>
          <DialogDescription>
            {step === 'rental' && 'Choose your rental type, date, time and duration.'}
            {step === 'race' && 'Select the track, karts and equipment for your race.'}
            {step === 'payment' && 'Confirm your booking and complete payment.'}
          </DialogDescription>
        </DialogHeader>

        {isCatalogLoading && step === 'race' ? (
          <div className="flex flex-col items-center py-12 text-muted-foreground gap-3">
            <Loader2 className="w-6 h-6 animate-spin text-primary" />
            <p className="text-sm">Loading tracks and karts…</p>
          </div>
        ) : (
          <>
            <div className="flex items-center gap-2 py-2">
              {steps.map((label, i) => (
                <div key={label} className="flex items-center gap-2 flex-1">
                  <div className={`flex items-center gap-1.5 text-xs ${i === stepIndex ? 'text-primary' : i < stepIndex ? 'text-green-600' : 'text-muted-foreground'}`}>
                    {i < stepIndex
                      ? <CheckCircle2 className="w-4 h-4" />
                      : <Circle className={`w-4 h-4 ${i === stepIndex ? 'fill-primary text-primary' : ''}`} />
                    }
                    <span className={i === stepIndex ? 'font-medium' : ''}>{label}</span>
                  </div>
                  {i < steps.length - 1 && (
                    <div className={`flex-1 h-px ${i < stepIndex ? 'bg-green-400' : 'bg-border'}`} />
                  )}
                </div>
              ))}
            </div>

            {step === 'rental' && (
              <div className="space-y-5 py-2">
                <div className="space-y-3">
                  <Label>Rental Type</Label>
                  <RadioGroup value={rentalType} onValueChange={(v: 'kart' | 'track') => setRentalType(v)} className="grid grid-cols-2 gap-3">
                    <label htmlFor="kart" className={`flex flex-col gap-1 border rounded-lg p-4 cursor-pointer transition-colors ${rentalType === 'kart' ? 'border-primary bg-primary/5' : 'hover:bg-accent'}`}>
                      <div className="flex items-center gap-2">
                        <RadioGroupItem value="kart" id="kart" />
                        <span className="font-medium">Kart</span>
                      </div>
                      <p className="text-xs text-muted-foreground pl-6">Shared track — join other racers</p>
                    </label>
                    <label htmlFor="track" className={`flex flex-col gap-1 border rounded-lg p-4 cursor-pointer transition-colors ${rentalType === 'track' ? 'border-primary bg-primary/5' : 'hover:bg-accent'}`}>
                      <div className="flex items-center gap-2">
                        <RadioGroupItem value="track" id="track" />
                        <span className="font-medium">Track</span>
                      </div>
                      <p className="text-xs text-muted-foreground pl-6">Exclusive track — just your group</p>
                    </label>
                  </RadioGroup>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="date">Date</Label>
                  <Input
                    id="date"
                    type="date"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    min={new Date().toISOString().split('T')[0]}
                  />
                </div>

                <div className="space-y-2">
                  <Label>Start Time</Label>
                  <Select value={selectedTime} onValueChange={setSelectedTime}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select time" />
                    </SelectTrigger>
                    <SelectContent>
                      {['09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00'].map(t => (
                        <SelectItem key={t} value={t}>
                          {new Date(`2000-01-01T${t}`).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Duration</Label>
                  <Select value={duration} onValueChange={(v: '30' | '60' | '90') => setDuration(v)}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="30">30 minutes</SelectItem>
                      <SelectItem value="60">1 hour</SelectItem>
                      <SelectItem value="90">1.5 hours</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="bg-accent rounded-lg p-4 space-y-1.5 text-sm">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Base price (est.)</span>
                    <span>€{basePrice.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">IVA ({IVA_RATE}%)</span>
                    <span>€{(basePrice * IVA_RATE / 100).toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between border-t pt-1.5 font-medium">
                    <span>Estimated Total</span>
                    <span className="text-primary">€{(basePrice * (1 + IVA_RATE / 100)).toFixed(2)}</span>
                  </div>
                </div>

                <div className="flex gap-3">
                  <Button variant="outline" onClick={handleClose} className="flex-1">Cancel</Button>
                  <Button onClick={handleStep1Continue} className="flex-1 bg-primary hover:bg-primary/90">
                    Next: Race Setup
                  </Button>
                </div>
              </div>
            )}

            {step === 'race' && (
              <div className="space-y-5 py-2">
                <div className="space-y-2">
                  <Label>Track</Label>
                  {availableTracks.length === 0 ? (
                    <p className="text-sm text-muted-foreground">No tracks available right now.</p>
                  ) : (
                    <div className="space-y-2">
                      {availableTracks.map(track => (
                        <label
                          key={track.id}
                          className={`flex items-center justify-between border rounded-lg p-3 cursor-pointer transition-colors ${selectedTrackId === track.id ? 'border-primary bg-primary/5' : 'hover:bg-accent'}`}
                        >
                          <div className="flex items-center gap-3">
                            <input
                              type="radio"
                              name="track"
                              checked={selectedTrackId === track.id}
                              onChange={() => setSelectedTrackId(track.id)}
                              className="accent-primary"
                            />
                            <div>
                              <p className="font-medium text-sm">{track.name}</p>
                              <p className="text-xs text-muted-foreground">{track.lengthKm} km · max {track.kartLimit} karts</p>
                            </div>
                          </div>
                          <span className="text-sm text-primary">€{track.pricePerHour}/hr</span>
                        </label>
                      ))}
                    </div>
                  )}
                </div>

                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <Label>Karts</Label>
                    {selectedKartIds.length > 0 && (
                      <Badge variant="secondary">{selectedKartIds.length} selected</Badge>
                    )}
                  </div>
                  {availableKarts.length === 0 ? (
                    <p className="text-sm text-muted-foreground">No karts available right now.</p>
                  ) : (
                    <div className="grid grid-cols-2 gap-2">
                      {karts.map(kart => {
                        const available = kart.status === 'available';
                        const selected = selectedKartIds.includes(kart.id);
                        return (
                          <button
                            key={kart.id}
                            type="button"
                            disabled={!available}
                            onClick={() => available && toggleKart(kart.id)}
                            className={`flex flex-col items-start gap-0.5 border rounded-lg p-3 text-left transition-colors
                              ${!available ? 'opacity-40 cursor-not-allowed bg-muted' : selected ? 'border-primary bg-primary/5' : 'hover:bg-accent cursor-pointer'}
                            `}
                          >
                            <div className="flex items-center justify-between w-full">
                              <span className="text-sm font-medium">{kart.name}</span>
                              {selected && <CheckCircle2 className="w-4 h-4 text-primary" />}
                            </div>
                            <span className="text-xs text-muted-foreground">{kart.model}</span>
                            {kart.type && (
                              <span className="text-xs text-muted-foreground">€{kart.type.pricePerHour}/hr</span>
                            )}
                            {!available && <span className="text-xs text-red-500 capitalize">{kart.status}</span>}
                          </button>
                        );
                      })}
                    </div>
                  )}
                </div>

                {availableEquipment.length > 0 && (
                  <div className="space-y-2">
                    <Label>Equipment <span className="text-muted-foreground text-xs ml-1">(optional)</span></Label>
                    <div className="space-y-2">
                      {availableEquipment.map(eq => {
                        const qty = getEquipmentQty(eq.id);
                        return (
                          <div key={eq.id} className="flex items-center justify-between border rounded-lg px-3 py-2.5">
                            <div>
                              <p className="text-sm font-medium">{eq.name}</p>
                              <p className="text-xs text-muted-foreground">€{eq.pricePerUnit.toFixed(2)} / unit</p>
                            </div>
                            <div className="flex items-center gap-2">
                              <button
                                type="button"
                                onClick={() => setEquipmentQty(eq.id, qty - 1)}
                                disabled={qty === 0}
                                className="w-7 h-7 rounded-full border flex items-center justify-center hover:bg-accent disabled:opacity-30"
                              >
                                <Minus className="w-3 h-3" />
                              </button>
                              <span className="w-5 text-center text-sm">{qty}</span>
                              <button
                                type="button"
                                onClick={() => setEquipmentQty(eq.id, qty + 1)}
                                disabled={qty >= eq.stockQuantity}
                                className="w-7 h-7 rounded-full border flex items-center justify-center hover:bg-accent disabled:opacity-30"
                              >
                                <Plus className="w-3 h-3" />
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}

                <div className="bg-accent rounded-lg p-4 space-y-1.5 text-sm">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Rental base</span>
                    <span>€{basePrice.toFixed(2)}</span>
                  </div>
                  {equipmentTotal > 0 && (
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Equipment</span>
                      <span>€{equipmentTotal.toFixed(2)}</span>
                    </div>
                  )}
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">IVA ({IVA_RATE}%)</span>
                    <span>€{ivaAmount.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between border-t pt-1.5 font-medium">
                    <span>Total</span>
                    <span className="text-primary">€{total.toFixed(2)}</span>
                  </div>
                </div>

                <div className="flex gap-3">
                  <Button variant="outline" onClick={() => setStep('rental')} className="flex-1">Back</Button>
                  <Button onClick={handleStep2Continue} className="flex-1 bg-primary hover:bg-primary/90">
                    Next: Payment
                  </Button>
                </div>
              </div>
            )}

            {step === 'payment' && (
              <div className="space-y-5 py-2">
                <div className="border rounded-lg p-4 space-y-2 text-sm bg-muted/30">
                  <p className="font-medium mb-2">Booking Summary</p>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Type</span>
                    <span className="capitalize">{rentalType === 'kart' ? 'Kart (Shared)' : 'Track (Exclusive)'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Date</span>
                    <span>{selectedDate} at {selectedTime}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Duration</span>
                    <span>{duration} min</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Track</span>
                    <span>{selectedTrack?.name ?? '—'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Karts</span>
                    <span>{selectedKartIds.length} selected</span>
                  </div>
                  {equipmentSelections.length > 0 && (
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Equipment items</span>
                      <span>{equipmentSelections.reduce((s, e) => s + e.quantity, 0)} pcs</span>
                    </div>
                  )}
                  <div className="border-t pt-2 flex justify-between font-medium">
                    <span>Total (incl. IVA)</span>
                    <span className="text-primary">€{total.toFixed(2)}</span>
                  </div>
                </div>

                <div className="space-y-3">
                  <Label>Payment Method</Label>
                  <RadioGroup value={paymentMethod} onValueChange={(v: 'credit_card' | 'debit_card' | 'cash') => setPaymentMethod(v)}>
                    {[
                      { value: 'credit_card' as const, label: 'Credit Card' },
                      { value: 'debit_card' as const, label: 'Debit Card' },
                      { value: 'cash' as const, label: 'Cash (Pay at Venue)' },
                    ].map(opt => (
                      <div key={opt.value} className="flex items-center gap-2 border rounded-lg p-3 hover:bg-accent cursor-pointer">
                        <RadioGroupItem value={opt.value} id={opt.value} />
                        <Label htmlFor={opt.value} className="flex-1 cursor-pointer">{opt.label}</Label>
                      </div>
                    ))}
                  </RadioGroup>
                </div>

                {paymentMethod !== 'cash' && (
                  <div className="space-y-3">
                    <div className="space-y-2">
                      <Label htmlFor="cardNumber">Card Number</Label>
                      <Input
                        id="cardNumber"
                        placeholder="1234 5678 9012 3456"
                        maxLength={19}
                        value={cardNumber}
                        onChange={(e) => setCardNumber(e.target.value)}
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      <div className="space-y-2">
                        <Label htmlFor="expiry">Expiry</Label>
                        <Input id="expiry" placeholder="MM/YY" maxLength={5} value={cardExpiry} onChange={e => setCardExpiry(e.target.value)} />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="cvv">CVV</Label>
                        <Input id="cvv" placeholder="123" maxLength={3} value={cardCvv} onChange={e => setCardCvv(e.target.value)} />
                      </div>
                    </div>
                  </div>
                )}

                <div className="flex gap-3">
                  <Button variant="outline" onClick={() => setStep('race')} className="flex-1" disabled={isProcessing}>Back</Button>
                  <Button
                    onClick={handlePaymentSubmit}
                    className="flex-1 bg-primary hover:bg-primary/90"
                    disabled={isProcessing}
                  >
                    {isProcessing ? 'Processing…' : paymentMethod === 'cash' ? 'Confirm Booking' : 'Pay Now'}
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
