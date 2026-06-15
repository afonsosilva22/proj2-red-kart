# Red Kart - Backend Integration Guide

This document explains how to connect the Red Kart frontend to your Spring Boot backend.

## Overview

The frontend is structured to match your Spring Boot backend API endpoints and data models. All API calls are currently using mock data, but can be easily switched to real API calls.

## API Service Structure

The API service is located at `/src/app/services/api.ts` and includes:

### Rental Endpoints
- `GET /api/rentals/get/all` - Get all rentals
- `GET /api/rentals/get/{id}` - Get rental by ID
- `POST /api/rentals/create` - Create new rental
- `PUT /api/rentals/update/{id}` - Update rental

### Payment Endpoints
- `GET /api/payments/get/all` - Get all payments
- `GET /api/payments/get/{id}` - Get payment by ID
- `POST /api/payments/create` - Create new payment

## Environment Configuration

### 1. Set Backend URL

Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:8080
```

Or update the default in `/src/app/services/api.ts`:

```typescript
const API_BASE_URL = 'http://your-backend-url:8080';
```

### 2. Enable CORS in Spring Boot

Your backend already has `@CrossOrigin(origins = "*")` configured, which should work. For production, update to specific origins:

```java
@CrossOrigin(origins = "https://your-frontend-domain.com")
```

## Switching from Mock to Real Data

### Current Implementation (Mock Data)

The app currently uses mock data from `/src/app/data/mockData.ts`.

### To Use Real API

Update `/src/app/App.tsx` to fetch data from the API:

```typescript
import { rentalApi } from './services/api';

function App() {
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Fetch rentals from backend
    const fetchRentals = async () => {
      try {
        setIsLoading(true);
        const data = await rentalApi.getAll();
        // Filter by current customer in real implementation
        setRentals(data);
      } catch (error) {
        console.error('Failed to fetch rentals:', error);
        toast.error('Failed to load rentals');
      } finally {
        setIsLoading(false);
      }
    };

    fetchRentals();
  }, []);

  // ... rest of component
}
```

### Update NewRentalDialog Component

In `/src/app/components/NewRentalDialog.tsx`, replace the mock API call:

```typescript
const handlePaymentSubmit = async () => {
  // ... validation ...

  setIsProcessing(true);

  try {
    // Create rental
    const rentalRequest: CreateRentalRequest = {
      plannedStartDatetime: startDateTime.toISOString(),
      plannedEndDatetime: endDateTime.toISOString(),
      basePrice: basePrice,
      discount: 0,
      type: rentalType,
      status: 'scheduled',
      customer: { id: mockCustomer.id }, // Use real customer ID from auth
      employee: { id: 1 } // Set appropriate employee ID
    };

    const createdRental = await rentalApi.create(rentalRequest);

    // Create payment
    const paymentRequest: CreatePaymentRequest = {
      paymentDate: new Date().toISOString(),
      amountPaid: totalPrice,
      ivaRate: ivaRate,
      paymentMethod: paymentMethod,
      rental: { id: createdRental.id! }
    };

    await paymentApi.create(paymentRequest);

    onRentalCreated(createdRental);
    toast.success('Rental booked successfully!');
  } catch (error) {
    console.error('Failed to create rental:', error);
    toast.error('Failed to create booking. Please try again.');
  } finally {
    setIsProcessing(false);
  }
};
```

## Data Model Mapping

### Frontend → Backend

The TypeScript types in `/src/app/types/index.ts` match your Java models:

| Frontend (TypeScript) | Backend (Java) |
|----------------------|----------------|
| `Rental` | `com.example.backend.models.Rental` |
| `Payment` | `com.example.backend.models.Payment` |
| `Customer` | `com.example.backend.models.Customer` |
| `Employee` | `com.example.backend.models.Employee` |

### Date Handling

- Frontend sends: ISO 8601 strings (e.g., `"2026-06-20T14:00:00Z"`)
- Backend expects: `Instant` type
- Conversion is automatic with Spring Boot's Jackson

## Authentication

Currently, the app uses a mock customer (`mockCustomer`). To implement real authentication:

1. Add authentication context/provider
2. Store authenticated user info
3. Filter rentals by customer ID: `GET /api/rentals/get/all?customerId={id}`
4. Or create new endpoint: `GET /api/rentals/get/customer/{customerId}`

### Suggested Backend Enhancement

Add a customer-specific endpoint:

```java
@GetMapping("/get/customer/{customerId}")
public ResponseEntity<List<Rental>> getByCustomerId(@PathVariable Integer customerId) {
    List<Rental> rentals = service.getAll()
        .stream()
        .filter(r -> r.getCustomer().getId().equals(customerId))
        .collect(Collectors.toList());
    return ResponseEntity.ok(rentals);
}
```

## Testing the Integration

### 1. Start Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Update Frontend Environment
```bash
# In frontend directory
echo "REACT_APP_API_URL=http://localhost:8080" > .env
```

### 3. Test API Connection
Open browser console and check network requests when:
- Loading rentals page
- Creating new rental
- Making payment

## Error Handling

The API service throws errors that can be caught and displayed to users:

```typescript
try {
  const rentals = await rentalApi.getAll();
  setRentals(rentals);
} catch (error) {
  toast.error('Failed to load rentals. Please check your connection.');
}
```

## Production Deployment

1. Build frontend: `npm run build`
2. Serve static files from Spring Boot or separate server
3. Update CORS configuration for production domain
4. Use environment variables for API URL
5. Enable HTTPS for both frontend and backend

## Security Considerations

- Implement proper authentication (JWT, OAuth, etc.)
- Validate user permissions before showing/modifying rentals
- Sanitize all user inputs
- Use HTTPS in production
- Implement rate limiting on backend
- Don't expose sensitive data in API responses

## Notes

- The frontend expects numeric IDs (matching your backend's `Integer` type)
- All prices are handled as numbers (matching backend's `BigDecimal`)
- IVA rate is fixed at 23% in the frontend but comes from backend Payment model
- Payment method options: 'credit_card', 'debit_card', 'cash'
- Rental types: 'individual', 'group'
- Rental statuses: 'scheduled', 'active', 'completed', 'cancelled'
