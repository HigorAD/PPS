# Padrão 03 — Builder

> **Família:** criacional
> **Pergunta central:** como construir um objeto com muitos campos — alguns obrigatórios, outros opcionais — sem construtores confusos e sem permitir que ele nasça inválido?

## Ideia central

> ### `new Usuario("Ana", "ana@exemplo.com", null, null, null, true, false)`: o que é cada argumento?

```text
 ANTES                             DEPOIS
 
 new Usuario("Ana",                new Usuario.Builder()
             "ana@exemplo.com",        .nome("Ana")
             null, null, null,         .email("ana@exemplo.com")
             true,                     .administrador(true)
             false);                   .build();      ◄── valida aqui
```

**Builder:** o objeto é montado **passo a passo**, com um nome para cada valor, e **só nasce depois de validado**.

| Sem o padrão | Com o padrão |
|---|---|
| Argumentos em ordem fixa e sem nome | Cada valor informado por um método com nome |
| Um construtor para cada combinação de opcionais | Opcionais têm valor padrão: basta não chamar |
| Setters permitem objetos incompletos ou alterados depois | `build()` valida; o objeto nasce pronto e imutável |

> **Três campos não pedem Builder.** Ele compensa com muitos campos, muitos opcionais ou regras de validação.

**Hoje:** construtor direto → construtor telescópico → setters → **Builder**, e o Diretor do GoF.

## Objetivos

Ao final da aula, você deverá conseguir:

- reconhecer os problemas do construtor telescópico e dos setters;
- implementar um Builder com métodos encadeados;
- validar campos obrigatórios antes de o objeto existir;
- construir objetos imutáveis;
- reconhecer a versão original do GoF, com Diretor;
- decidir quando o Builder é exagero.

## Pré-requisitos e execução

- construtores, encapsulamento, `final`, classes aninhadas estáticas e coleções.
- Salve os exemplos completos em `Main.java` e execute com `javac Main.java` e `java Main` (Java 11+).

---

# Etapa 1 — A solução mais simples

Um usuário tem nome e e-mail. Um construtor resolve:

```java
class Usuario {
    private final String nome;
    private final String email;

    Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    String resumo() {
        return nome + " <" + email + ">";
    }
}

public class Main {
    public static void main(String[] args) {
        Usuario usuario = new Usuario("Ana", "ana@exemplo.com");
        System.out.println(usuario.resumo());
    }
}
```

**Saída:** `Ana <ana@exemplo.com>`

**Análise:** dois campos, ambos obrigatórios, ordem óbvia. Um construtor é a melhor solução. **Builder aqui seria exagero.**

---

# Etapa 2 — O problema cresce: construtor telescópico

Novos requisitos acrescentam telefone, cidade, estado, perfil de administrador e preferência por notícias:

```java
class Usuario {
    Usuario(String nome, String email) { ... }
    Usuario(String nome, String email, String telefone) { ... }
    Usuario(String nome, String email, String telefone, String cidade, String estado) { ... }
    Usuario(String nome, String email, String telefone, String cidade, String estado,
            boolean administrador, boolean recebeNoticias) { ... }
}
```

A chamada fica assim:

```java
Usuario usuario = new Usuario("Ana", "ana@exemplo.com", "51999999999",
                              "Porto Alegre", "RS", true, false);
```

Problemas:

- é fácil **trocar a ordem** de dois `String` (cidade e estado) sem erro de compilação;
- `true, false` não dizem nada a quem lê;
- cada novo campo multiplica os construtores (o nome "telescópico" vem disso);
- quem quer informar só o perfil de administrador precisa passar todos os outros campos.

---

# Etapa 3 — Primeira tentativa: construtor vazio e setters

```java
Usuario usuario = new Usuario();
usuario.setNome("Ana");
usuario.setEmail("ana@exemplo.com");
usuario.setAdministrador(true);
```

A leitura melhorou, mas surgiram problemas novos:

| Problema | Consequência |
|---|---|
| `new Usuario()` cria um objeto sem nome nem e-mail | O objeto existe em **estado inválido** |
| A validação não tem um momento certo para acontecer | Erros aparecem longe da criação |
| Setters públicos continuam disponíveis depois | O objeto pode ser alterado a qualquer momento |
| Os campos não podem ser `final` | Perde-se a imutabilidade |

Precisamos da legibilidade dos setters **e** da segurança do construtor.

---

# Etapa 4 — Builder

## 4.1 A estrutura

- O objeto final tem campos `final` e um **construtor privado** que recebe o builder.
- Uma classe aninhada `Builder` guarda temporariamente os valores.
- Cada método do builder recebe um valor e **devolve o próprio builder** (`return this`), permitindo o encadeamento.
- O método `build()` **valida** e só então cria o objeto.

## 4.2 Exemplo completo

```java
class Usuario {
    private final String nome;
    private final String email;
    private final String telefone;
    private final boolean administrador;

    private Usuario(Builder builder) {
        this.nome = builder.nome;
        this.email = builder.email;
        this.telefone = builder.telefone;
        this.administrador = builder.administrador;
    }

    static class Builder {
        private String nome;
        private String email;
        private String telefone = "não informado";
        private boolean administrador = false;

        Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        Builder administrador(boolean administrador) {
            this.administrador = administrador;
            return this;
        }

        Usuario build() {
            if (nome == null || nome.isBlank()) {
                throw new IllegalStateException("Nome é obrigatório.");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalStateException("E-mail inválido.");
            }
            return new Usuario(this);
        }
    }

    void exibirResumo() {
        System.out.println("Nome: " + nome);
        System.out.println("E-mail: " + email);
        System.out.println("Telefone: " + telefone);
        System.out.println("Administrador: " + administrador);
    }
}

public class Main {
    public static void main(String[] args) {
        Usuario ana = new Usuario.Builder()
            .nome("Ana")
            .email("ana@exemplo.com")
            .administrador(true)
            .build();
        ana.exibirResumo();

        try {
            new Usuario.Builder().nome("Bruno").build();
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
```

**Saída esperada:**

```text
Nome: Ana
E-mail: ana@exemplo.com
Telefone: não informado
Administrador: true
Erro: E-mail inválido.
```

## 4.3 Acompanhe a execução

1. `new Usuario.Builder()` cria o builder com os valores padrão (`telefone` e `administrador`).
2. Cada chamada (`.nome(...)`, `.email(...)`) guarda um valor e devolve o mesmo builder.
3. `.build()` verifica nome e e-mail. Só depois chama o construtor privado.
4. O `Usuario` nasce completo; seus campos são `final` e não há setters.
5. No segundo caso, o e-mail não foi informado: `build()` lança a exceção e **nenhum** `Usuario` é criado.

## 4.4 O que o Builder resolveu

| Problema anterior | Como foi resolvido |
|---|---|
| Ordem dos argumentos | Cada valor é informado por um método com nome |
| Booleanos sem significado | `.administrador(true)` explica o valor |
| Campos opcionais | Valores padrão no builder; basta não chamar o método |
| Objeto inválido | `build()` valida antes de criar |
| Mutabilidade | Campos `final`, construtor privado, sem setters |

---

# Etapa 5 — Até onde evoluir?

## 5.1 Obrigatórios no construtor do builder

Se alguns campos são sempre obrigatórios, eles podem ir para o construtor do próprio builder. Assim, o compilador impede que sejam esquecidos:

```java
Usuario ana = new Usuario.Builder("Ana", "ana@exemplo.com")
    .telefone("51999999999")
    .build();
```





## 5.2 Quando não usar

- poucos campos, todos obrigatórios;
- construtor já claro;
- sem validação especial nem campos opcionais;
- objeto criado em poucos lugares.

> Builder compra legibilidade e segurança ao custo de praticamente **duplicar os campos** da classe. Em Java 16+, um `record` com construtor compacto resolve muitos casos simples de objetos imutáveis.

---

# Etapa 6 — Exercícios

## Exercício 1 — Pedido

Crie a classe `Pedido` com cliente (obrigatório), endereço (obrigatório), observação (opcional, padrão vazio) e prioridade (opcional, padrão `NORMAL`). Use Builder, valide os obrigatórios e não ofereça setters.

## Exercício 2 — Obrigatórios garantidos pelo compilador

Modifique a solução do exercício 1 para que cliente e endereço sejam parâmetros do construtor do `Builder`. Qual erro deixa de ser possível? Qual validação ainda precisa ficar em `build()`?

## Exercício 3 — Diretor

Usando a seção 5.3, crie um `ConstrutorMarkdown` (`# título` e parágrafos separados por linha em branco) e use o mesmo `Diretor` para gerar o comunicado nos três formatos. O `Diretor` precisou mudar?

## Solução de referência — exercício 1

```java
class Pedido {
    private final String cliente;
    private final String endereco;
    private final String observacao;
    private final String prioridade;

    private Pedido(Builder builder) {
        this.cliente = builder.cliente;
        this.endereco = builder.endereco;
        this.observacao = builder.observacao;
        this.prioridade = builder.prioridade;
    }

    static class Builder {
        private String cliente;
        private String endereco;
        private String observacao = "";
        private String prioridade = "NORMAL";

        Builder cliente(String cliente) {
            this.cliente = cliente;
            return this;
        }

        Builder endereco(String endereco) {
            this.endereco = endereco;
            return this;
        }

        Builder observacao(String observacao) {
            this.observacao = observacao;
            return this;
        }

        Builder prioridade(String prioridade) {
            this.prioridade = prioridade;
            return this;
        }

        Pedido build() {
            if (cliente == null || cliente.isBlank()) {
                throw new IllegalStateException("Cliente é obrigatório.");
            }
            if (endereco == null || endereco.isBlank()) {
                throw new IllegalStateException("Endereço é obrigatório.");
            }
            return new Pedido(this);
        }
    }

    void exibirResumo() {
        System.out.println("Cliente: " + cliente);
        System.out.println("Endereço: " + endereco);
        System.out.println("Observação: " + observacao);
        System.out.println("Prioridade: " + prioridade);
    }
}

public class Main {
    public static void main(String[] args) {
        Pedido pedido = new Pedido.Builder()
            .cliente("Bruno")
            .endereco("Rua das Flores, 100")
            .observacao("Entregar na portaria.")
            .prioridade("ALTA")
            .build();

        pedido.exibirResumo();
    }
}
```

**Saída esperada:**

```text
Cliente: Bruno
Endereço: Rua das Flores, 100
Observação: Entregar na portaria.
Prioridade: ALTA
```

**Respostas esperadas:** no exercício 2, esquecer cliente ou endereço vira **erro de compilação**, mas `build()` ainda precisa rejeitar textos vazios. No exercício 3, o `Diretor` não muda: só um novo builder concreto é criado.

---

# Etapa 7 — Onde o padrão é útil

## 7.1 Montando um e-mail

Um e-mail tem remetente e assunto obrigatórios, mas pode ter **vários** destinatários, cópias e anexos, acrescentados **um de cada vez**, às vezes em pontos diferentes do código. Aqui o Builder brilha: além de nomear os valores, ele **acumula** itens e valida o conjunto no final (por exemplo, "pelo menos um destinatário").

```java
import java.util.ArrayList;
import java.util.List;

class Email {
    private final String remetente;
    private final String assunto;
    private final List<String> destinatarios;
    private final List<String> anexos;

    private Email(Builder b) {
        this.remetente = b.remetente;
        this.assunto = b.assunto;
        this.destinatarios = List.copyOf(b.destinatarios);   // cópia defensiva
        this.anexos = List.copyOf(b.anexos);
    }

    static class Builder {
        private final String remetente;
        private final String assunto;
        private final List<String> destinatarios = new ArrayList<>();
        private final List<String> anexos = new ArrayList<>();

        Builder(String remetente, String assunto) {
            this.remetente = remetente;
            this.assunto = assunto;
        }

        Builder para(String destinatario) {
            destinatarios.add(destinatario);
            return this;
        }

        Builder anexar(String arquivo) {
            anexos.add(arquivo);
            return this;
        }

        Email build() {
            if (destinatarios.isEmpty()) {
                throw new IllegalStateException("Informe ao menos um destinatário.");
            }
            if (anexos.size() > 3) {
                throw new IllegalStateException("Máximo de 3 anexos.");
            }
            return new Email(this);
        }
    }

    @Override
    public String toString() {
        return "De: " + remetente + " | Assunto: " + assunto
            + " | Para: " + destinatarios + " | Anexos: " + anexos;
    }
}

public class Main {
    public static void main(String[] args) {
        Email.Builder builder = new Email.Builder("secretaria@uni.br", "Notas finais")
            .para("ana@uni.br")
            .para("bruno@uni.br");

        boolean incluirBoletim = true;   // decidido em outro ponto do sistema
        if (incluirBoletim) {
            builder.anexar("boletim.pdf");
        }

        Email email = builder.build();
        System.out.println(email);
    }
}
```

**Saída esperada:**

```text
De: secretaria@uni.br | Assunto: Notas finais | Para: [ana@uni.br, bruno@uni.br] | Anexos: [boletim.pdf]
```

## Síntese

- Construtor simples é a melhor opção para poucos campos obrigatórios.
- Construtores telescópicos e setters públicos geram chamadas confusas e objetos inválidos ou mutáveis.
- **Builder** nomeia cada valor, aplica padrões, valida no `build()` e entrega um objeto imutável.
- A versão GoF usa um **Diretor** para produzir **representações diferentes** com os mesmos passos.
- O custo é duplicar os campos em uma classe auxiliar.

**Próximo padrão:** e se, em vez de montar do zero, for mais fácil **copiar** um objeto pronto e ajustar alguns detalhes? → [Padrão 04 — Prototype](04-prototype.md)
