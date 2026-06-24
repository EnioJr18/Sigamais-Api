# 🎓 SIGA+ Backend API

> **Status:** Concluído para apresentação (Versão 1.0) 🚀

A API do **SIGA+** é o núcleo de um sistema de gestão acadêmica inteligente desenvolvido para o Instituto Federal de Alagoas (IFAL). Muito além de um simples registro de notas e faltas, o SIGA+ atua como um CRM educacional, calculando automaticamente o risco de reprovação ou evasão dos alunos e alertando a coordenação, mantendo um histórico completo de acompanhamento.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi desenvolvido utilizando as melhores práticas do ecossistema Java e Spring:

* **Java 17+**
* **Spring Boot 3** (Web, Data JPA, Validation)
* **Spring Security & JWT** (Autenticação e Autorização)
* **MySQL** (Banco de Dados Relacional)
* **Lombok** (Redução de boilerplate)
* **JUnit 5 & Mockito** (Testes Unitários com 100% de cobertura nos Controllers)
* **Maven** (Gerenciamento de dependências)

---

## ✨ Principais Funcionalidades

* **🔐 Autenticação Segura:** Login via JWT separando perfis de acesso (Alunos, Professores, Coordenação).
* **📚 Gestão Acadêmica Core:** CRUD completo de Alunos, Professores, Disciplinas, Turmas e Matrículas.
* **📊 Lançamento de Notas e Frequências:** Registro detalhado com cálculo automático de status de aprovação.
* **🚨 Motor de Análise de Risco:** Algoritmo que avalia notas e faltas para classificar o aluno em risco (BAIXO, MÉDIO, ALTO).
* **timeline Histórico de Acompanhamento (CRM):** Sistema de notificação à coordenação com linha do tempo de ações realizadas (Pendente, Em Acompanhamento, Resolvido), com proteção anti-spam para envios de e-mail.

---

## 🚦 Arquitetura

O projeto segue a arquitetura em camadas (Layered Architecture) padrão REST:
* `Controller`: Recebe as requisições HTTP, valida as entradas e devolve os Status Codes corretos (200, 201, 204, 400).
* `Service`: Centraliza toda a regra de negócio (ex: cálculo de risco, verificação de anti-spam).
* `Repository`: Interfaces do Spring Data JPA para persistência no MySQL.
* `DTO (Data Transfer Object)`: Padronização da entrada e saída de dados, blindando as entidades do banco.
* `Model`: Entidades mapeadas com o banco de dados via Hibernate/JPA.

---

## 🌐 Endpoints da API

Abaixo estão as principais rotas mapeadas na aplicação:

### Autenticação (`/auth`)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Autentica o usuário e retorna o Token JWT. |

### Usuários e Perfil (`/usuarios`)
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/usuarios` | Lista todos os usuários. |
| `GET` | `/usuarios/me` | Retorna o perfil do usuário logado. |
| `PUT` | `/usuarios/me` | Atualiza dados do perfil. |
| `PUT` | `/usuarios/me/senha` | Altera a senha do usuário logado. |

### Cadastros Base
| Método | Rota | Descrição |
|---|---|---|
| `GET / POST` | `/alunos` | Lista e cadastra Alunos. |
| `DELETE` | `/alunos/{id}` | Remove um aluno. |
| `GET / POST` | `/professores` | Lista e cadastra Professores. |
| `GET / POST` | `/disciplinas` | Lista e cadastra Disciplinas. |
| `GET / POST` | `/turmas` | Lista e cadastra Turmas. |

### Matrículas (`/matriculas`)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/matriculas` | Realiza a matrícula de um aluno em uma turma. |
| `GET` | `/matriculas/{id}/risco` | Retorna o nível de risco calculado em tempo real. |

### Lançamentos Acadêmicos
| Método | Rota | Descrição |
|---|---|---|
| `GET / POST` | `/notas` | Cadastra e lista notas. |
| `GET` | `/notas/resumo` | Retorna o boletim completo detalhado. |
| `GET / POST` | `/frequencias` | Registra e lista faltas/presenças. |
| `GET` | `/frequencias/resumo` | Retorna o consolidado de frequências. |

### Alertas de Risco (CRM Educacional) - `/alertas-risco`
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/{matriculaId}/notificar` | Analisa a matrícula, dispara e-mail se houver risco alto e gera alerta inicial. |
| `GET` | `/` | Lista todos os alertas ativos para a coordenação. |
| `PUT` | `/{id}` | Atualiza o status de acompanhamento do alerta. |
| `GET` | `/{id}/historico` | Retorna a linha do tempo completa de um alerta (ações tomadas). |

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
* JDK 17 instalado
* MySQL em execução (porta 3306)
* Variáveis de ambiente configuradas no `application.properties` ou `application.yml`.

### Passos
1. Clone este repositório:
   ```bash
   git clone https://github.com/EnioJr18/Sigamais-Api.git
   ```
2. Configure as credenciais do MySQL no arquivo src/main/resources/application.properties:
    ```bash
    Properties
    spring.datasource.url=jdbc:mysql://localhost:3306/sigamais_db
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    
    # Chave secreta para geração do JWT
    api.security.token.secret=sua_chave_secreta_super_segura
    ```

3. Instale as dependências e inicie a aplicação usando o Maven:

    ```bash
    ./mvnw spring-boot:run
    A API estará disponível em http://localhost:8080.
   ```

4. A API estará disponível em http://localhost:8080.

---

## 🧪 Testes Unitários
O projeto conta com uma suíte de testes unitários focada no comportamento da camada de Controllers e validação de regras de negócio dos Services utilizando MockMvc e Mockito.

Para rodar os testes, execute:

```bash
./mvnw test
```

---

## 📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

## 🤝 Contribuição
Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

---

## 👨‍💻 Autores

* **Enio Junior**
* **David Gabriel**
* **Jean Marcos**
* **Yury Galvão**

**Desenvolvido para fins de estudo, portfólio e Trabalho na matéria de Programação Orientada à Objetos 💻**
