// scripts/main.js

document.addEventListener('DOMContentLoaded', function() {
    // Example: Store a token (in real app, you'd get this from login)
    localStorage.setItem('authToken', 'example-token-123');

    document.getElementById('fetchData').addEventListener('click', async function() {
        try {
            const response = await fetch('https://jsonplaceholder.typicode.com/todos/1');
            const data = await response.json();
            document.getElementById('result').textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            console.error('Fetch error:', error);
            document.getElementById('result').textContent = 'Error fetching data';
        }
    });

});