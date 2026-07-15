# PropertyOS UI

A modern React-based administrative dashboard for **PropertyOS**, a multi-tenant Property Management SaaS platform. The project is built by customizing the free version of a Material UI dashboard template into a scalable property management system.

## Overview

PropertyOS is designed to manage residential and commercial properties from a single platform. The UI communicates with multiple backend microservices and provides a clean, responsive, and multilingual user experience.

## Technology Stack

* React
* TypeScript
* Material UI (MUI)
* React Router
* React Hook Form
* React i18next
* Axios
* Vite

## Current Features

### Authentication

* JWT-based authentication
* Access token and refresh token support
* Automatic token refresh
* Request timeout handling
* Centralized Axios configuration

### Workspace Management

A workspace represents a facility/property that the current user can access.

Features include:

* Workspace switcher
* Active workspace persistence using Local Storage
* Automatic restoration of the last selected workspace
* Active workspace highlighting
* Workspace synchronization across the application

## Facility Management

Current implementation includes:

* Facility listing
* Create Facility page
* Responsive facility creation form
* Facility type support
* Active facility selection

## Internationalization (i18n)

The application supports multiple languages using **react-i18next**.

Currently supported:

* English
* বাংলা (Bangla)

Implemented localization includes:

* Navigation
* Authentication pages
* Facility pages
* User roles
* Facility types
* Validation messages
* Error pages
* Common UI components

The application dynamically switches language without requiring a page refresh.

## Error Handling

Implemented global error handling includes:

* Server unavailable page
* Backend connection failure handling
* Axios request timeout support
* Friendly error messages
* Localized error pages

## Responsive Design

The UI is optimized for:

* Desktop
* Laptop
* Tablet
* Mobile devices

Responsive improvements include:

* Adaptive spacing
* Flexible layouts
* Mobile-friendly forms
* Responsive workspace selector

## Project Structure

```
src/
├── api/
│   ├── auth/
│   ├── property/
│   └── common/
├── components/
├── contexts/
├── hooks/
├── layouts/
├── locales/
│   ├── en/
│   └── bn/
├── pages/
├── routes/
├── sections/
│   ├── auth/
│   ├── dashboard/
│   ├── facility/
│   └── error/
├── theme/
├── types/
└── utils/
```

## Backend Integration

The frontend communicates with multiple Spring Boot microservices.

Current integrations include:

* Authentication Service
* Property Service

Communication is performed through REST APIs using Axios.

## UI Improvements

Recent enhancements include:

* Workspace context management
* Active workspace persistence
* Facility creation workflow
* Reusable server error page
* Improved responsive layouts
* Centralized Axios configuration
* Request timeout support
* Shared interceptor architecture
* English and Bangla localization
* Cleaner page titles and metadata
* General UI cleanup and consistency improvements

## Roadmap

Upcoming features include:

* Facility management (Edit/Delete)
* Building management
* Floor management
* Unit management
* Tenant management
* Lease management
* Visitor management
* Maintenance requests
* Staff management
* Role-based permissions
* Notifications
* Dashboard analytics
* Reports
* Payment management
* Document management
* File uploads
* Audit logs
* Dark mode improvements

## Development

Install dependencies:

```bash
npm install
```

Run the development server:

```bash
npm run dev
```

Build for production:

```bash
npm run build
```

## License

This project is built by customizing the free Material UI dashboard template and extends it into a complete Property Management SaaS platform for learning, development, and production use.
