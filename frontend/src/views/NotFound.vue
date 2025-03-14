<template>
  <div class="not-found-page flex flex-col items-center justify-center py-16 px-4 text-center">
    <div class="max-w-md">
      <h1 class="text-9xl font-bold text-primary-600 dark:text-primary-400">404</h1>
      <h2 class="text-3xl font-semibold mb-4 text-gray-900 dark:text-white">Page Not Found</h2>
      <p class="text-lg mb-8 text-gray-600 dark:text-gray-300">
        The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
      </p>
      <router-link to="/" class="btn btn-primary px-6 py-3">
        Return to Home
      </router-link>
    </div>
  </div>
</template> 