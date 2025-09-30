import java.util.LinkedList;
import java.util.List;

public class Fila {
    private final List<Cliente> fila = new LinkedList<>();

    public synchronized void adicionar(Cliente client) {
        fila.add(client);
        notifyAll();
    }

    public synchronized Cliente retirar() {
        while (fila.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) { //também caso der pau
                return null;
            }
        }
        return fila.remove(0);
    }

    public synchronized boolean isVazia() {
        return fila.isEmpty();
    }
}
