const BASE_URL = import.meta.env.VITE_API_URL ?? '';

let accessToken = null; // in memory- lost on page reload

export function setAccessToken(token) {
    accessToken = token;
}

async function request(path, options = {}, isRetry = false) {
    const response = await fetch(`${BASE_URL}${path}`, {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
            ...options.headers,
        },
    });

    if (response.status === 401 && !isRetry && path !== '/api/auth/refresh') {
        const refreshed = await tryRefresh();
        if (refreshed) return request(path, options, true);
        window.location.href = '/login';
        return;
    }

    return response;
}

export async function tryRefresh() {
    const res = await fetch(`${BASE_URL}/api/auth/refresh`, {
        method: 'POST',
        credentials: 'include',
    });
    if (!res.ok) return null;
    const data = await res.json(); // { accessToken, username }
    accessToken = data.accessToken;
    return data.username;
}

export const api = {
    get:    (path)       => request(path, { method: 'GET' }),
    post:   (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
    put:    (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
    delete: (path)       => request(path, { method: 'DELETE' }),
};