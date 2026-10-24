class Configuracao {
    private static final Configuracao INSTANCIA = new Configuracao();
    private String ambiente = "desenvolvimento";

    private Configuracao() {
    }

    static Configuracao getInstancia() {
        return INSTANCIA;
    }

    String getAmbiente() {
        return ambiente;
    }

    void definirAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }
}

public class Main {
    public static void main(String[] args) {
        Configuracao primeira = Configuracao.getInstancia();
        Configuracao segunda = Configuracao.getInstancia();

        primeira.definirAmbiente("producao");
        boolean mesmaInstancia = primeira == segunda;
        System.out.println(mesmaInstancia);

        String ambienteAtual = segunda.getAmbiente();
        System.out.println(ambienteAtual);
    }
}
