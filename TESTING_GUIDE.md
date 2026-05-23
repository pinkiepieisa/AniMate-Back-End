# 🔐 Guia de Testes - Rotas Seguras com JWT

## 📝 Resumo das Mudanças

Esta branch implementa autenticação JWT segura para o backend AniMate.

### Mudanças Principais:
- ✅ **Novo Filtro JWT** (`JwtAuthenticationFilter.java`) - Valida tokens no header Authorization
- ✅ **SecurityConfig Atualizado** - Define rotas públicas e protegidas
- ✅ **AuthController Atualizado** - Usa `Authorization: Bearer <token>`
- ✅ **ProfileController Atualizado** - Endpoints protegidos com token

---

## 🧪 Testando as Rotas

### **1. Registrar Novo Usuário** (Público)

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@email.com",
    "password": "Senha123!"
  }'
```

**Resposta esperada:** `200 OK`
```
Cadastrado com sucesso!
```

---

### **2. Fazer Login** (Público)

```bash
curl -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@email.com",
    "password": "Senha123!",
    "rememberMe": true
  }'
```

**Resposta esperada:** `200 OK`
```json
{
  "token": "seu_jwt_token_aqui",
  "expirationTime": "2026-05-24T10:30:00",
  "userId": 1,
  "username": "testuser",
  "email": "test@email.com"
}
```

**⚠️ IMPORTANTE:** Copie o valor de `token` para usar nos próximos testes!

---

### **3. Testar Acesso sem Token** (Deve Falhar)

```bash
curl -X GET http://localhost:8080/profile/me
```

**Resposta esperada:** `401 Unauthorized`
```
Token não fornecido
```

---

### **4. Testar com Token Inválido** (Deve Falhar)

```bash
curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer token_invalido"
```

**Resposta esperada:** `401 Unauthorized`

---

### **5. Buscar Perfil** (Protegido)

```bash
# Substitua SEU_TOKEN_AQUI pelo token obtido no login
TOKEN="SEU_TOKEN_AQUI"

curl -X GET http://localhost:8080/profile/me \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:** `200 OK`
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@email.com",
  "bio": null,
  "profilePicture": null
}
```

---

### **6. Atualizar Bio** (Protegido)

```bash
TOKEN="SEU_TOKEN_AQUI"

curl -X PUT http://localhost:8080/profile/bio \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"bio": "Desenvolvedor apaixonado por anime!"}'
```

**Resposta esperada:** `200 OK`
```
Bio atualizada com sucesso.
```

---

### **7. Atualizar Username** (Protegido)

```bash
TOKEN="SEU_TOKEN_AQUI"

curl -X PUT http://localhost:8080/profile/username \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username": "novonome123"}'
```

**Resposta esperada:** `200 OK`
```
Username atualizado com sucesso.
```

---

### **8. Validar Token** (Protegido)

```bash
TOKEN="SEU_TOKEN_AQUI"

curl -X POST http://localhost:8080/auth/check \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:** `200 OK` (corpo vazio)

---

### **9. Fazer Logout** (Protegido)

```bash
TOKEN="SEU_TOKEN_AQUI"

curl -X POST http://localhost:8080/auth/signout \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:** `200 OK` (corpo vazio)

---

## 🌐 Testando com Postman

### Passo 1: Criar Requisição de Login
1. Abra **Postman**
2. Crie uma nova requisição **POST**
3. URL: `http://localhost:8080/auth/signin`
4. **Body** (raw, JSON):
```json
{
  "email": "test@email.com",
  "password": "Senha123!",
  "rememberMe": true
}
```
5. Clique **Send**

### Passo 2: Copiar o Token
1. Na resposta, copie o valor de `token`
2. Salve em um local seguro (ou use a variável do Postman)

### Passo 3: Adicionar Autorização
1. Crie uma nova requisição **GET**
2. URL: `http://localhost:8080/profile/me`
3. Vá para aba **Headers**
4. Adicione:
   - **Key:** `Authorization`
   - **Value:** `Bearer seu_token_aqui`
5. Clique **Send**

### Alternativa: Usar Postman Auth
1. Na requisição, vá para aba **Authorization**
2. Selecione tipo **Bearer Token**
3. Cole seu token no campo **Token**
4. Postman adicionará automaticamente o header

---

## ✅ Checklist de Testes de Segurança

- [ ] Login retorna token válido
- [ ] Acesso sem token retorna 401
- [ ] Acesso com token inválido retorna 401
- [ ] Acesso com token válido retorna 200
- [ ] Header `Authorization: Bearer <token>` é obrigatório
- [ ] Token em query param não funciona mais (removido)
- [ ] Endpoints públicos funcionam sem token
- [ ] Endpoints protegidos requerem token

---

## 📋 Rotas Públicas (sem autenticação)
```
POST   /auth/signup                  - Registrar usuário
POST   /auth/signin                  - Fazer login
POST   /auth/forgot-password         - Solicitar recuperação de senha
POST   /auth/reset-password          - Redefinir senha
GET    /swagger-ui/**                - Documentação
GET    /v3/api-docs/**               - OpenAPI docs
```

## 🔒 Rotas Protegidas (requerem Bearer token)
```
POST   /auth/check                   - Validar token
POST   /auth/signout                 - Logout
GET    /profile/me                   - Buscar perfil
PUT    /profile/bio                  - Atualizar bio
PUT    /profile/username             - Atualizar username
PUT    /profile/picture              - Atualizar foto de perfil
```

---

## 🐛 Solução de Problemas

### "Token não fornecido"
- Verifique se está usando `Authorization: Bearer <token>`
- Certifique-se de copiar o token inteiro (sem espaços extras)

### "Token inválido"
- Verifique se o token não expirou
- Faça login novamente para obter um novo token

### "401 Unauthorized"
- Confirme que está usando o header correto: `Authorization: Bearer `
- Não use query params como antes (`?token=...`)

---

## 📚 Próximos Passos

1. Merge desta branch para `main`
2. Deploy em ambiente de staging
3. Testes de integração com frontend
4. Deploy em produção

---

**Criado por:** leandrolpz  
**Data:** 2026-05-23  
**Branch:** rotas-seguras
