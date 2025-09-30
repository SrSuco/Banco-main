import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=== SIMULAÇÃO DO BANCO FIRMEZA ===");
        System.out.println("Objetivo: Tempo médio de espera <= 2 minutos");
        System.out.println("Simulando 2 horas como 2 minutos (aceleração x60)\n");
        
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
            int intervaloChegadaAcelerado = intervaloChegadaReal / 60;

            int tempoAtendimentoReal = 30000 + rand.nextInt(90001);
            int tempoAtendimentoAcelerado = tempoAtendimentoReal / 60;

            Cliente c = new Cliente(System.currentTimeMillis(), tempoAtendimentoAcelerado);
            fila.adicionar(c);

            Thread.sleep(intervaloChegadaAcelerado);

            tempoDecorrido = System.currentTimeMillis() - inicioSimulacao;
        }

        while (!fila.isVazia()) {
            Thread.sleep(100);
        }

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

        System.out.println("===== RESULTADOS DA SIMULAÇÃO (tempo acelerado) =====");
        System.out.println("Número de atendentes utilizados: " + numAtendentes);
        System.out.println("Clientes atendidos: " + totalAtendidos);
        System.out.println("Tempo máximo de espera (ms): " + tempoMaxEspera);
        System.out.println("Tempo máximo de atendimento (ms): " + tempoMaxAtendimento);
        System.out.println("Tempo médio no banco (ms): " + tempoMedioBanco);
        System.out.println("Tempo médio de espera na fila (ms): " + tempoMedioEspera);
        if (tempoMedioEspera <= 2000) {
            System.out.println("OK Objetivo atingido: tempo médio de espera <= 2 minutos.");
        } else {
            System.out.println("X Objetivo NÃO atingido: tempo médio de espera > 2 minutos.");
        }

        System.out.println("\n===== RESULTADOS CONVERTIDOS PARA O MUNDO REAL =====");
        System.out.printf("Tempo máximo de espera: %.2f segundos\n", tempoMaxEspera * 60.0 / 1000.0);
        System.out.printf("Tempo máximo de atendimento: %.2f segundos\n", tempoMaxAtendimento * 60.0 / 1000.0);
        System.out.printf("Tempo médio no banco: %.2f segundos\n", tempoMedioBanco * 60.0 / 1000.0);
        System.out.printf("Tempo médio de espera na fila: %.2f segundos\n", tempoMedioEspera * 60.0 / 1000.0);
        
        double tempoMedioEsperaReal = tempoMedioEspera * 60.0 / 1000.0;
        if (tempoMedioEsperaReal <= 120.0) {
            System.out.printf("✓ Objetivo ATINGIDO: %.2f segundos <= 120 segundos\n", tempoMedioEsperaReal);
        } else {
            System.out.printf("✗ Objetivo NÃO atingido: %.2f segundos > 120 segundos\n", tempoMedioEsperaReal);
        }
    }
}
