# MediQ API Contract

## Base URL
- Local: `http://localhost:8099`
- Production: `https://api.mediq.app`

## Authentication
All authenticated endpoints require `Authorization: Bearer <token>` header.

### POST /auth/sign-in
Request: `{ username: string, password: string }`
Response: `{ accessToken: string, expiresAt: string, refreshToken: string, refreshExpiresAt: string, profile: UserProfile }`

### POST /auth/register
Request: `{ username, password, fullName, email?, mobileNumber, dateOfBirth, role }`
Response: AuthSession

### POST /auth/request-otp
Request: `{ contact: string, purpose: "register" | "reset_password" }`
Response: `{}`

### POST /auth/verify-otp
Request: `{ contact, code, purpose }`
Response: AuthSession

### POST /auth/refresh
Request: `{}` (token in Authorization header)
Response: AuthSession

## User Profile

### GET /profile
Response: UserProfile

### PATCH /profile
Request: `{ name?, email?, mobileNumber? }`
Response: UserProfile

## Doctors

### GET /doctors
Query: `?specializationId=&search=&status=&limit=&offset=`
Response: Paged<Doctor>

### GET /doctors/{id}
Response: Doctor

### GET /doctors/{id}/schedule
Response: ClinicHours[]

### POST /doctors/{id}/schedule
Request: ClinicHours[]
Response: 200 OK

### GET /doctors/{id}/availability
Query: `?date=YYYY-MM-DD`
Response: AvailableDate[]

### POST /doctors/{id}/availability
Request: AvailableDate
Response: 200 OK

### GET /doctors/{id}/appointments
Query: `?from=&to=`
Response: Appointment[]

### PATCH /doctors/appointments/{appointmentId}/outcome
Request: VisitOutcome ("completed" | "no_show")
Response: Appointment

## Specializations

### GET /specializations
Response: Specialty[]

### POST /specializations
Request: Specialty
Response: Specialty (admin only)

### PUT /specializations/{id}
Request: Specialty
Response: Specialty (admin only)

### DELETE /specializations/{id}
Response: 200 OK (admin only)

## Appointments

### GET /appointments
Query: `?status=&from=&to=&limit=&offset=`
Response: Paged<Appointment>

### POST /appointments
Request: BookingRequest
Response: Appointment (patient only)

### GET /appointments/{id}
Response: Appointment

## Messages

### GET /messages/conversations
Response: MessageThread[]

### GET /messages/conversations/{id}
Response: MessageThread

### GET /messages/conversations/{id}/messages
Response: Message[]

### POST /messages/conversations/{id}/messages
Request: string (body)
Response: Message

### PATCH /messages/conversations/{id}/read
Response: 200 OK

## Notifications

### GET /notifications
Query: `?unread=true`
Response: Notification[]

### PATCH /notifications/{id}/read
Response: 200 OK

### POST /notifications/read-all
Response: 200 OK

## Admin

### GET /admin/accounts
Response: AdminAccountsResponse

### POST /admin/accounts?kind=doctor|secretary
Request: StaffDraft
Response: UserProfile

### PATCH /admin/accounts/{id}/status
Request: AccountStatus
Response: UserProfile

### GET /admin/doctors
Response: Doctor[]

## Secretary

### GET /secretary/appointments
Response: Appointment[]

### POST /secretary/appointments/{id}/confirm
Response: 200 OK

### POST /secretary/appointments/{id}/cancel
Response: 200 OK

### GET /secretary/patients
Response: UserProfile[]

### GET /secretary/messages
Response: MessageThread[]

## Health

### GET /health
Response: { status: "ok" }