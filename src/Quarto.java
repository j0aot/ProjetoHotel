public class Quarto {
    private int id;
    private int numero;
    private int capacidade;

    public Quarto(int id, int numero, int capacidade) {
        this.id = id;
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public int getCapacidade() { return capacidade; }
    
    // Método para formatar linha CSV
    public String toCSV() {
        return id + ";" + numero + ";" + capacidade;
    }

    @Override
    public String toString() {
        return "Quarto " + numero + " (Cap: " + capacidade + ")";
    }
}