# Desafio – Painel de Investimentos com Perfil de Risco Dinâmico

Este projeto consiste em uma API RESTful que analisa o comportamento financeiro do cliente e ajusta
automaticamente seu perfil de risco, sugerindo produtos de investimento como CDBs,
LCIs, LCAs, Tesouro Direto, Fundos, etc.

## Tecnologias e Arquitetura

O projeto utiliza a seguinte stack tecnológica:

* **Linguagem:** Java 21 (LTS)
* **Framework:** Quarkus 
* **Banco de Dados:** SQLite 
* **Containerização:** Docker & Docker Compose
* **Segurança:** JWT com chaves RSA e BCrypt para hash de senhas
* **Testes:** JUnit 5, RestAssured e Mockito
* **Cobertura de Código:** JaCoCo (> 80% de cobertura)
* **Observabilidade:** Micrometer (Métricas customizadas de telemetria)
* **Documentação:** OpenAPI / Swagger UI

---

## Como Executar a Aplicação

A forma recomendada para iniciar o projeto é através do Docker Compose, que orquestra a aplicação e configura o ambiente automaticamente.

### Opção 1: Via Docker (Recomendado)

Certifique-se de ter o Docker e o Docker Compose instalados. Na raiz do projeto, execute:

```bash
docker compose up --build
```
- A API estará disponível em: http://localhost:8080
- O banco de dados será criado automaticamente com dados iniciais.


### Opção 2: Execução Local (Modo Desenvolvimento)
Caso prefira rodar diretamente na JVM local (necessário JDK 21):
```bash

# Linux / Mac
./mvnw clean quarkus:dev

# Windows
mvnw.cmd clean quarkus:dev
```
---

## Documentação da API (Swagger)
A documentação interativa de todos os endpoints está disponível via Swagger UI. Através dela, é possível visualizar os DTOs e testar as requisições em tempo real.

Acesse: http://localhost:8080/q/swagger-ui

---

## Testes e Cobertura de Código
O projeto possui testes unitários (lógica de negócio) e testes de integração (endpoints e segurança).

### Executar os Testes

Para rodar a bateria completa de testes:

```bash
./mvnw test
```

### Relatório de Cobertura (> 80%)

A aplicação utiliza o JaCoCo para garantir a qualidade do código. A cobertura atual supera os 80%, validando tanto os caminhos felizes quanto o tratamento de exceções.

Após a execução dos testes, o relatório detalhado pode ser consultado em:

```target/jacoco-report/index.html```

---

## Segurança e Controle de Acesso
A segurança foi implementada conforme a especificação, utilizando autenticação via Token JWT assinado.

Perfis de Acesso (RBAC)
- USER: Acesso limitado aos seus próprios dados de simulação e perfil.
- ADMIN: Acesso total, incluindo relatórios gerenciais e dados de telemetria.

### Credenciais para Teste (Pré-carregadas)

O sistema inicia com os seguintes utilizadores para facilitar a avaliação:

| Usuário   | Senha    | Papel | Permissões                                                        |
|-----------|----------|-------|--------------------------------------------------------------------|
| user123   | user123  | user  | Simular, Ver Perfil Próprio, Listar Minhas Simulações              |
| admin123  | admin123 | admin | Ver Telemetria, Listar Todas Simulações, Relatórios                |

---

## Funcionalidades Principais

- Autenticação (```/auth/login```): Geração de token JWT seguro.

- Cálculo de Perfil (```/perfil-risco/{id}```): Algoritmo que classifica o cliente em Conservador, Moderado ou Agressivo baseado no histórico financeiro.

- Simulação de Investimentos (```/simular-investimento```): Projeção de rentabilidade futura com persistência automática e cálculo de juros compostos.

- Motor de Recomendação (```/produtos-recomendados/{perfil}```): Sugere produtos (CDB, FIIs, Ações) adequados ao perfil de risco.

- Telemetria (```/telemetria```): Endpoint exclusivo para administradores que monitoriza o desempenho e o volume de chamadas dos serviços críticos.

---

## Outros Detalhes de Implementação

**Tratamento de Erros:** Implementação de um GlobalExceptionHandler para garantir que todos os erros retornem respostas JSON padronizadas e amigáveis.

**Padrão DTO:** Separação estrita entre Entidades de Banco de Dados e Objetos de Transferência de Dados (Request/Response) para segurança e clareza da API.

> ** Nota de Segurança para Avaliação:**
> As chaves RSA (`privateKey.pem` e `publicKey.pem`) foram incluídas intencionalmente no repositório para facilitar a execução e avaliação do desafio.
> Em num ambiente de produção real, estas chaves não seriam commitadas; seriam injetadas via Secret Manager (Vault, Kubernetes Secrets) ou variáveis de ambiente no momento do deploy.