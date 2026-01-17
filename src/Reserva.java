import java.time.LocalDate;

public class Reserva {
    private int id;
    private int idQuarto;
    private int idHospede;
    private int numeroHospedes;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativa;

    public Reserva(int id, int idQuarto, int idHospede, int numeroHospedes, LocalDate dataInicio, LocalDate dataFim, boolean ativa) {
        this.id = id;
        this.idQuarto = idQuarto;
        this.idHospede = idHospede;
        this.numeroHospedes = numeroHospedes;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativa = ativa;
    }

    // Getters e Setters necessários
    public int getId() { return id; }
    public int getIdQuarto() { return idQuarto; }
    public int getIdHospede() { return idHospede; }
    public int getNumeroHospedes() { return numeroHospedes; }
    public void setNumeroHospedes(int n) { this.numeroHospedes = n; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate d) { this.dataInicio = d; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate d) { this.dataFim = d; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public String toCSV() {
        return id + ";" + idQuarto + ";" + idHospede + ";" + numeroHospedes + ";" + dataInicio + ";" + dataFim + ";" + ativa;
    }

    @Override
    public String toString() {
        return "Reserva " + id + " | Quarto ID: " + idQuarto + " | Hóspede ID: " + idHospede + 
               " | " + dataInicio + " a " + dataFim + " | " + (ativa ? "Ativa" : "Cancelada");
    }
}