package ar.edu.itba.pod.server;

import ar.edu.itba.pod.User;
import ar.edu.itba.pod.UserAvailableCallbackHandler;
import ar.edu.itba.pod.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    private Queue<User> queue;
    private Queue<UserAvailableCallbackHandler> callbackHandlersQueue;

    public UserServiceImpl() throws RemoteException {
        this.queue = new LinkedList<>();
        this.callbackHandlersQueue = new LinkedList<>();
    }


    @Override
    public void offerUser(User user) throws RemoteException {
        if (!callbackHandlersQueue.isEmpty()){
            callbackHandlersQueue.poll().userAvailable(queue.poll());
            return ;
        }
        queue.offer(user);
    }

    @Override
    public Optional<User> pollUser(UserAvailableCallbackHandler callbackHandler) throws RemoteException {
        callbackHandlersQueue.offer(callbackHandler);
        if (!queue.isEmpty()){
            User user = queue.poll();
            if (callbackHandlersQueue.isEmpty()){
                callbackHandler.userAvailable(user);
            }else{
                callbackHandlersQueue.poll().userAvailable(queue.poll());
            }
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public int queueSize(){
        return queue.size();
    }
}
