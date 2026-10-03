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

# Fechamento

## O que você precisa levar para a Aula 1

A Aula 1 começa a discutir problemas de design e padrões de projeto.

Para acompanhar bem, é importante lembrar:

- classe define estrutura;
- objeto carrega estado e comportamento;
- encapsulamento protege o estado;
- composição organiza colaboração entre objetos;
- herança deve ser usada com cuidado;
- polimorfismo permite tratar objetos diferentes por um tipo comum;
- interface define contrato;
- depender de contratos facilita troca e teste.

## Perguntas de revisão

1. Qual é a diferença entre classe e objeto?
2. Por que atributos privados ajudam no encapsulamento?
3. O que é composição?
4. O que é polimorfismo?
5. Quando a herança pode ser uma má ideia?
6. Qual é a vantagem de uma classe depender de uma interface?

## Preparação para a próxima aula

Na próxima aula, vamos usar essas construções para discutir problemas de design e entender por que padrões de projeto existem.