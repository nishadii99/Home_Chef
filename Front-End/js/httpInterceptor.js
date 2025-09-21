// scripts/api-interceptor.js

// Store the original fetch function
const originalFetch = window.fetch;

// Override the fetch function
window.fetch = async function(...args) {
    // Request interceptor - runs before the request is sent
    console.log('Request interceptor - modifying request');

    // Add authorization token if available
    const token = localStorage.getItem('authToken');
    let headers = {};

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // Modify request options
    let options = args[1] || {};
    options.headers = {...options.headers, ...headers};

    // Call original fetch with modified options
    const response = await originalFetch(args[0], options);

    // Clone response to read it and still pass it to the original flow
    const clonedResponse = response.clone();

    // Response interceptor - runs after response is received
    console.log('Response interceptor - checking response');

    if (response.status === 401) {
        console.log('Unauthorized - redirecting to login');
        window.location.href = '/login.html';
        return response;
    }

    try {
        const data = await clonedResponse.json();
        console.log('Response data:', data);

        // You can modify the response data here if needed
        // For example, normalize data structure

    } catch (error) {
        console.log('Error parsing JSON:', error);
    }

    return response;
};

console.log('Fetch interceptor initialized');