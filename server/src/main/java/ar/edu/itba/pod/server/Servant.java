package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.constants.VotingSystems;
import ar.edu.itba.pod.models.Inspector;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.services.VotingService;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Servant extends UnicastRemoteObject implements AdministrationService, VotingService, QueryService, InspectionService {

    private ElectionsState electionsState;
    private HashMap<Inspector, InspectorCallback> callbaks;
    private List<Vote> votes;



    protected Servant() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
        this.callbaks = new HashMap<>();
        this.votes = new ArrayList<>();

    }

    @Override
    public void openElections() throws RemoteException, IllegalStateException {
        if (this.electionsState != ElectionsState.NON_INITIALIZED){
            throw new IllegalStateException("Elections currently running or finished.");
        }
        this.electionsState = ElectionsState.RUNNING;
    }

    @Override
    public void closeElections() throws RemoteException, IllegalStateException {
        if (this.electionsState != ElectionsState.RUNNING){
            throw new IllegalStateException("Elections not running.");
        }
        this.electionsState = ElectionsState.FINISHED;
    }

    @Override
    public ElectionsState getElectionsState() throws RemoteException {
        return this.electionsState;
    }

    @Override
    public void registerInspector(String table, String party, InspectorCallback callback) throws RemoteException, IllegalStateException {
        if (this.getElectionsState() != ElectionsState.NON_INITIALIZED){
            throw new IllegalStateException("Elections already started or finished.");
        }
        this.callbaks.put(new Inspector(table, party), callback);
    }

    private void alertInspector(Vote vote) throws RemoteException {
        callbaks.entrySet().parallelStream()
                .filter(e -> e.getKey().getTable().equals(vote.getTable()))
                .filter(e -> e.getKey().getParty().equals(vote.getFirstSelection()) ||
                        vote.getSecondSelection().map(e.getKey().getParty()::equals).orElse(Boolean.FALSE) ||
                        vote.getThirdSelection().map(e.getKey().getParty()::equals).orElse(Boolean.FALSE)
                ).forEach(e -> {
            try {
                e.getValue().inspect();
            } catch (RemoteException ex) {
                // LOG ERROR
            }
        });
    }

    @Override
    public Map<String, Double> getResults(VotingDimension dimension, Optional<String> filter) throws RemoteException {

        List<Vote> votes = this.votes;

        switch (this.getElectionsState()){
            case NON_INITIALIZED: throw new IllegalStateException("Elections didn't started yet.");
            case RUNNING: return calculatPartialResults(votes, filter);
            case FINISHED:
                switch (dimension){
                    case NATIONAL: return calculateNationalResults(votes, filter);
                    case PROVINCE: return calculateProvinceResults(votes, filter);
                    case TABLE: return calculateTableResults(votes, filter);
                    default: throw new IllegalStateException("Invalid Dimension.");
                }
            default: throw new IllegalStateException("Invalid Election State.");
        }
    }


    private Map<String, Double> calculateNationalResults(List<Vote> votes, Optional<String> filter){

        if (filter.isPresent()){
            throw new IllegalArgumentException("Unnecessary argument for National Voting.");
        }

        //HashMap<String, Double> hs = new HashSet();

        //AV
        return null;
    }

    private Map<String, Double> calculateProvinceResults(List<Vote> votes, Optional<String> filter){
        //STV
        return null;
    }

    private Map<String, Double> calculateTableResults(List<Vote> votes, Optional<String> filter){

        return null;
    }

    private Map<String, Double> calculatPartialResults(List<Vote> votes, Optional<String> filter){
        //votes.
        return null;
    }


    private Map<String, Double> calculateResults(VotingDimension dimension, VotingSystems system, Optional<String> filter){

        if (dimension == VotingDimension.NATIONAL && filter.isPresent()){
            throw new IllegalArgumentException("Unnecessary argument for National Voting.");
        }

        // Calculate results

        return null;
    }

    @Override
    public void vote(Vote vote) throws RemoteException {
        if (this.getElectionsState() != ElectionsState.RUNNING){
            throw new IllegalStateException("There aren't elections running.");
        }
        this.votes.add(vote);
        this.alertInspector(vote);
    }
}
