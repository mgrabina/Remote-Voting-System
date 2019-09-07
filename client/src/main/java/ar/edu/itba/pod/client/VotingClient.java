package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.VotingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VotingClient {
    private static Logger logger = LoggerFactory.getLogger(VotingClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        VotingService votingService = (VotingService) Naming.lookup("//127.0.0.1/votingService");

        // Can Vote
        votingService.vote(new Vote("TABLE", "PROVINCE", new ArrayList<>()));
    }

}
