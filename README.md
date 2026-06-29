# Sistema Escolar 

Sistema escolar em Java usando Spring Boot para gerenciamento de dados acadêmicos. O sistema possui as entidades Aluno, Professor e Matrícula e implementa requisições para cadastro, atualização, listagem e exclusão dessas entidades (com exceção de Matrícula, que não possui opção de atualização)

Este repositório contém o projeto de avaliação do Ciclo 2 da disciplina de Programação Web I que consiste na identificação e correção de erros no sistema.

**Aluna**: Lara Peddinghausen


# Erros encontrados:
## ✖ Erro 1
* **Arquivo**: `pom.xml`
* **Linha(s)**: 
* **Erro**: Ao tentar acessar http://localhost:8080/h2-console aparece uma página de erro:  
![Imagem do erro 1](/img/Erro1.png)
* **Correção**: Incluir a dependência spring-boot-h2console nas dependências do Maven  
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-h2console</artifactId>
</dependency>
```
* **Justificativa**: A denpendência é responsável pela criação do visual do H2, como ela não estava incluída nas dependências do Maven, a página do h2 dava erro ao ser acessada.

## ✖ Erro 2
* **Arquivo**: `ProfessorController.java`
* **Linha(s)**: 26
* **Erro sugerido**: Método de cadastro sem @Transactional — o dado não será persistido no banco
* **Correção**: Incluir anotação `@Transactional` acima do método cadastrar
* **Obs**: Fiz a inclusão da anotação porque o erro estava sinalizado no código, mas não o identifiquei ao rodar o sistema. No meu computador, os dados de cadastro do professor persistiram no banco mesmo sem a anotação.  
![Imagem do erro 2](/img/Erro2.png)  

Perguntei ao Gemini sobre isso e essa foi a justificativa dada:  

>"Isso confirma o que descobrimos! Se os dados estão aparecendo no console do H2 quando você roda o SELECT, significa que a persistência foi concluída com sucesso no banco de dados.
>
>O motivo disso acontecer, mesmo que o seu método do Controller esteja sem a anotação @Transactional, é o comportamento padrão do Spring Data JPA:
>
>A transação embutida do repositório: Quando o seu código executa a linha repository.save(professor);, ele entra em um método nativo do Spring que já possui a anotação @Transactional configurada internamente de fábrica.
>
>O Commit Automático: Esse repositório abre uma transação rápida, dispara o comando INSERT no banco de dados H2, salva o registro e fecha a transação imediatamente.
>
>Por isso, para a operação de Cadastro, o dado é gravado de forma permanente mesmo sem a anotação no seu método principal.
>
>O cenário real onde a falta do @Transactional vai gerar um bug e deixar a tabela do H2 desatualizada (sem salvar nada) é no método de Atualização (PUT), caso você apenas altere os atributos da entidade na memória e não chame o método .save() explicitamente depois."

Ao testar o que a IA falou sobre o método de atualização, consegui notar o erro da falta de persistência ao ver que os dados não foram atualizados no banco.

## ✖ Erro 3
* **Arquivo**: `Aluno.java`
* **Linha(s)**: 
* **Erro**: Ao chamar qualquer endponit dos métodos do tipo GET, tanto da entidade Aluno quanto da entidade Professor, a requisição retorna uma lista que junta os alunos e os professores cadastrados. Além disso, ao cadastrar um aluno ou um professor, a contagem do id fica unificada.  
![Imagem do erro 3](/img/Erro3.png)
* **Correção**: `@Table(name = "professores")` ➔ `@Table(name = "alunos")`
* **Justificativa**: Como ambas as entidades(Aluno e Professor) tinham tabelas declaradas com o mesmo nome, o sistema entendia que era a mesma tabela. Como era a mesma tabela, o incremento do id era o mesmo para as duas entidades e a requisição GET retornava tudo que estava nessa tabela.

## ✖ Erro 4
* **Arquivo**: `DadosListagemAluno.java`
* **Linha(s)**: 14 e 15 
* **Erro**: Nome e email do aluno aparecem invertidos no JSON quando o método GET listarPorPagina da entidade Aluno é chamado:  
![Imagem do erro 4](/img/Erro4.png)
* **Correção**:  `aluno.getEmail(),` ➔ `aluno.getNome(),`  
           `aluno.getNome(),` ➔ `aluno.getEmail(),`
* **Justificativa**: Os componentes de DadosListagemAluno foram declarados na ordem:    
`String nome,`  
`String email,`  
Mas no construtor, a ordem dos argumentos passada no this() estava invertida, fazendo com que as informações aparecessem trocadas no JSON

## ✖ Erro 5
* **Arquivo**: `AlunoController.java`
* **Linha(s)**: 47
* **Erro**:  
![Imagem do erro 5](/img/Erro5.png)
* **Correção**: `@PostMapping` ➔ `@PutMapping`
* **Justificativa**: No arquivo existe os métodos cadastrar e atualizar, que estão descritos com a mesma anotação @PostMappinng, mas nenhum dos dois vem acompanhado de uma rota diferente, gerando uma ambiguidade que impede a aplicação de rodar. A anotação do método atualizar foi mudada para @PutMapping pois se trata de um método para atualizar dados já existentes.

## ✖ Erro 6
* **Arquivo**: `Professor.java`
* **Linha(s)**: 47
* **Erro**: Ao mudar o e-mail de um professor com o método PUT atualizar da entidade Professor, o novo e-mail é lançado no campo do nome e não é atualizado no campo do e-mail:  
![Imagem do erro 6](/img/Erro6.png)
* **Correção**: `this.nome` ➔  `this.email`
* **Justificativa**: O método atualizar chama o método atualizarInformacoes, do arquivo Professor.java. Como no método atualizarInformacoes havia essa troca na atribuição, os dados eram atualizados erroneamente

## ✖ Erro 7
* **Arquivo**: `MatriculaController.java`
* **Linha(s)**: 50 e 51
* **Erro**:   
![Imagem do erro 7](/img/Erro7-2.png)   
![Imagem do erro 7](/img/Erro7-1.png) 
* **Correção**: `@PathVariable Integer ids` ➔ `@PathVariable Integer id`
`deleteById(ids)` ➔ `deleteById(id)`
* **Justificativa**: O parâmetro 'ids' não era condizente com a variável 'id' passada na rota. Parâmetros foram ajustados para o singular, pois a rota com 'ids' no plural não ficaria coerente, uma vez que apenas um id é passado por vez na requisição

## ✖ Erro 8
* **Arquivo**: `AlunoRepository.java`
* **Linha(s)**: 6
* **Erro**:   
![Imagem do erro 8](/img/Erro8-1.png) 
* **Correção**: `JpaRepository<Aluno, String>` ➔ `JpaRepository<Aluno, Integer>`
* **Justificativa**: O segundo parâmetro passado dentro do diamante deve ser Integer e não String, uma vez que a chave primária da entidade Aluno (Id) é do tipo Integer, como se pode conferir no arquivo Aluno.java, linha 19.  


**Obs**: Essa correção também resultou na resolução dos erros getReferenceById e atualizarInformacoes do arquivo AlunoController.java (linhas 50 e 51)
![Erros corrigidos em consequência da correção do erro 8](/img/Erro8-2.png) 


**Obs2**: Essa correção gerou erros nos arquivos:
### Erro 8.1
* **Arquivo**: `MatriculaController.java`
* **Linha(s)**: 34 
* **Erro**:   
![Imagem do erro gerado pela correção do erro 8](/img/Erro8-4.png) 
* **Correção**: `getReferenceById(dados.alunoId().toString())` ➔ `getReferenceById(dados.alunoId())`
* **Justificativa**: O método getReferenceById espera receber um argumento do tipo Integer, uma vez que o alunoId declarado na DTO DadosCadastroMatricula é do tipo Integer

### Erro 8.2
* **Arquivo**: `AlunoController.java`
* **Linha(s)**: 56 e 57
* **Erro**:   
![Imagem do erro gerado pela correção do erro 8](/img/Erro8-3.png) 
* **Correção**: `@PathVariable String id` ➔ `@PathVariable Integer id`
* **Justificativa**: DeleteById espera receber um argumento do tipo Integer, uma vez que o Id da entidade Aluno é do tipo Integer

# Como executar o projeto

1. Clone o repositório acima 
2. Abra o projeto na sua IDE (IntelliJ IDEA, Eclipse ou VS Code com extensão Java).
3. Execute a classe AppApplication.java.
4. Com o servidor rodando, use o Insomnia ou Postman para testar os endpoints. (Exemplos de requisições JSON para testes se encontram logo abaixo).

**Opcional**: Para visualizar as tabelas criadas no banco de dados em memória, acesse http://localhost:8080/h2-console no seu navegador e configure os campos conforme abaixo (deixe a senha em branco):  
`Driver Class`: org.h2.Driver  
`JDBC URL`: jdbc:h2:mem:sistemaescolar  
`User Name`: sa  
`Password`: (em branco)

# Exemplos de requisições (JSON) para testar os endpoints

## Aluno (localhost:8080/alunos)
* **Cadastrar**:   
```
{
	"nome": "Olivia Oliveira",
  "email": "olivia.oliveira@email.com",
  "telefone": "(21) 99873-9873",
	"ra": "01",
	"curso": "ADS",
  "endereco": {
      "logradouro": "rua 3",
      "bairro": "bairro",
      "cep": "12345678",
      "complemento": "bloco 1",
      "cidade": "Brasil",
      "uf": "DF"
        }
}
```

* **Alterar**:   
```
{
  "id": 1,
	"nome": "Olivia Oliveiras",
  "telefone": "(21) 99873-3789",
	"curso": "Artes",
  "endereco": {
      "logradouro": "rua 3",
      "bairro": "bairro",
      "cep": "12345678",
      "complemento": "bloco 1",
      "cidade": "Brasil",
      "uf": "DF"
        }
}
```
## Professor (localhost:8080/professores)
* **Cadastrar**:   
```
{
	"nome": "Beth Bethania",
  "email": "beth.bethania@email.com",
  "telefone": "(21) 99873-4521",
	"registro": "001",
	"disciplina": "ARTES",
  "endereco": {
      "logradouro": "rua 11",
      "bairro": "bairro",
      "cep": "12345612",
      "complemento": "bloco 3",
      "cidade": "Brasil",
      "uf": "SP"
        }
}
```

* **Alterar**:   
```
{
	"id": 1,
	"nome": "Beth",
  "email": "beth.bethania@email",
  "telefone": "(21) 99873-4521",
  "endereco": {
      "logradouro": "rua 11",
      "bairro": "bairro",
      "cep": "12345612",
      "complemento": "bloco 3",
      "cidade": "Brasil",
      "uf": "SP"
        }
}
```

## Matrícula (localhost:8080/matriculas)
* **Cadastrar**:   
```
{
	"alunoId": 1,
  "professorId": 1,
  "turno": "TARDE",
	"semestre": "1",
	"observacao": "Não tem",
  "dataMatricula": "2026-06-28T14:30:00"
}
```


## ANOTATIONS - ANOTAÇÕES
1. Anotações do Spring Web
`@RequestMapping("/medicos")`
=> Define que a classe está mapeada para a url[endpoint] /medicos.

`@RestController`
=> Define que a classe é uma classe controladora no Spring.

`@GetMapping` 
=> Define que o método será somente leitura.

`@PostMapping`
=> Define que o método irá receber dados.

`@PutMapping`
=> Atualiza alguma informação.

`@DeleteMapping`
=> Deleta dados.

`@ResquestBody`
=> é utilizada quando você irá receber dados pelo simulador de requisição [insomnia], e informa que os dados serão enviados no corpo da requisição.

`@Autowired`
=> é utilizado quando você está aplicando a injeção de depêndencia. Ou seja, o Springboot sabe o que a classe(interface) possui de métodos e atributos.

`@Transactional`
=> é utilizado para que o método consiga realizar algum tipo de modelagem(alteração) no BD.

## RELACIONAMENTO ENTRE TABELAS NO SPRINGBOOT
`@OneToOne` -> Um para um. (Uma consulta está ligada a um único médico).
`@OneToMany` -> Um para muitos. (Um médico tem várias consultas).
`@ManyToOne` -> Muitos para um. (Muitas consultas para um paciente).
`@ManyToMany` -> Muitos para Muitos. (Muitos pacientes para muitos médicos).

`Chave Primária (PK)` -> é o atributo(campo) que identifica a tabela(objeto) no BD.
`Chave Estrangeira (FK)` -> é o atributo PK que está mencionado em uma outra tabela, que por sua vez será uma chave estrangeira no BD.

OBS:
1. Sempre defina o lado "dono" da `relação(@JoinColumn)` o lado que tem a `FK(chave estrangeira).`