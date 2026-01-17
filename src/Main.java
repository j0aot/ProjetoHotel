import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    // Limites Técnicos
    private static final int MAX_QUARTOS = 200;
    private static final int MAX_HOSPEDES = 1000;
    private static final int MAX_RESERVAS = 1000;

    // Arrays
    private static Quarto[] quartos = new Quarto[MAX_QUARTOS];
    private static Hospede[] hospedes = new Hospede[MAX_HOSPEDES];
    private static Reserva[] reservas = new Reserva[MAX_RESERVAS];

    // Contadores
    private static int qtdQuartos = 0;
    private static int qtdHospedes = 0;
    private static int qtdReservas = 0;

    // Utilitários
    private static Scanner scanner = new Scanner(System.in);
    private static int proximoIdHospede = 1;
    private static int proximoIdReserva = 1;

    public static void main(String[] args) {
        carregarDados();
        
        int opcao = -1;
        while (opcao != 0) {
            limparEcra();
            System.out.println("=== GESTÃO HOTELEIRA ===");
            System.out.println("1. Menu Quartos");
            System.out.println("2. Menu Hóspedes");
            System.out.println("3. Menu Reservas");
            System.out.println("0. Sair e Guardar");
            System.out.print("Opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1: menuQuartos(); break;
                case 2: menuHospedes(); break;
                case 3: menuReservas(); break;
                case 0: guardarDados(); System.out.println("A sair..."); break;
                default: System.out.println("Opção inválida."); pausar();
            }
        }
    }

    // --- MENUS ---

    private static void menuQuartos() {
        System.out.println("\n--- QUARTOS ---");
        System.out.println("1. Listar todos");
        System.out.println("2. Listar livres (hoje)");
        System.out.println("3. Listar ocupados (hoje)");
        System.out.println("4. Ver histórico de um quarto");
        System.out.print("Opção: ");
        String op = scanner.nextLine();

        switch (op) {
            case "1": 
                for (int i = 0; i < qtdQuartos; i++) {
                    boolean ocupado = estaOcupadoData(quartos[i].getId(), LocalDate.now());
                    System.out.println(quartos[i] + " [" + (ocupado ? "OCUPADO" : "LIVRE") + "]");
                }
                break;
            case "2":
                for (int i = 0; i < qtdQuartos; i++) {
                    if (!estaOcupadoData(quartos[i].getId(), LocalDate.now())) 
                        System.out.println(quartos[i]);
                }
                break;
            case "3":
                for (int i = 0; i < qtdQuartos; i++) {
                    if (estaOcupadoData(quartos[i].getId(), LocalDate.now())) {
                        System.out.println(quartos[i]);
                        // Mostrar reserva atual
                        for(int r=0; r<qtdReservas; r++){
                            if(reservas[r].getIdQuarto() == quartos[i].getId() && reservas[r].isAtiva() &&
                               !LocalDate.now().isBefore(reservas[r].getDataInicio()) && 
                               !LocalDate.now().isAfter(reservas[r].getDataFim())) {
                                System.out.println("   -> Ocupado por reserva ID: " + reservas[r].getId());
                            }
                        }
                    }
                }
                break;
            case "4":
                System.out.print("ID do Quarto: ");
                int idQ = lerInteiro();
                listarReservasPorFiltro(idQ, -1);
                break;
        }
        pausar();
    }

    private static void menuHospedes() {
        System.out.println("\n--- HÓSPEDES ---");
        System.out.println("1. Listar hóspedes");
        System.out.println("2. Criar hóspede");
        System.out.println("3. Procurar por documento");
        System.out.println("4. Editar hóspede");
        System.out.print("Opção: ");
        String op = scanner.nextLine();

        switch (op) {
            case "1":
                for (int i = 0; i < qtdHospedes; i++) System.out.println(hospedes[i]);
                break;
            case "2":
                criarHospede();
                break;
            case "3":
                System.out.print("Documento: ");
                String doc = scanner.nextLine();
                for (int i = 0; i < qtdHospedes; i++) {
                    if (hospedes[i].getDocumento().equalsIgnoreCase(doc)) System.out.println(hospedes[i]);
                }
                break;
            case "4":
                editarHospede();
                break;
        }
        pausar();
    }

    private static void menuReservas() {
        System.out.println("\n--- RESERVAS ---");
        System.out.println("1. Criar Reserva (Automática)");
        System.out.println("2. Listar todas");
        System.out.println("3. Listar por Hóspede");
        System.out.println("4. Editar Reserva");
        System.out.println("5. Cancelar Reserva");
        System.out.print("Opção: ");
        String op = scanner.nextLine();

        switch (op) {
            case "1": criarReserva(); break;
            case "2": for (int i = 0; i < qtdReservas; i++) System.out.println(reservas[i]); break;
            case "3": 
                System.out.print("ID do Hóspede: ");
                listarReservasPorFiltro(-1, lerInteiro());
                break;
            case "4": editarReserva(); break;
            case "5": cancelarReserva(); break;
        }
        pausar();
    }

    // --- LÓGICA DE NEGÓCIO ---

    private static void criarHospede() {
        if (qtdHospedes >= MAX_HOSPEDES) { System.out.println("Erro: Limite de hóspedes atingido."); return; }
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Documento: ");
        String doc = scanner.nextLine();

        if (nome.isEmpty() || doc.isEmpty()) { System.out.println("Dados inválidos."); return; }
        
        // Verificar duplicado
        for(int i=0; i<qtdHospedes; i++) {
            if(hospedes[i].getDocumento().equals(doc)) {
                System.out.println("Erro: Documento já existe."); return;
            }
        }

        hospedes[qtdHospedes++] = new Hospede(proximoIdHospede++, nome, doc);
        System.out.println("Hóspede criado com sucesso.");
    }

    private static void editarHospede() {
        System.out.print("ID do Hóspede a editar: ");
        int id = lerInteiro();
        Hospede h = null;
        for(int i=0; i<qtdHospedes; i++) { if(hospedes[i].getId() == id) h = hospedes[i]; }
        
        if (h == null) { System.out.println("Hóspede não encontrado."); return; }
        
        System.out.print("Novo Nome (" + h.getNome() + "): ");
        String nome = scanner.nextLine();
        if(!nome.isEmpty()) h.setNome(nome);
        
        System.out.print("Novo Documento (" + h.getDocumento() + "): ");
        String doc = scanner.nextLine();
        if(!doc.isEmpty()) h.setDocumento(doc); 
        
        System.out.println("Hóspede atualizado.");
    }

    private static void criarReserva() {
        if (qtdReservas >= MAX_RESERVAS) { System.out.println("Limite de reservas atingido."); return; }

        try {
            System.out.print("ID Hóspede: ");
            int idHospede = lerInteiro();
            // Validar existência hóspede
            boolean existe = false;
            for(int i=0; i<qtdHospedes; i++) if(hospedes[i].getId() == idHospede) existe = true;
            if(!existe) { System.out.println("Hóspede não existe."); return; }

            System.out.print("Nº Pessoas: ");
            int nPessoas = lerInteiro();
            System.out.print("Data Início (YYYY-MM-DD): ");
            LocalDate inicio = LocalDate.parse(scanner.nextLine());
            System.out.print("Data Fim (YYYY-MM-DD): ");
            LocalDate fim = LocalDate.parse(scanner.nextLine());

            if (inicio.isAfter(fim)) { System.out.println("Erro: Data início após data fim."); return; }

            // Algoritmo para encontrar quarto
            Quarto melhorQuarto = null;
            int menorDiferencaCapacidade = Integer.MAX_VALUE;

            for (int i = 0; i < qtdQuartos; i++) {
                Quarto q = quartos[i];
                if (q.getCapacidade() >= nPessoas) {
                    if (verificarDisponibilidadeQuarto(q.getId(), inicio, fim, -1)) {
                        int diff = q.getCapacidade() - nPessoas;
                        if (diff < menorDiferencaCapacidade) {
                            menorDiferencaCapacidade = diff;
                            melhorQuarto = q;
                        }
                    }
                }
            }

            if (melhorQuarto != null) {
                reservas[qtdReservas++] = new Reserva(proximoIdReserva++, melhorQuarto.getId(), idHospede, nPessoas, inicio, fim, true);
                System.out.println("Reserva criada com sucesso no Quarto " + melhorQuarto.getNumero());
            } else {
                System.out.println("Não há quartos disponíveis para estes critérios.");
            }

        } catch (Exception e) {
            System.out.println("Erro nos dados: " + e.getMessage());
        }
    }

    private static void editarReserva() {
        System.out.print("ID da Reserva: ");
        int id = lerInteiro();
        Reserva r = null;
        for(int i=0; i<qtdReservas; i++) if(reservas[i].getId() == id) r = reservas[i];

        if (r == null || !r.isAtiva()) { System.out.println("Reserva não encontrada ou inativa."); return; }

        try {
            System.out.print("Novo Nº Pessoas (" + r.getNumeroHospedes() + "): ");
            String npStr = scanner.nextLine();
            int novoNp = npStr.isEmpty() ? r.getNumeroHospedes() : Integer.parseInt(npStr);

            System.out.print("Nova Data Início (" + r.getDataInicio() + "): ");
            String iniStr = scanner.nextLine();
            LocalDate novoIni = iniStr.isEmpty() ? r.getDataInicio() : LocalDate.parse(iniStr);

            System.out.print("Nova Data Fim (" + r.getDataFim() + "): ");
            String fimStr = scanner.nextLine();
            LocalDate novoFim = fimStr.isEmpty() ? r.getDataFim() : LocalDate.parse(fimStr);

            // Validar capacidade do quarto atual
            Quarto q = null;
            for(int i=0; i<qtdQuartos; i++) if(quartos[i].getId() == r.getIdQuarto()) q = quartos[i];
            
            if(novoNp > q.getCapacidade()) { System.out.println("Capacidade do quarto insuficiente."); return; }
            
            // Validar sobreposição (excluindo a propria reserva)
            if(verificarDisponibilidadeQuarto(r.getIdQuarto(), novoIni, novoFim, r.getId())) {
                r.setNumeroHospedes(novoNp);
                r.setDataInicio(novoIni);
                r.setDataFim(novoFim);
                System.out.println("Reserva atualizada.");
            } else {
                System.out.println("Datas indisponíveis.");
            }

        } catch (Exception e) { System.out.println("Erro formato dados."); }
    }

    private static void cancelarReserva() {
        System.out.print("ID da Reserva: ");
        int id = lerInteiro();
        for(int i=0; i<qtdReservas; i++) {
            if(reservas[i].getId() == id) {
                reservas[i].setAtiva(false);
                System.out.println("Reserva cancelada.");
                return;
            }
        }
        System.out.println("Reserva não encontrada.");
    }

    // --- MÉTODOS AUXILIARES ---

    private static boolean verificarDisponibilidadeQuarto(int idQuarto, LocalDate inicio, LocalDate fim, int idReservaIgnorar) {
        for (int i = 0; i < qtdReservas; i++) {
            Reserva r = reservas[i];
            if (r.getIdQuarto() == idQuarto && r.isAtiva() && r.getId() != idReservaIgnorar) {
                // Se as datas se sobrepõem
                if (!inicio.isAfter(r.getDataFim()) && !fim.isBefore(r.getDataInicio())) {
                    return false; // Há conflito
                }
            }
        }
        return true;
    }
    
    private static boolean estaOcupadoData(int idQuarto, LocalDate data) {
        return !verificarDisponibilidadeQuarto(idQuarto, data, data, -1);
    }

    private static void listarReservasPorFiltro(int idQuarto, int idHospede) {
        for(int i=0; i<qtdReservas; i++) {
            boolean matchQuarto = (idQuarto == -1) || (reservas[i].getIdQuarto() == idQuarto);
            boolean matchHospede = (idHospede == -1) || (reservas[i].getIdHospede() == idHospede);
            
            if(matchQuarto && matchHospede) {
                System.out.println(reservas[i]);
            }
        }
    }

    private static int lerInteiro() {
        try { return Integer.parseInt(scanner.nextLine()); } catch (Exception e) { return -1; }
    }
    
    private static void pausar() {
        System.out.println("\nPressione Enter para continuar...");
        scanner.nextLine();
    }
    
    private static void limparEcra() {
        for(int i = 0; i < 50; i++) System.out.println();
    }

    // --- PERSISTÊNCIA (FICHEIROS) ---

    private static void carregarDados() {
        try {
            // Quartos
            File fq = new File("quartos.csv");
            if(fq.exists()) {
                Scanner sc = new Scanner(fq);
                while(sc.hasNextLine() && qtdQuartos < MAX_QUARTOS) {
                    String[] dados = sc.nextLine().split(";");
                    if(dados.length >= 3) {
                        quartos[qtdQuartos++] = new Quarto(Integer.parseInt(dados[0]), Integer.parseInt(dados[1]), Integer.parseInt(dados[2]));
                    }
                }
                sc.close();
            }

            // Hóspedes
            File fh = new File("hospedes.csv");
            if(fh.exists()) {
                Scanner sc = new Scanner(fh);
                while(sc.hasNextLine() && qtdHospedes < MAX_HOSPEDES) {
                    String[] dados = sc.nextLine().split(";");
                    if(dados.length >= 3) {
                        int id = Integer.parseInt(dados[0]);
                        hospedes[qtdHospedes++] = new Hospede(id, dados[1], dados[2]);
                        if(id >= proximoIdHospede) proximoIdHospede = id + 1;
                    }
                }
                sc.close();
            }

            // Reservas
            File fr = new File("reservas.csv");
            if(fr.exists()) {
                Scanner sc = new Scanner(fr);
                while(sc.hasNextLine() && qtdReservas < MAX_RESERVAS) {
                    String[] dados = sc.nextLine().split(";");
                    if(dados.length >= 7) {
                        int id = Integer.parseInt(dados[0]);
                        reservas[qtdReservas++] = new Reserva(
                            id, 
                            Integer.parseInt(dados[1]), 
                            Integer.parseInt(dados[2]), 
                            Integer.parseInt(dados[3]), 
                            LocalDate.parse(dados[4]), 
                            LocalDate.parse(dados[5]), 
                            Boolean.parseBoolean(dados[6])
                        );
                        if(id >= proximoIdReserva) proximoIdReserva = id + 1;
                    }
                }
                sc.close();
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar ficheiros: " + e.getMessage());
            pausar();
        }
    }

    private static void guardarDados() {
        try {
            // Guardar Hóspedes
            PrintWriter pw = new PrintWriter(new File("hospedes.csv"));
            for(int i=0; i<qtdHospedes; i++) pw.println(hospedes[i].toCSV());
            pw.close();

            // Guardar Reservas
            pw = new PrintWriter(new File("reservas.csv"));
            for(int i=0; i<qtdReservas; i++) pw.println(reservas[i].toCSV());
            pw.close();
            
        
            
        } catch (Exception e) {
            System.out.println("Erro ao guardar: " + e.getMessage());
        }
    }
}