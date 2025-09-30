import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=== SIMULAÇÃO DO BANCO FIRMEZA ===");
        System.out.println("Horário de pico: 11:00 às 13:00 (2 horas)");
        System.out.println("Objetivo: Tempo médio de espera <= 2 minutos (120 segundos)");
        System.out.println("Intervalo de chegada: 5-50 segundos");
        System.out.println("Tempo de atendimento: 30-120 segundos\n");
        
        System.out.println("Executando simulações com 1 a 15 atendentes...\n");
        
        for (int numAtendentes = 1; numAtendentes <= 15; numAtendentes++) {
            System.out.println("========================================");
            System.out.printf("SIMULAÇÃO COM %d ATENDENTE(S)\n", numAtendentes);
            System.out.println("========================================");
            
            executarSimulacao(numAtendentes);
            System.out.println();
        }
    }
    
    private static void executarSimulacao(int numAtendentes) throws Exception {
        Random rand = new Random();

        int tempoSimulacaoMs = 2 * 60 * 1000;

        Fila fila = new Fila();
        List<Cliente> atendidos = Collections.synchronizedList(new ArrayList<>());
        List<Atendente> atendentes = new ArrayList<>();

        for (int i = 0; i < numAtendentes; i++) {
            Atendente atend = new Atendente(fila, atendidos, "Atendente-" + (i+1));
            atendentes.add(atend);
            atend.start();
        }

        long inicioSimulacao = System.currentTimeMillis();
        long tempoDecorrido = 0;

        while (tempoDecorrido < tempoSimulacaoMs) {
            int intervaloChegadaReal = 5000 + rand.nextInt(45001);
            int intervaloChegadaSimulacao = intervaloChegadaReal / 60;
            intervaloChegadaSimulacao = Math.max(10, intervaloChegadaSimulacao);

            int tempoAtendimentoReal = 30000 + rand.nextInt(90001);
            int tempoAtendimentoSimulacao = tempoAtendimentoReal / 60;
            tempoAtendimentoSimulacao = Math.max(500, tempoAtendimentoSimulacao);

            Cliente c = new Cliente(System.currentTimeMillis(), tempoAtendimentoSimulacao);
            fila.adicionar(c);

            Thread.sleep(intervaloChegadaSimulacao);

            tempoDecorrido = System.currentTimeMillis() - inicioSimulacao;
        }

        Thread.sleep(5000);

        for (Atendente atend : atendentes) {
            atend.interrupt();
        }

        for (Atendente atend : atendentes) {
            atend.join();
        }

        int totalAtendidos = atendidos.size();
        long tempoMaxEspera = 0;
        long tempoMaxAtendimento = 0;
        long somaTempoBanco = 0;
        long somaTempoEspera = 0;

        for (Cliente client : atendidos) {
            long espera = client.getInicioAtendimento() - client.getChegada();
            long tempoBanco = client.getFimAtendimento() - client.getChegada();
            tempoMaxEspera = Math.max(tempoMaxEspera, espera);
            tempoMaxAtendimento = Math.max(tempoMaxAtendimento, client.getTempoAtendimento());
            somaTempoBanco += tempoBanco;
            somaTempoEspera += espera;
        }

        double tempoMedioBanco = totalAtendidos > 0 ? somaTempoBanco / (double) totalAtendidos : 0;
        double tempoMedioEspera = totalAtendidos > 0 ? somaTempoEspera / (double) totalAtendidos : 0;

        double tempoMaxEsperaReal = tempoMaxEspera * 60.0 / 1000.0;
        double tempoMaxAtendimentoReal = tempoMaxAtendimento * 60.0 / 1000.0;
        double tempoMedioBancoReal = tempoMedioBanco * 60.0 / 1000.0;
        double tempoMedioEsperaReal = tempoMedioEspera * 60.0 / 1000.0;

        System.out.println("===== DADOS ESTATÍSTICOS =====");
        System.out.println("Número de atendentes: " + numAtendentes);
        System.out.println("Clientes atendidos: " + totalAtendidos);
        System.out.printf("Tempo máximo de espera: %.1f segundos\n", tempoMaxEsperaReal);
        System.out.printf("Tempo máximo de atendimento: %.1f segundos\n", tempoMaxAtendimentoReal);
        System.out.printf("Tempo médio no banco: %.1f segundos\n", tempoMedioBancoReal);
        System.out.printf("Tempo médio de espera na fila: %.1f segundos\n", tempoMedioEsperaReal);
        
        if (tempoMedioEsperaReal <= 120.0) {
            System.out.printf("Ok OBJETIVO ATINGIDO: %.1f segundos <= 120 segundos\n", tempoMedioEsperaReal);
        } else {
            System.out.printf("X Objetivo NÃO atingido: %.1f segundos > 120 segundos\n", tempoMedioEsperaReal);
        }
    }
}
