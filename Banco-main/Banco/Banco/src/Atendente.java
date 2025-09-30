public class Atendente extends Thread {
    private final Fila fila;
    private final java.util.List<Cliente> atendidos;

    public Atendente(Fila fila, java.util.List<Cliente> atendidos, String nome) {
        super(nome);
        this.fila = fila;
        this.atendidos = atendidos;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Cliente cliente = fila.retirar();
            if (cliente == null) break;

            long inicio = System.currentTimeMillis();
            cliente.setInicioAtendimento(inicio);

            try {
                // O tempo já vem acelerado do App.java, não precisa dividir novamente
                Thread.sleep(cliente.getTempoAtendimento());
            } catch (InterruptedException e) { //só para não dar pau
                break;
            }

            long fim = System.currentTimeMillis();
            cliente.setFimAtendimento(fim);

            synchronized (atendidos) {
                atendidos.add(cliente);
            }
        }
    }
}
