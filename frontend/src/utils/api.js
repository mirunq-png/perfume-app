const BASE_URL = '';

async function request(path, options = {}, skipRedirect=false) {
    const response = await fetch(`${BASE_URL}${path}`, {
        ...options,
        credentials: 'include', // sends httpOnly cookie automatically
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    });

    if (response.status === 401 && !skipRedirect) {
        window.location.href = '/login';
        return;
    }

    return response;
}

export const api = {
    get:    (path)         => request(path, { method: 'GET' }),
    post:   (path, body)   => request(path, { method: 'POST',   body: JSON.stringify(body) }),
    put:    (path, body)   => request(path, { method: 'PUT',    body: JSON.stringify(body) }),
    delete: (path)         => request(path, { method: 'DELETE' }),
    getNoRedirect: (path) => request(path, { method: 'GET' }, true),
};