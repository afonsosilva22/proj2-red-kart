import { useCallback, useEffect, useState } from 'react';
import { Button } from './components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './components/ui/tabs';
import { Plus, Car, Loader2 } from 'lucide-react';
import { RentalCard } from './components/RentalCard';
import { NewRentalDialog } from './components/NewRentalDialog';
import { Rental, Customer, Race } from './types';
import { Toaster, toast } from 'sonner';
import redKartLogo from '../imports/red-kart-logo.png';
import { customerApi, raceApi, rentalApi } from './services/api';
import {
  COMPLETED_STATUSES,
  DEFAULT_CUSTOMER_ID,
  UPCOMING_STATUSES,
} from './config';

function App() {
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [races, setRaces] = useState<Race[]>([]);
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const loadData = useCallback(async (showLoading = true, showErrors = true) => {
    try {
      if (showLoading) setIsLoading(true);
      const [allRentals, allRaces, currentCustomer] = await Promise.all([
        rentalApi.getAll(),
        raceApi.getAll(),
        customerApi.getById(DEFAULT_CUSTOMER_ID),
      ]);

      const customerRentals = allRentals.filter(
        r => r.customer?.id === DEFAULT_CUSTOMER_ID
      );
      setRentals(customerRentals);
      setRaces(allRaces);
      setCustomer(currentCustomer);
    } catch (error) {
      console.error('Failed to load data from backend:', error);
      if (showErrors) {
        toast.error('Failed to load rentals', {
          description: 'Make sure the backend is running at http://localhost:8080',
        });
      }
    } finally {
      if (showLoading) setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();

    const refreshInterval = window.setInterval(() => {
      loadData(false, false);
    }, 10000);

    return () => window.clearInterval(refreshInterval);
  }, [loadData]);

  const handleRentalCreated = (newRental: Rental) => {
    setRentals(prev => [newRental, ...prev]);
    toast.success('Rental booked successfully!', {
      description: 'Your kart rental has been confirmed.',
    });
  };

  const upcomingRentals = rentals.filter(r =>
    UPCOMING_STATUSES.includes(r.status as (typeof UPCOMING_STATUSES)[number])
  );
  const activeRentals = rentals.filter(rental =>
    races.some(race =>
      race.status?.toLowerCase() === 'ongoing'
      && race.rental?.id === rental.id
    )
  );
  const completedRentals = rentals.filter(r =>
    COMPLETED_STATUSES.includes(r.status as (typeof COMPLETED_STATUSES)[number])
  );

  return (
    <div className="min-h-screen bg-background">
      <header className="bg-white border-b sticky top-0 z-10 shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <img
                src={redKartLogo}
                alt="Red Kart logo"
                className="h-20 w-auto object-contain"
              />
            </div>
            <div className="flex items-center gap-4">
              {customer && (
                <div className="text-right hidden sm:block">
                  <p className="text-sm">{customer.name}</p>
                  <p className="text-xs text-muted-foreground">{customer.email}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        <div className="max-w-5xl mx-auto">
          <div className="flex items-center justify-between mb-8">
            <div>
              <h2>My Rentals</h2>
              <p className="text-muted-foreground mt-1">
                Manage your kart rentals and bookings
              </p>
            </div>
            <Button
              onClick={() => setIsDialogOpen(true)}
              className="bg-primary hover:bg-primary/90 gap-2"
              disabled={isLoading || !customer}
            >
              <Plus className="w-4 h-4" />
              New Rental
            </Button>
          </div>

          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-24 text-muted-foreground gap-3">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
              <p>Loading rentals from server…</p>
            </div>
          ) : (
            <Tabs defaultValue="upcoming" className="space-y-6">
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="upcoming">
                  Upcoming ({upcomingRentals.length})
                </TabsTrigger>
                <TabsTrigger value="active">
                  Active ({activeRentals.length})
                </TabsTrigger>
                <TabsTrigger value="completed">
                  Completed ({completedRentals.length})
                </TabsTrigger>
              </TabsList>

              <TabsContent value="upcoming" className="space-y-4">
                {upcomingRentals.length > 0 ? (
                  upcomingRentals.map(rental => (
                    <RentalCard key={rental.id} rental={rental} />
                  ))
                ) : (
                  <div className="text-center py-12 bg-accent/50 rounded-lg">
                    <Car className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                    <p className="text-muted-foreground">No upcoming rentals</p>
                    <Button
                      variant="outline"
                      onClick={() => setIsDialogOpen(true)}
                      className="mt-4"
                    >
                      Book Your First Rental
                    </Button>
                  </div>
                )}
              </TabsContent>

              <TabsContent value="active" className="space-y-4">
                {activeRentals.length > 0 ? (
                  activeRentals.map(rental => (
                    <RentalCard key={rental.id} rental={rental} />
                  ))
                ) : (
                  <div className="text-center py-12 bg-accent/50 rounded-lg">
                    <Car className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                    <p className="text-muted-foreground">No active rentals</p>
                  </div>
                )}
              </TabsContent>

              <TabsContent value="completed" className="space-y-4">
                {completedRentals.length > 0 ? (
                  completedRentals.map(rental => (
                    <RentalCard key={rental.id} rental={rental} />
                  ))
                ) : (
                  <div className="text-center py-12 bg-accent/50 rounded-lg">
                    <Car className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                    <p className="text-muted-foreground">No completed rentals</p>
                  </div>
                )}
              </TabsContent>
            </Tabs>
          )}
        </div>
      </main>

      <footer className="border-t mt-16 py-8 bg-white">
        <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
          <p>© 2026 Red Kart Karting Center. All rights reserved.</p>
        </div>
      </footer>

      {customer && (
        <NewRentalDialog
          open={isDialogOpen}
          onOpenChange={setIsDialogOpen}
          onRentalCreated={handleRentalCreated}
          customerId={customer.id}
        />
      )}

      <Toaster position="top-right" richColors />
    </div>
  );
}

export default App;
