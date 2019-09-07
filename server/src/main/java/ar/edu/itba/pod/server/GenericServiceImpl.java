package ar.edu.itba.pod.server;

import ar.edu.itba.pod.GenericService;

import javax.swing.text.html.Option;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Basic implementation of {@link GenericService}.
 */
public class GenericServiceImpl extends UnicastRemoteObject implements GenericService {

    private final AtomicInteger visitCounter;
    private final Queue<String> queue;

    public GenericServiceImpl() throws RemoteException {
        super();
//        UnicastRemoteObject.exportObject(this, 1099);
        visitCounter = new AtomicInteger(0);
        queue = new LinkedList<>();
    }

    @Override
    public String echo(String message) {
        return message;
    }

    @Override
    public String toUpper(String message) {
        return Optional.ofNullable(message).map(String::toUpperCase).orElse(null);
    }

    @Override
    public void addVisit() {
        synchronized (visitCounter){
            visitCounter.incrementAndGet();
        }
    }

    @Override
    public int getVisitCount() {
        synchronized (visitCounter){
            return visitCounter.get();
        }
    }

    @Override
    public boolean isServiceQueueEmpty() {
        synchronized (queue){
            return queue.isEmpty();
        }
    }

    @Override
    public void addToServiceQueue(String name) {
        Optional.ofNullable(name).orElseThrow(IllegalAccessError::new);
        synchronized (queue){
            queue.add(name);
        }
    }

    @Override
    public String getFirstInServiceQueue() {
        synchronized (queue){
            if (queue.isEmpty()) throw new IllegalStateException();
            return queue.poll();
        }
    }
}
