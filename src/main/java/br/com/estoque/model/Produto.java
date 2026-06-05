package br.com.estoque.model;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private double custo;
    private int validade;
    private int estoque;
    private int tempoDeReposicaoEmDias;
    private int consumoMedioDiario;

    private int estoqueDeSeguranca;
    private int pontoDePedido;

    public Produto(){

    }

    public Produto(int id, String nome, String categoria, double custo, int validade,
                   int estoque, int tempoDeReposicao, int consumoMedio) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.custo = custo;
        this.validade = validade;
        this.estoque = estoque;
        this.tempoDeReposicaoEmDias = tempoDeReposicao;
        this.consumoMedioDiario = consumoMedio;

        calcularEstoqueDeSeguranca();
        calcularPontoDePedido();
    }

    public void calcularEstoqueDeSeguranca(){
        this.estoqueDeSeguranca = this.consumoMedioDiario * this.tempoDeReposicaoEmDias;
    }

    public void calcularPontoDePedido(){
        this.pontoDePedido  = (this.consumoMedioDiario * this.tempoDeReposicaoEmDias) + this.estoqueDeSeguranca;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public int getTempoDeReposicaoEmDias() {
        return tempoDeReposicaoEmDias;
    }

    public void setTempoDeReposicaoEmDias(int tempoDeReposicaoEmDias) {
        this.tempoDeReposicaoEmDias = tempoDeReposicaoEmDias;
    }

    public int getConsumoMedioDiario() {
        return consumoMedioDiario;
    }

    public void setConsumoMedioDiario(int consumoMedioDiario) {
        this.consumoMedioDiario = consumoMedioDiario;
    }

    public int getEstoqueDeSeguranca() {return estoqueDeSeguranca;};
    public int getPontoDePedido() {return pontoDePedido;};

    @Override
    public String toString() {
        return id + ";" + nome + ";" + categoria + ";" + custo + ";" + validade + ";" +
                estoque + ";" + estoqueDeSeguranca + ";" + tempoDeReposicaoEmDias + ";" +
                consumoMedioDiario + ";" + pontoDePedido;
    }
}
