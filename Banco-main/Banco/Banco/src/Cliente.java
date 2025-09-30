public class Cliente {
    private final long chegada;
    private long inicioAtendimento;
    private long fimAtendimento;
    private final int tempoAtendimento;

    public Cliente(long chegada, int tempoAtendimento) {
        this.chegada = chegada;
        this.tempoAtendimento = tempoAtendimento;
    }

    public long getChegada() { return chegada; }
    public int getTempoAtendimento() { return tempoAtendimento; }
    public void setInicioAtendimento(long inicio) { this.inicioAtendimento = inicio; }
    public void setFimAtendimento(long fim) { this.fimAtendimento = fim; }
    public long getInicioAtendimento() { return inicioAtendimento; }
    public long getFimAtendimento() { return fimAtendimento; }
}
