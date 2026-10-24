# Padrões de Projeto de Software
## Aula 01 — Pensamento de design antes dos padrões

**Linguagem:** Java  
**Ideia central:** antes de escolher um padrão, é preciso reconhecer um problema, propor uma mudança e avaliar se ela compensa.

## Objetivos de aprendizagem

Ao concluir a aula, o estudante deverá conseguir:

- identificar responsabilidades e pontos de acoplamento em uma classe;
- distinguir o comportamento estável das partes que podem variar;
- melhorar um código gradualmente, sem aplicar padrões por obrigação;
- reconhecer interfaces, composição e polimorfismo em uma solução;
- conhecer a origem e os três grupos dos padrões GoF;
- justificar os benefícios e os custos de uma decisão de design.

## Roteiro da aula

| Etapa | Atividade | Questão orientadora |
|---|---|---|
| 1 | Diagnóstico de um problema | Por que um código que funciona pode ser difícil de manter? |
| 2 | Evolução guiada | Qual a menor mudança que ajuda de verdade? |
| 3 | Padrões e princípios | Como dar nome às soluções e avaliá-las? |
| 4 | Exercício | Quando mudar o design, e quando manter a simplicidade? |
| 5 | Síntese | O que aprendemos antes de estudar padrões específicos? |

> **Método da disciplina:** observar o problema → identificar o que varia → propor a menor melhoria útil → discutir custos → só então nomear um padrão.

---

# Etapa 1 — Identificando um problema de design

## 1.1 Uma central de notificações

Considere a seguinte classe Java:

```java
class CentralNotificacoes {
    void enviar(String canal, String destino, String mensagem) {
        if (canal.equals("EMAIL")) {
            System.out.println("SMTP -> " + destino + ": " + mensagem);
        } else if (canal.equals("SMS")) {
            System.out.println("SMS -> " + destino + ": " + mensagem);
        } else if (canal.equals("PUSH")) {
            System.out.println("PUSH -> " + destino + ": " + mensagem);
        } else {
            throw new IllegalArgumentException("Canal não reconhecido.");
        }
    }
}
```

**O código funciona.** Um `if` não é um problema por si só. Agora imagine que cada canal passe a exigir validação própria, integração com serviço externo, tratamento de falhas e tentativas de reenvio.

**Questões para discussão:**

1. Quantos motivos diferentes podem levar a uma alteração nessa classe?
2. Onde seria implementado um novo canal, como WhatsApp?
3. Como testar somente o envio por SMS?
4. Se o sistema continuar pequeno, essa implementação ainda pode ser aceitável?

## 1.2 Separando o estável do variável

| Mais estável | Variável |
|---|---|
| Solicitar o envio de uma mensagem | Como cada canal realiza o envio |
| Informar destino e mensagem | Validação do destinatário |
| Obter um resultado de envio | Integração e tratamento de falhas |

**Diagnóstico:** a mesma classe seleciona o canal e implementa detalhes dos canais. Se esses detalhes crescerem, ela passará a ter muitos motivos para mudar.

## 1.3 Primeira opção: não acrescentar abstrações

Uma melhoria imediata seria extrair métodos como `enviarEmail(...)` e `enviarSms(...)` na própria classe. A leitura melhora, porém a central continua responsável por todos os canais. Essa opção pode ser suficiente em um programa pequeno.

> **Decisão importante:** nem toda repetição ou condição justifica uma interface. Primeiro avalie a frequência e a complexidade das mudanças.

---

# Etapa 2 — Evoluindo o código somente quando necessário

## 2.1 Quando cada canal cresce, um contrato pode ajudar

Suponha que os canais tenham regras independentes e possam ser acrescentados ao longo do tempo. Podemos criar uma interface:

```java
interface CanalNotificacao {
    void enviar(String destino, String mensagem);
}
```

**Leitura:** qualquer classe que implemente `CanalNotificacao` deve oferecer o método `enviar(...)`.

Cada canal implementa esse contrato e a central deixa de conhecer seus detalhes. Para não repetir várias versões intermediárias, vamos diretamente ao exemplo executável.

## 2.2 Exemplo executável — interface e composição

Crie um arquivo `Main.java` e execute o código completo:

```java
interface CanalNotificacao {
    void enviar(String destino, String mensagem);
}

class NotificacaoEmail implements CanalNotificacao {
    @Override
    public void enviar(String destino, String mensagem) {
        System.out.println("SMTP -> " + destino + ": " + mensagem);
    }
}

class NotificacaoSms implements CanalNotificacao {
    @Override
    public void enviar(String destino, String mensagem) {
        System.out.println("SMS -> " + destino + ": " + mensagem);
    }
}

class CentralNotificacoes {
    private final CanalNotificacao canal;

    CentralNotificacoes(CanalNotificacao canal) {
        this.canal = canal;
    }

    void notificar(String destino, String mensagem) {
        canal.enviar(destino, mensagem);
    }
}

public class Main {
    public static void main(String[] args) {
        CentralNotificacoes email =
                new CentralNotificacoes(new NotificacaoEmail());
        CentralNotificacoes sms =
                new CentralNotificacoes(new NotificacaoSms());

        email.notificar("ana@exemplo.com", "Bem-vinda!");
        sms.notificar("11999999999", "Pedido confirmado.");
    }
}
```

**Saída esperada:**

```text
SMTP -> ana@exemplo.com: Bem-vinda!
SMS -> 11999999999: Pedido confirmado.
```

## 2.3 Entendendo o que mudou

1. `CanalNotificacao` representa a capacidade comum de enviar mensagens.
2. `NotificacaoEmail` e `NotificacaoSms` implementam essa capacidade de maneiras diferentes.
3. `CentralNotificacoes` recebe um objeto pronto no construtor: isso é **injeção de dependência**.
4. O campo `canal` tem tipo de interface: a central não depende de um canal específico.
5. `canal.enviar(...)` usa **polimorfismo**: o comportamento depende do objeto efetivamente recebido.
6. A central **usa** um canal em vez de **herdar** de um canal: isso é composição.

**E se precisarmos de WhatsApp?** Podemos criar `NotificacaoWhatsApp implements CanalNotificacao` e fornecê-la à central. Não é necessário editar `CentralNotificacoes`; o local onde as dependências são montadas deverá escolher o novo canal.

## 2.4 O que ganhamos e o que pagamos?

| Ganhos | Custos |
|---|---|
| Canais podem evoluir e ser testados isoladamente | Há uma interface e mais classes |
| A central não precisa conhecer implementações concretas | A execução tem uma chamada indireta |
| Novos canais podem ser introduzidos sem editar a central | Outra parte do programa deve escolher e construir o canal |

**Conclusão:** para canais com evolução independente, a solução pode valer a pena. Para dois `println` estáveis, a versão com `if` provavelmente é suficiente.

> O design se aproxima do padrão comportamental **Strategy**, que será aprofundado em outra aula. Aqui nosso foco é entender a motivação da mudança, não estudar esse padrão formalmente.

---

# Etapa 3 — O que são padrões de projeto?

## 3.1 Conceito

Um **padrão de projeto** descreve uma solução reutilizável para um problema recorrente de design de software. Não é uma implementação obrigatória nem uma receita pronta. Seu estudo considera **contexto, problema, estrutura da solução e consequências**.

Os padrões clássicos foram catalogados por Erich Gamma, Richard Helm, Ralph Johnson e John Vlissides no livro *Design Patterns: Elements of Reusable Object-Oriented Software*. Os autores ficaram conhecidos como **Gang of Four (GoF)**.

## 3.2 Três famílias de padrões GoF

| Grupo | Questão central | Exemplos |
|---|---|---|
| Criacionais | Como decidir ou organizar a criação de objetos? | Factory Method, Abstract Factory, Builder, Prototype, Singleton |
| Estruturais | Como compor classes e objetos? | Adapter, Decorator, Facade, Composite, Proxy |
| Comportamentais | Como distribuir e variar comportamentos? | Strategy, Observer, Command, State, Iterator |

Nesta aula os nomes são apenas um **mapa**. A próxima aula começa pelos criacionais.

## 3.3 Princípios que orientam as decisões

- **Responsabilidade:** uma classe deve ter um propósito compreensível e poucos motivos independentes para mudar.
- **Separar o que varia:** isolar a parte com evolução própria, quando isso simplifica a manutenção.
- **Depender de abstrações quando útil:** receber uma interface pode evitar dependência direta de classes concretas.
- **Preferir composição quando adequada:** montar objetos por colaboração costuma facilitar a troca de comportamentos; herança continua útil quando há uma relação de subtipo coerente.
- **Avaliar o custo:** novas classes e indireções só são justificadas pelos benefícios obtidos.

### SOLID: vocabulário inicial

| Sigla | Princípio | Ideia introdutória |
|---|---|---|
| S | Single Responsibility | Responsabilidade e motivo de mudança |
| O | Open/Closed | Estender comportamentos sem reescrever partes estáveis |
| L | Liskov Substitution | Subtipos devem respeitar o contrato esperado |
| I | Interface Segregation | Contratos específicos em vez de interfaces excessivas |
| D | Dependency Inversion | Componentes de alto nível não devem depender de detalhes concretos |

Não é necessário dominar todos os princípios nesta primeira aula; voltaremos a eles ao estudar os padrões.

---

# Etapa 4 — Exercício de diagnóstico e decisão

## 4.1 Situação: cálculo de frete

```java
import java.math.BigDecimal;

class CalculadoraFrete {
    BigDecimal calcular(String tipo, BigDecimal valorPedido) {
        if (tipo.equals("CORREIOS")) {
            return valorPedido.multiply(new BigDecimal("0.12"));
        } else if (tipo.equals("TRANSPORTADORA")) {
            return valorPedido.multiply(new BigDecimal("0.18"));
        } else if (tipo.equals("RETIRADA")) {
            return BigDecimal.ZERO;
        }
        throw new IllegalArgumentException("Tipo de frete inválido.");
    }
}
```

**Respondam:**

1. Qual é a responsabilidade da classe?
2. O que permanece estável e o que varia?
3. Quando manter o `if` seria uma decisão razoável?
4. O que mudaria se cada modalidade consultasse APIs externas, tabelas por CEP e regras de prazo?
5. Se fosse criada uma interface `RegraFrete`, quem escolheria a implementação?
6. Escrevam uma decisão de design com **problema, proposta, benefício, custo e conclusão**.

## 4.2 Correção comentada (referência)

**Problema:** hoje a escolha e as três fórmulas estão concentradas na calculadora. Isso não é necessariamente ruim.

**Proposta:** manter o `if` enquanto as regras forem estáveis e pequenas. Caso as modalidades ganhem integrações e regras independentes, considerar uma interface `RegraFrete`, implementações específicas e composição na calculadora.

**Benefícios:** testes por modalidade, isolamento de integrações e menos alterações na calculadora.

**Custos:** mais classes, configuração das dependências e necessidade de saber quem escolhe a implementação.

**Conclusão:** a decisão depende do cenário; a solução com mais classes não é automaticamente melhor.

> **Registro curto (modelo de decisão):** Contexto → Alternativas → Escolha → Benefícios → Custos → Condições para rever a escolha.

---

# Etapa 5 — Síntese e preparação para a Aula 2

## 5.1 O que fica desta aula

- Código funcional pode ter problemas de manutenção, mas simplicidade também é uma qualidade.
- Interfaces e polimorfismo podem ajudar a separar responsabilidades; também têm custos.
- Padrões de projeto são respostas a problemas recorrentes, não obrigações.
- Antes de introduzir abstrações, precisamos justificar **qual mudança provável** elas tornam mais fácil.

## 5.2 Perguntas de fechamento

1. Por que nem todo `if` deve ser substituído por classes?
2. Qual problema a interface resolveu na central de notificações?
3. O que foi ganho e que custo foi introduzido?
4. Quais são os três grupos de padrões GoF?
5. Que sinais justificam refatorar uma implementação simples?

## 5.3 Ponte para a Aula 2

Na solução de notificações, a central recebeu o canal já criado. Mas **quem deve escolher e construir esse objeto?** Quando essa decisão fica repetida ou complexa, podemos estudar outras alternativas para organizar a criação. Esse será o ponto de partida da **Aula 2 — Padrões Criacionais**.
