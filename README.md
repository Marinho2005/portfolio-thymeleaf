<div align="center">

# 🎨 Portfolio Thymeleaf

**Portfólio pessoal em desenvolvimento** — Spring Boot + Thymeleaf + Tailwind + Supabase

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green)
![License](https://img.shields.io/badge/license-MIT-yellow)

</div>

## 📌 Sobre o projeto

Um portfólio web em desenvolvimento, construído com **Spring Boot** e **Thymeleaf**, usando **Tailwind CSS** para o visual, **JavaScript** para interações e **Supabase (PostgreSQL)** como banco de dados.

> ⚠️ **Projeto em andamento** — funcionalidades podem mudar, bugs podem aparecer e melhorias estão constantemente sendo feitas. Contribuições e sugestões são bem-vindas!

## ✨ Funcionalidades

| Rota | Descrição |
|------|-----------|
| `/` | Página inicial (home) — botão para personalizar o portfólio |
| `/portfolio` | Portfólio com projetos vindos do banco (Supabase) |
| `/configuracoes` | Configurações de perfil (CRUD no Supabase) |
| `/admin/projetos` | CRUD de projetos (listar, criar, editar, excluir) |

## 🛠️ Stack

- **Backend:** Spring Boot 4.1 · Spring Web MVC
- **Templates:** Thymeleaf
- **Frontend:** Tailwind CSS · JavaScript
- **Banco de dados:** PostgreSQL (Supabase — Session Pooler)
- **Migrações:** Flyway (schema em `src/main/resources/db/migration/`)
- **Build:** Maven (Java 21)

## 🚀 Como rodar localmente

### Pré-requisitos

- JDK 21
- Maven (ou use o wrapper `./mvnw`)
- Uma instância do Supabase (ou PostgreSQL)

### Passo a passo

1. Clone o repositório:

   ```bash
   git clone git@github.com:Marinho2005/portfolio-thymeleaf.git
   cd portfolio-thymeleaf
   ```

2. Crie seu arquivo de configuração a partir do template:

   ```bash
   cp src/main/resources/application.properties.example \
      src/main/resources/application.properties
   ```

3. Preencha `application.properties` com suas credenciais do Supabase
   (URL, chave da API, usuário e senha do banco).

4. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Acesse em: <http://localhost:8080>

### 🐳 Rodando com Docker

1. Crie o arquivo de credenciais a partir do modelo (preencha com seus valores):

   ```bash
   cp .env.example .env
   ```

2. Suba a aplicação:

   ```bash
   docker compose up --build
   ```

3. Acesse em: <http://localhost:8080>

> O `.env` está no `.gitignore` e **nunca** deve ir pro GitHub. O `.dockerignore`
> também exclui o `application.properties` da imagem — as credenciais vivem apenas
> no `.env` local, mapeadas como variáveis de ambiente (Spring Boot faz o binding
> automático: `SPRING_DATASOURCE_PASSWORD` → `spring.datasource.password`).

## 🔒 Segurança

⚠️ **Importante:** o arquivo `application.properties` contém senhas e chaves sensíveis e está **ignorado no `.gitignore`** — ele **não** deve ser enviado ao GitHub. Sempre use o template `application.properties.example` como referência e mantenha suas credenciais em sigilo.

## 🗄️ Estrutura do banco

- `settings` — preferências/perfil (sem acesso público via RLS)
- `contact_messages` — mensagens do formulário de contato (RLS permite apenas INSERT)
- `projects` — projetos do portfólio (RLS permite SELECT público)

## 🗺️ Roadmap (próximos passos)

- [x] Estrutura base com Spring Boot + Thymeleaf
- [x] Página de portfólio com dados do banco (Supabase)
- [x] Integração com Supabase (persistência de configurações)
- [x] CRUD de projetos via área administrativa
- [ ] Formulário de contato funcional
- [ ] Reativar o Flyway para migrações automáticas
- [ ] Melhorias de UI/UX e responsividade
- [ ] Deploy em produção

## 📄 Licença

Este projeto está sob a licença MIT.
