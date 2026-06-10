import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10,          // 10 usuários virtuais
    duration: '30s',  // teste durante 30 segundos
};

export default function () {

    const payload = JSON.stringify({
        email: 'leandro2@test.com',
        password: '123456',
        rememberMe: false
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(
        'http://localhost:8081/auth/signin',
        payload,
        params
    );

    check(response, {
        'status é 200': (r) => r.status === 200,
    });
}