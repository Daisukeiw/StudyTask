# 📚 StudyTask -- Aplicativo de Gestão de Estudos

Aplicativo Android desenvolvido em **Kotlin + Jetpack Compose**,
utilizando **Firebase Authentication** para login e **Cloud Firestore**
para armazenamento de tarefas organizadas por usuário.

O sistema possui:

✔️ Tela de Login e Registro\
✔️ Tela Home com lista de tarefas\
✔️ CRUD completo (Criar, Ler, Atualizar e Deletar tarefas)\
✔️ Organização das tasks por usuário no Firestore (`users/{uid}/tasks`)\
✔️ Navegação com Navigation Compose\
✔️ Arquitetura desacoplada com ViewModels + Repository\
✔️ Regras seguras do Firestore\
✔️ Telas estilizadas com Material 3

------------------------------------------------------------------------

# 📸 Demonstração do App

> Adicione aqui seus prints reais:

-   Tela de Login\
<img width="485" height="1158" alt="image" src="https://github.com/user-attachments/assets/4f077179-ef17-4a26-bbeb-85973e920c7e" />

------------------------------------------------------------------------

-   Tela Home\
<img width="485" height="1158" alt="image" src="https://github.com/user-attachments/assets/7122a61b-358f-42ba-bced-6056ead2e91c" />

------------------------------------------------------------------------

-   Tela de Nova/Editar Tarefa\
<img width="485" height="1158" alt="image" src="https://github.com/user-attachments/assets/b1d65278-8d14-4978-994b-7e45595be55a" />

------------------------------------------------------------------------

-   Print do Firestore mostrando `users/{uid}/tasks`
<img width="1158" height="485" alt="image" src="https://github.com/user-attachments/assets/fa4a89b4-fd0f-43b9-8109-e58d1ff5ca21" />

------------------------------------------------------------------------

<img width="1322" height="727" alt="image" src="https://github.com/user-attachments/assets/6410d7f7-83df-45a6-9bf4-8753f00a9e70" />

------------------------------------------------------------------------

# 🛠️ Tecnologias Utilizadas

### **Frontend**

-   Kotlin
-   Jetpack Compose
-   Material Design 3
-   Navigation Compose
-   ViewModel + State Hoisting

### **Backend**

-   Firebase Authentication
-   Firebase Firestore


------------------------------------------------------------------------

# 📂 Estrutura do Projeto

    app/
    └── src/main/java/com/example/studytask/
        ├── MainActivity.kt
        ├── navigation/
        │   └── AppNavHost.kt
        ├── data/
        │   ├── auth/
        │   │   └── AuthRepository.kt
        │   ├── model/
        │   │   └── Task.kt
        │   └── task/
        │       └── TaskRepository.kt
        └── ui/
            └── screens/
                ├── LoginScreen.kt
                ├── HomeScreen.kt
                └── TaskFormScreen.kt

------------------------------------------------------------------------

# 🚀 Funcionalidades

### ✔ Login & Registro

-   Autenticação com email e senha\
-   Validações\
-   Feedback visual

### ✔ Tela Inicial (Home)

-   Lista de tarefas do usuário logado\
-   Botão para adicionar novas tarefas\
-   Ação de logout\
-   Botão para excluir tarefas\
-   Navegação para editar tarefa

### ✔ CRUD completo

-   Criar tarefa\
-   Editar tarefa\
-   Excluir tarefa\
-   Listar tarefas\
-   Salvar no Firestore por usuário

### ✔ Firestore estruturado por usuário

    users/
       └── uid123/
            └── tasks/
                  ├── id1
                  ├── id2


