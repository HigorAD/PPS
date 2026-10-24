# Aula 0 - Revisão de Orientação a Objetos em Java

## Propósito da aula

Esta aula revisa as construções de orientação a objetos que serão necessárias nas primeiras aulas de Padrões de Projeto de Software.

O foco é prático:

- classes e objetos;
- atributos e métodos;
- construtores;
- encapsulamento;
- composição;
- herança;
- polimorfismo;
- interfaces;
- coleções básicas.

> **Importante:** esta aula não entra em padrões de projeto. Ela prepara a base para que os padrões façam sentido a partir da Aula 1.

## Como usar este material

Todos os exemplos executáveis foram escritos para ambientes em que o arquivo principal se chama `Main.java`.

Regra usada nos blocos:

- a classe principal é `public class Main`;
- classes auxiliares ficam sem `public`;
- interfaces auxiliares ficam sem `public`;
- cada bloco pode ser colado em um único arquivo `Main.java`.

<hr>

# Etapa 1 - Classes, objetos e métodos

## Ideia central

Uma classe define uma estrutura.

Um objeto é uma instância dessa estrutura.

```text
Classe   -> molde
Objeto   -> algo criado a partir do molde
Atributo -> estado
Método   -> comportamento
```

## Exemplo 1 - Classe simples

> **Cole em `Main.java` e execute.**

```java
class Produto {
    String nome;
    double preco;

    void exibirResumo() {
        System.out.println(nome + " custa R$ " + preco);
    }
}

public class Main {
    public static void main(String[] args) {
        Produto teclado = new Produto();
        teclado.nome = "Teclado";
        teclado.preco = 120.00;

        Produto mouse = new Produto();
        mouse.nome = "Mouse";
        mouse.preco = 80.00;

        teclado.exibirResumo();
        mouse.exibirResumo();
    }
}
```

## O que observar

- `Produto` define a estrutura.
- `teclado` e `mouse` são objetos diferentes.
- Cada objeto tem seu próprio estado.
- O método `exibirResumo` usa os dados do próprio objeto.

## Problema do exemplo

Os atributos estão acessíveis diretamente:

```java
teclado.preco = -100.00;
```

Isso permite criar estados inválidos.

Para controlar melhor o estado de um objeto, usamos encapsulamento.

<hr>

# Etapa 2 - Encapsulamento e construtores

## Ideia central

Encapsular é proteger o estado interno do objeto e oferecer métodos controlados de acesso.

Em Java, isso normalmente envolve:

- atributos `private`;
- construtores;
- métodos públicos;
- validações.

## Exemplo 2 - Produto encapsulado

> **Cole em `Main.java` e execute.**

```java
class Produto {
    private String nome;
    private double preco;

    Produto(String nome, double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }

        this.nome = nome;
        this.preco = preco;
    }

    String getNome() {
        return nome;
    }

    double getPreco() {
        return preco;
    }

    void aplicarDesconto(double percentual) {
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("Percentual inválido.");
        }

        preco = preco - (preco * percentual / 100);
    }

    void exibirResumo() {
        System.out.println(nome + " custa R$ " + preco);
    }
}

public class Main {
    public static void main(String[] args) {
        Produto produto = new Produto("Monitor", 900.00);
        produto.aplicarDesconto(10);
        produto.exibirResumo();
    }
}
```

## Exercício 1 - Conta bancária

Crie uma classe `ContaBancaria` com:

- titular;
- saldo;
- construtor;
- método `depositar`;
- método `sacar`;
- método `exibirSaldo`.

Regras:

- depósito deve ser maior que zero;
- saque deve ser maior que zero;
- saque não pode deixar saldo negativo.

## Solução do exercício 1

> **Cole em `Main.java` e execute.**

```java
class ContaBancaria {
    private String titular;
    private double saldo;

    ContaBancaria(String titular, double saldoInicial) {
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }

        this.titular = titular;
        this.saldo = saldoInicial;
    }

    void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Depósito deve ser maior que zero.");
        }

        saldo += valor;
    }

    void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Saque deve ser maior que zero.");
        }

        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        saldo -= valor;
    }

    void exibirSaldo() {
        System.out.println(titular + " possui saldo de R$ " + saldo);
    }
}

public class Main {
    public static void main(String[] args) {
        ContaBancaria conta = new ContaBancaria("Ana", 500.00);
        conta.depositar(200.00);
        conta.sacar(150.00);
        conta.exibirSaldo();
    }
}
```

<hr>

# Etapa 3 - Composição

## Ideia central

Composição acontece quando uma classe usa outra classe para cumprir uma responsabilidade.

Em vez de uma classe fazer tudo sozinha, ela colabora com outros objetos.

Composição será uma ideia central nas aulas de padrões de projeto.

## Exemplo 3 - Pedido composto por cliente e item

> **Cole em `Main.java` e execute.**

```java
class Cliente {
    private String nome;

    Cliente(String nome) {
        this.nome = nome;
    }

    String getNome() {
        return nome;
    }
}

class ItemPedido {
    private String descricao;
    private double preco;

    ItemPedido(String descricao, double preco) {
        this.descricao = descricao;
        this.preco = preco;
    }

    String getDescricao() {
        return descricao;
    }

    double getPreco() {
        return preco;
    }
}

class Pedido {
    private Cliente cliente;
    private ItemPedido item;

    Pedido(Cliente cliente, ItemPedido item) {
        this.cliente = cliente;
        this.item = item;
    }

    void exibirResumo() {
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Item: " + item.getDescricao());
        System.out.println("Total: R$ " + item.getPreco());
    }
}

public class Main {
    public static void main(String[] args) {
        Cliente cliente = new Cliente("Bruno");
        ItemPedido item = new ItemPedido("Livro", 75.00);
        Pedido pedido = new Pedido(cliente, item);
        pedido.exibirResumo();
    }
}
```

## Exercício 2 - Motor e carro

Crie:

- uma classe `Motor` com método `ligar`;
- uma classe `Carro` que recebe um `Motor` no construtor;
- um método `ligarCarro` em `Carro` que chama o motor.

## Solução do exercício 2

> **Cole em `Main.java` e execute.**

```java
class Motor {
    void ligar() {
        System.out.println("Motor ligado.");
    }
}

class Carro {
    private Motor motor;

    Carro(Motor motor) {
        this.motor = motor;
    }

    void ligarCarro() {
        System.out.println("Ligando o carro...");
        motor.ligar();
    }
}

public class Main {
    public static void main(String[] args) {
        Motor motor = new Motor();
        Carro carro = new Carro(motor);
        carro.ligarCarro();
    }
}
```

<hr>

# Etapa 4 - Herança e polimorfismo

## Ideia central

Herança permite criar uma classe a partir de outra.

Polimorfismo permite tratar objetos diferentes por um tipo comum.

Essas ideias são úteis, mas precisam ser usadas com cuidado.

## Exemplo 4 - Herança e sobrescrita

> **Cole em `Main.java` e execute.**

```java
class Funcionario {
    private String nome;

    Funcionario(String nome) {
        this.nome = nome;
    }

    String getNome() {
        return nome;
    }

    double calcularBonus() {
        return 500.00;
    }
}

class Gerente extends Funcionario {
    Gerente(String nome) {
        super(nome);
    }

    @Override
    double calcularBonus() {
        return 1500.00;
    }
}

class Desenvolvedor extends Funcionario {
    Desenvolvedor(String nome) {
        super(nome);
    }

    @Override
    double calcularBonus() {
        return 1000.00;
    }
}

public class Main {
    public static void main(String[] args) {
        Funcionario gerente = new Gerente("Carla");
        Funcionario dev = new Desenvolvedor("Diego");

        System.out.println(gerente.getNome() + ": R$ " + gerente.calcularBonus());
        System.out.println(dev.getNome() + ": R$ " + dev.calcularBonus());
    }
}
```

## Exemplo 5 - Lista polimórfica

> **Cole em `Main.java` e execute.**

```java
import java.util.ArrayList;
import java.util.List;

class Animal {
    void emitirSom() {
        System.out.println("Som genérico.");
    }
}

class Cachorro extends Animal {
    @Override
    void emitirSom() {
        System.out.println("Au au.");
    }
}

class Gato extends Animal {
    @Override
    void emitirSom() {
        System.out.println("Miau.");
    }
}

public class Main {
    public static void main(String[] args) {
        List<Animal> animais = new ArrayList<>();
        animais.add(new Cachorro());
        animais.add(new Gato());

        for (Animal animal : animais) {
            animal.emitirSom();
        }
    }
}
```

## Cuidado com herança

Herança deve representar uma relação coerente de substituição.

Pergunte:

> Um objeto da subclasse pode ser usado com segurança onde a superclasse é esperada?

Se a resposta for não, talvez composição seja melhor.

<hr>

# Etapa 5 - Interfaces e contratos

## Ideia central

Interface define um contrato.

Ela diz o que uma classe precisa oferecer, sem dizer exatamente como isso será feito.

Isso será muito importante nas aulas de padrões.

## Exemplo 6 - Interface

> **Cole em `Main.java` e execute.**

```java
interface Notificador {
    void enviar(String destino, String mensagem);
}

class NotificadorEmail implements Notificador {
    @Override
    public void enviar(String destino, String mensagem) {
        System.out.println("E-mail para " + destino + ": " + mensagem);
    }
}

class NotificadorSms implements Notificador {
    @Override
    public void enviar(String destino, String mensagem) {
        System.out.println("SMS para " + destino + ": " + mensagem);
    }
}

class ServicoAviso {
    private Notificador notificador;

    ServicoAviso(Notificador notificador) {
        this.notificador = notificador;
    }

    void avisar(String destino) {
        notificador.enviar(destino, "Sua solicitacao foi recebida.");
    }
}

public class Main {
    public static void main(String[] args) {
        ServicoAviso avisoPorEmail = new ServicoAviso(new NotificadorEmail());
        avisoPorEmail.avisar("ana@exemplo.com");

        ServicoAviso avisoPorSms = new ServicoAviso(new NotificadorSms());
        avisoPorSms.avisar("5551999999999");
    }
}
```

## Exercício 3 - Interface de pagamento

Crie:

- uma interface `FormaPagamento` com método `pagar`;
- uma classe `PagamentoPix`;
- uma classe `PagamentoCartao`;
- uma classe `Checkout` que recebe uma `FormaPagamento` no construtor;
- um `main` testando as duas formas de pagamento.

## Solução do exercício 3

> **Cole em `Main.java` e execute.**

```java
interface FormaPagamento {
    void pagar(double valor);
}

class PagamentoPix implements FormaPagamento {
    @Override
    public void pagar(double valor) {
        System.out.println("Pagamento via PIX: R$ " + valor);
    }
}

class PagamentoCartao implements FormaPagamento {
    @Override
    public void pagar(double valor) {
        System.out.println("Pagamento via cartao: R$ " + valor);
    }
}

class Checkout {
    private FormaPagamento formaPagamento;

    Checkout(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    void finalizarCompra(double valor) {
        formaPagamento.pagar(valor);
    }
}

public class Main {
    public static void main(String[] args) {
        Checkout checkoutPix = new Checkout(new PagamentoPix());
        checkoutPix.finalizarCompra(150.00);

        Checkout checkoutCartao = new Checkout(new PagamentoCartao());
        checkoutCartao.finalizarCompra(200.00);
    }
}
```

<hr>




# Etapa 6 - Prática complementar: `private`, `final` e `static`

## 6.1 Três decisões diferentes

Essas palavras podem aparecer juntas, mas cada uma responde a uma pergunta diferente:

| Modificador | Pergunta | Exemplos de uso |
|---|---|---|
| `private` | Quem pode acessar este membro? | Atributos, métodos, construtores e classes aninhadas |
| `final` | O que não poderá ser redefinido? | Variáveis, atributos, métodos e classes |
| `static` | Este membro pertence à classe? | Atributos, métodos e classes aninhadas estáticas |

Uma classe declarada diretamente no arquivo, fora de outra classe, **não pode ser `private` nem `static`**. Ela pode ser `final`. Para usar `private` ou `static` em uma classe, veremos uma classe declarada dentro de outra.

Também não declaramos um objeto como `private`, `final` ou `static` diretamente: declaramos atributos ou variáveis que podem guardar **referências** a objetos. Essa diferença será importante nos exemplos.

> Execute cada exemplo completo separadamente em `Main.java`. As linhas comentadas que indicam erro são propostas de investigação: descomente uma por vez para observar o erro de compilação.

## 6.2 `private` em atributos e métodos

Um atributo `private` impede o acesso direto por código externo à classe. Um método `private` representa uma operação interna, usada pela própria implementação. Classes aninhadas também podem acessar membros privados da classe que as contém.

Neste exemplo, quem usa o contador pede um incremento. A própria classe decide como validar a operação e alterar o valor.

```java
class Contador {
    private int valor;

    public void incrementar(int quantidade) {
        validarQuantidade(quantidade);
        valor = valor + quantidade;
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
    }

    public int getValor() {
        return valor;
    }
}

public class Main {
    public static void main(String[] args) {
        Contador contador = new Contador();
        contador.incrementar(3);

        int valorAtual = contador.getValor();
        System.out.println(valorAtual);

        // contador.valor = -10;             // Erro: atributo privado.
        // contador.validarQuantidade(2);    // Erro: método privado.
    }
}
```

**Saída:** `3`

### Acompanhe os passos

1. O `main` cria um contador. Seu atributo inteiro começa com o valor padrão `0`.
2. A chamada `incrementar(3)` entra no método público.
3. Esse método chama `validarQuantidade(3)` dentro da própria classe.
4. Como a quantidade é positiva, a execução continua.
5. O atributo recebe a soma do valor anterior com a quantidade.
6. `getValor()` devolve o estado atual, que é guardado em uma variável e impresso.

**Atenção:** `private` sozinho não valida dados. A proteção vem da combinação entre restringir o acesso e oferecer operações que respeitem as regras do objeto.

## Exercício 4 - Controlando um estoque

1. Crie `Estoque` com um atributo `private int quantidade`, inicialmente igual a zero.
2. Crie um método `private void validarEntrada(int valor)` que rejeite valores menores ou iguais a zero.
3. Crie um método público `adicionar(int valor)` que primeiro valide e depois atualize a quantidade.
4. Crie um método público para consultar a quantidade.
5. No `main`, adicione `5`, depois `2`, consulte e imprima o resultado.
6. Em uma execução separada, tente adicionar `0`.
7. Tente acessar diretamente o atributo e o método privado pelo `main`. Explique os erros e depois comente essas linhas.

**Resultado esperado:** as entradas válidas produzem `7`; a entrada zero lança uma exceção sem alterar o estoque. O acesso direto aos membros privados não compila.

## 6.3 `final` em valores e referências a objetos

Uma variável `final` só pode receber uma atribuição. Para um valor primitivo, como `int`, isso impede trocar o valor. Para uma referência, impede trocar o objeto para o qual ela aponta.

**Uma referência `final` não torna o objeto imutável.** Se o objeto oferece operações de alteração, elas continuam disponíveis.

```java
class Aluno {
    private final int matricula;
    private String nome;

    Aluno(int matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
    }

    public int getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void alterarNome(String novoNome) {
        nome = novoNome;
    }
}

public class Main {
    public static void main(String[] args) {
        final int limite = 30;
        final Aluno aluno = new Aluno(101, "Ana");

        aluno.alterarNome("Ana Silva");

        int matricula = aluno.getMatricula();
        String nomeAtual = aluno.getNome();

        System.out.println(limite);
        System.out.println(matricula);
        System.out.println(nomeAtual);

        // limite = 40;                         // Erro: nova atribuição.
        // aluno = new Aluno(102, "Bruno");      // Erro: troca de referência.
    }
}
```

**Saída:**

```text
30
101
Ana Silva
```

### Acompanhe os passos

1. `limite` recebe `30` e não poderá receber outro valor.
2. O construtor cria o aluno e atribui a matrícula uma única vez para esse objeto.
3. A variável local `aluno` guarda a referência ao objeto e não poderá ser reatribuída.
4. `alterarNome` modifica um atributo do objeto existente. Isso é permitido, pois `nome` não é `final`.
5. Os métodos de consulta devolvem os valores, que são guardados e impressos.

O atributo `private final int matricula` combina duas regras: não pode ser acessado diretamente de fora da classe e não pode ser reatribuído depois de inicializado. Outro aluno pode ter outra matrícula; cada objeto possui seu próprio atributo.

## Exercício 5 - Referência fixa e objeto mutável

1. Crie `Livro` com um código `private final int codigo` e um título `private String titulo`.
2. Inicialize os dois atributos no construtor.
3. Ofereça métodos para consultar os atributos e alterar apenas o título.
4. No `main`, declare `final Livro livro` e crie um livro.
5. Altere o título e imprima os dados.
6. Tente atribuir outro livro à mesma variável. Depois, tente reatribuir o código dentro de um método da classe.
7. Comente as linhas inválidas e explique por que alterar o título continua permitido.

**Resposta esperada:** o título pode mudar; a variável local não pode apontar para outro livro; o código não pode ser reatribuído. `final` na referência não congela o objeto.

## 6.4 `final` em métodos e classes

Em métodos, `final` impede a **sobrescrita por subclasses**. O método continua podendo ser chamado.

Em classes, `final` impede a criação de subclasses. A classe continua podendo ter objetos e atributos mutáveis.

```java
class Documento {
    public final void abrir() {
        System.out.println("Documento aberto.");
    }

    public void exibirTipo() {
        System.out.println("Documento comum.");
    }
}

final class DocumentoTexto extends Documento {
    @Override
    public void exibirTipo() {
        System.out.println("Documento de texto.");
    }

    // Não podemos declarar outro abrir() com a mesma assinatura aqui:
    // o método herdado é final.
}

// class DocumentoEspecial extends DocumentoTexto { } // Erro: classe final.

public class Main {
    public static void main(String[] args) {
        Documento documento = new DocumentoTexto();
        documento.abrir();
        documento.exibirTipo();
    }
}
```

**Saída:**

```text
Documento aberto.
Documento de texto.
```

O objeto executa `abrir()` herdado de `Documento`. Já `exibirTipo()` usa a implementação sobrescrita em `DocumentoTexto`. Declarar a subclasse como `final` impede novas subclasses, sem impedir que ela sobrescreva métodos não finais de sua superclasse.

## Exercício 6 - Limites da herança

1. Crie `Equipamento` com um método público `final void ligar()` que imprima uma mensagem.
2. Acrescente um método público `exibirDescricao()` sem `final`.
3. Crie `final class Impressora extends Equipamento`.
4. Sobrescreva somente `exibirDescricao()` e teste os dois métodos no `main`.
5. Tente sobrescrever `ligar()` e observe o erro.
6. Tente criar uma subclasse de `Impressora` e observe o erro.
7. Comente as tentativas inválidas antes de executar novamente.

**Resposta esperada:** um método `final` não pode ser sobrescrito; uma classe `final` não pode ser estendida. Nenhuma dessas declarações impede criar e usar uma impressora.

## 6.5 `static` em atributos e métodos

Um atributo de instância pertence a cada objeto. Um atributo `static` pertence à classe e é compartilhado pelas suas instâncias.

Um método `static` pode ser chamado pelo nome da classe, sem criar um objeto. Ele não tem `this` e não pode acessar diretamente atributos de instância: para isso, precisa de uma referência a um objeto.

```java
class Pedido {
    private static int totalCriados = 0;
    public static final int LIMITE_ITENS = 10;

    private final int numero;

    Pedido() {
        totalCriados = totalCriados + 1;
        numero = totalCriados;
    }

    public int getNumero() {
        return numero;
    }

    public static int getTotalCriados() {
        return totalCriados;
    }
}

public class Main {
    public static void main(String[] args) {
        int totalInicial = Pedido.getTotalCriados();
        System.out.println(totalInicial);

        Pedido primeiro = new Pedido();
        Pedido segundo = new Pedido();

        int numeroPrimeiro = primeiro.getNumero();
        int numeroSegundo = segundo.getNumero();
        int totalFinal = Pedido.getTotalCriados();
        int limite = Pedido.LIMITE_ITENS;

        System.out.println(numeroPrimeiro);
        System.out.println(numeroSegundo);
        System.out.println(totalFinal);
        System.out.println(limite);
    }
}
```

**Saída:**

```text
0
1
2
2
10
```

### Acompanhe os passos

1. Antes de criar pedidos, o contador da classe vale zero.
2. O primeiro construtor aumenta o contador para `1` e guarda esse valor no número do primeiro pedido.
3. O segundo construtor aumenta o mesmo contador para `2` e guarda esse valor no segundo pedido.
4. Cada pedido preserva seu próprio número, enquanto a classe mantém o total compartilhado.
5. `Pedido.getTotalCriados()` consulta o contador sem precisar de um pedido específico.
6. `Pedido.LIMITE_ITENS` consulta uma constante: `static` a associa à classe e `final` impede sua reatribuição.

`static` **não significa constante**: `totalCriados` muda a cada construção. Também não significa que só pode existir um objeto: o exemplo cria dois pedidos. O contador representa o total criado, não a quantidade de objetos ainda em uso. Este exemplo considera execução sequencial, sem criação simultânea por várias threads.

O método `main` também é `static`. Por isso, ele cria objetos antes de chamar os métodos de instância desses objetos.

## Exercício 7 - Matrículas e contador compartilhado

1. Crie `Participante` com um atributo de instância `private final int numero`.
2. Declare `private static int totalCriados = 0`.
3. No construtor, aumente o contador e depois atribua o resultado a `numero`.
4. Crie um método de instância para consultar o número.
5. Crie um método estático para consultar o total.
6. Declare `public static final int CAPACIDADE = 30` como informação sobre a capacidade da turma. Neste exercício, não é necessário implementar o controle de vagas.
7. No `main`, consulte o total inicial, crie três participantes e imprima seus números, o total final e a capacidade.

**Resultado esperado:** total inicial `0`; números `1`, `2` e `3`; total final `3`; capacidade `30`.

**Investigue:** o que acontece se você retirar `static` de `totalCriados`? Cada objeto passa a ter seu próprio contador e recebe o número `1`. Por que isso não atende ao requisito?

## 6.6 Construtor privado e classe aninhada estática

Um construtor `private` restringe a criação de objetos. Isso pode ser útil em uma classe utilitária que oferece apenas métodos estáticos.

Uma classe aninhada `static` não precisa de um objeto da classe externa para ser instanciada. Se também for `private`, ficará restrita à implementação que a contém. Seus objetos continuam tendo seus próprios atributos de instância.

```java
final class Conversor {
    private Conversor() {
    }

    public static int minutosParaSegundos(int minutos) {
        Validacao validacao = new Validacao();
        validacao.verificar(minutos);

        int segundos = minutos * 60;
        return segundos;
    }

    private static class Validacao {
        void verificar(int minutos) {
            if (minutos < 0) {
                throw new IllegalArgumentException("Minutos negativos.");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        int minutos = 2;
        int segundos = Conversor.minutosParaSegundos(minutos);
        System.out.println(segundos);

        // Conversor conversor = new Conversor(); // Erro: construtor privado.
        // Conversor.Validacao teste;             // Erro: classe privada.
    }
}
```

**Saída:** `120`

1. O `main` chama o método estático pelo nome `Conversor`.
2. O método cria um objeto da classe aninhada `Validacao`.
3. A validação rejeita valores negativos.
4. Para o valor `2`, o método calcula os segundos e devolve `120`.
5. O `main` guarda e imprime o resultado.

Aqui, a classe aninhada serve para demonstrar os modificadores. Para uma validação tão curta, um método privado na própria classe seria suficiente. O exemplo trabalha com valores pequenos, cujo resultado cabe em `int`.

## Exercício 8 - Classe utilitária

1. Crie `final class CalculadoraTempo`.
2. Declare um construtor privado.
3. Declare `public static final int MINUTOS_POR_HORA = 60`.
4. Crie um método público estático `horasParaMinutos(int horas)`.
5. Separe a validação em um método `private static void validarHoras(int horas)` que rejeite valores negativos.
6. No método de conversão, chame a validação, guarde o cálculo em uma variável e depois retorne o resultado.
7. No `main`, converta `3` horas e imprima o resultado, sem criar uma calculadora.
8. Explique por que o método de validação também pode ser estático.

**Resultado esperado:** `180`. O método de validação usa apenas o parâmetro recebido, sem depender do estado de um objeto.

## 6.7 Revisão dos modificadores

| Declaração | Efeito |
|---|---|
| `private int saldo;` | Restringe o acesso direto ao atributo |
| `private void validar()` | Restringe o acesso à operação interna |
| `private Classe()` | Restringe chamadas ao construtor |
| `final int limite = 10;` | Impede uma segunda atribuição à variável |
| `final Aluno aluno = ...;` | Impede trocar a referência; o objeto pode continuar mutável |
| `final void executar()` | Impede sobrescrita do método |
| `final class Classe` | Impede subclasses |
| `static int contador;` | Mantém o atributo na classe, compartilhado pelas instâncias |
| `static int consultar()` | Permite chamar o método sem uma instância da classe |
| `static final int LIMITE = 10;` | Define uma constante inteira associada à classe |
| `private static class Auxiliar` | Declara uma classe aninhada privada sem vínculo obrigatório com uma instância externa |

Antes de escolher um modificador, explique a intenção: **restringir acesso, impedir reatribuição ou sobrescrita, impedir herança, ou associar um membro à classe**.
