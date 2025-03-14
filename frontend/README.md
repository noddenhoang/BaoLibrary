# Library Management System - Frontend

A modern, responsive frontend for a Library Management System built with Vue.js, Vuetify, and Tailwind CSS.

## Project Overview

This project is a frontend application for a Library Management System that allows users to browse books, borrow and return them, and manage their account. The application is built with Vue.js and follows a modular, component-based architecture.

## Tech Stack

- **Frontend Framework**: Vue.js 3
- **UI Library**: Vuetify 3
- **State Management**: Vuex 4
- **HTTP Client**: Axios
- **CSS Framework**: Tailwind CSS
- **Build Tool**: Vite

## Incremental Development Approach

This project is being developed in discrete, testable increments:

### Increment 1: Project Setup and Core Layout (Current)

- Project initialization with Vue CLI
- Basic folder structure following Vue best practices
- Core layout components (navigation bar, sidebar, footer)
- Router setup with placeholder routes
- Vuex store initialization
- API service foundation
- Authentication service skeleton
- Base styling and theme setup

## Project Structure

```
frontend/
├── public/              # Static assets
├── src/
│   ├── assets/          # CSS, images, and other assets
│   ├── components/      # Reusable Vue components
│   │   ├── common/      # Common UI components
│   │   └── layout/      # Layout components
│   ├── router/          # Vue Router configuration
│   ├── services/        # API and other services
│   ├── store/           # Vuex store modules
│   │   └── modules/     # Store modules for different features
│   ├── views/           # Page components
│   ├── App.vue          # Root component
│   └── main.js          # Application entry point
├── .gitignore           # Git ignore file
├── index.html           # HTML entry point
├── package.json         # Project dependencies
├── tailwind.config.js   # Tailwind CSS configuration
└── vite.config.js       # Vite configuration
```

## Getting Started

### Prerequisites

- Node.js (v14 or later)
- npm or yarn

### Installation

1. Clone the repository
2. Navigate to the frontend directory
3. Install dependencies:

```bash
npm install
# or
yarn install
```

### Running the Development Server

```bash
npm run dev
# or
yarn dev
```

The application will be available at http://localhost:5173 (or another port if 5173 is in use).

## Testing Increment 1

### Features Implemented

1. **Project Structure**: Basic folder structure with Vue best practices
2. **Core Layout**: Navigation bar, sidebar, and footer components
3. **Router Setup**: Basic routes configured for all planned pages
4. **Store Setup**: Vuex store with modules for authentication, books, and users
5. **API Service**: Foundation for API calls with Axios
6. **Theme Setup**: Tailwind CSS with custom theme colors and dark mode support

### Testing Instructions

1. **Run the development server**:
   ```bash
   npm run dev
   ```

2. **Test the responsive layout**:
   - Resize the browser window to test mobile, tablet, and desktop layouts
   - The navigation bar should collapse to a hamburger menu on mobile
   - The sidebar should be hidden on mobile and visible on desktop

3. **Test dark mode**:
   - Click the dark mode toggle button in the navigation bar
   - The application should switch between light and dark themes

4. **Test navigation**:
   - Click on the navigation links to navigate between pages
   - The 404 page should be displayed for non-existent routes

5. **Test placeholder pages**:
   - The home page should display a welcome message and feature sections
   - Other pages will be implemented in future increments

## Next Steps

The next increment (Increment 2) will focus on implementing the authentication and user management features:

- Login page with form validation
- Registration page with all required fields
- User profile page (view only initially)
- Password recovery flow
- Auth guards for protected routes

## License

This project is licensed under the MIT License. 