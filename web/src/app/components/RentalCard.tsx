import { Card, CardContent } from './ui/card';
import { Badge } from './ui/badge';
import { Calendar, Clock, Users, DollarSign } from 'lucide-react';
import { Rental } from '../types';
import { format } from 'date-fns';
import { normalizeNumber } from '../utils/catalog';

interface RentalCardProps {
  rental: Rental;
}

export function RentalCard({ rental }: RentalCardProps) {
  const getStatusVariant = (status: string) => {
    switch (status) {
      case 'scheduled':
        return 'default';
      case 'active':
        return 'secondary';
      case 'completed':
      case 'finished':
        return 'outline';
      case 'cancelled':
        return 'destructive';
      case 'fully_payed':
        return 'secondary';
      default:
        return 'default';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'scheduled':
        return 'bg-blue-500';
      case 'active':
        return 'bg-green-500';
      case 'completed':
      case 'finished':
        return 'bg-gray-400';
      case 'cancelled':
        return 'bg-red-500';
      case 'fully_payed':
        return 'bg-emerald-500';
      default:
        return 'bg-gray-400';
    }
  };

  const plannedStart = new Date(rental.plannedStartDatetime);
  const plannedEnd = new Date(rental.plannedEndDatetime);
  const duration = Math.round((plannedEnd.getTime() - plannedStart.getTime()) / (1000 * 60));
  
  const basePrice = normalizeNumber(rental.basePrice);
  const discount = normalizeNumber(rental.discount);
  const finalPrice = basePrice - discount;
  const totalWithIva = finalPrice * 1.23; // Adding 23% IVA

  return (
    <Card className="hover:shadow-md transition-shadow border-l-4 border-l-primary">
      <CardContent className="p-6">
        <div className="flex items-start justify-between mb-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <h3>
                {rental.type === 'kart' ? 'Kart Rental' : rental.type === 'track' ? 'Track Rental' : rental.type}
              </h3>
              <Badge variant={getStatusVariant(rental.status)} className="capitalize">
                {rental.status}
              </Badge>
            </div>
            <p className="text-sm text-muted-foreground">Booking #{rental.id}</p>
          </div>
          <div className={`w-3 h-3 rounded-full ${getStatusColor(rental.status)}`} />
        </div>

        <div className="space-y-3">
          <div className="flex items-center gap-2 text-sm">
            <Calendar className="w-4 h-4 text-primary" />
            <span>{format(plannedStart, 'EEEE, MMMM d, yyyy')}</span>
          </div>

          <div className="flex items-center gap-2 text-sm">
            <Clock className="w-4 h-4 text-primary" />
            <span>
              {format(plannedStart, 'h:mm a')} - {format(plannedEnd, 'h:mm a')} ({duration} min)
            </span>
          </div>

          {rental.type === 'track' && (
            <div className="flex items-center gap-2 text-sm">
              <Users className="w-4 h-4 text-primary" />
              <span>Exclusive track booking</span>
            </div>
          )}

          <div className="flex items-center gap-2 text-sm pt-2 border-t">
            <DollarSign className="w-4 h-4 text-primary" />
            <div className="flex-1 flex justify-between items-center">
              <span>
                {discount > 0 ? (
                  <>
                    <span className="line-through text-muted-foreground mr-2">
                      €{basePrice.toFixed(2)}
                    </span>
                    <span>€{finalPrice.toFixed(2)}</span>
                  </>
                ) : (
                  <span>€{basePrice.toFixed(2)}</span>
                )}
              </span>
              <span className="text-primary">
                Total: €{totalWithIva.toFixed(2)}
              </span>
            </div>
          </div>

          {rental.actualStartDatetime && rental.actualEndDatetime && (
            <div className="pt-2 border-t">
              <p className="text-xs text-muted-foreground">
                Actual: {format(new Date(rental.actualStartDatetime), 'h:mm a')} - {format(new Date(rental.actualEndDatetime), 'h:mm a')}
              </p>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
