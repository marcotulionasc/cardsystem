# 🚀 Card System API 💳


Este projeto aborda a criação de um sistema de cartão por meio de uma API REST. A finalidade principal deste projeto é
proporcionar uma oportunidade prática para aplicar os conhecimentos adquiridos nas trilhas de aprendizado do programa de
estágio.

## 📋 Resumo


Este projeto tem como objetivo criar uma aplicação onde o tema é Registro de cartões. As seguintes operações estão presentes
no Swagger:

##### 🔒 Autenticação
    - Registro de um novo login de usuário
    - Login de um novo usuário

##### 👤 Usuário

    - Registro de um novo usuário
    - Buscar usuário pelo ID
    - Buscar usuário pelo status
    - Buscar usuário pelo faixa salarial
    - Listagem de cartões de usuário
    - Atualizar dados do usuário
    - Inativar um usuário existente

##### 💳 Cartão

    - Registro de um novo cartão
    - Buscar detalhes do cartão pelo ID
    - Buscar cartão pela bandeira
    - Atualizar dados do cartão
    - Deletar cartão existente

## 💾 Instalação

Para instalar e configurar o projeto Card System, certifique-se de ter o Docker instalado em sua máquina e siga os seguintes passos:

1. Clone o repositório do projeto usando `git clone`.
2. Acesse a pasta do projeto com `cd projeto-tecnico-2023`.
3. Certifique-se de que todas as dependências do Maven estão instaladas, conforme especificado no arquivo `pom.xml`.
4. Para executar o projeto em um ambiente Docker, siga estas etapas:
    - Abra o Docker e navegue até a pasta `src/main/resources` no projeto.
    - Execute o comando `docker-compose up -d`.
5. Após configurar o ambiente Docker, execute o programa no IntelliJ.

## 🚀 Uso

Para usar o projeto Card System, basta seguir os seguintes passos:

- Acesse o aplicativo Postman

- Monte as rotas da API: `http://localhost:8089/request`


## 📚 Documentações do projeto


Para obter mais informações sobre os diagramas de classes e diagrama de banco de dados, <a href="https://www.figma.com/file/4m3BH2iv9wk4CNzkoUpTiu/Diagrams?type=whiteboard&node-id=0%3A1&t=a1XBMu9L0PhfqIEw-1">clique aqui</a>.