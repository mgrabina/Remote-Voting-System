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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Servant extends UnicastRemoteObject implements AdministrationService, VotingService, QueryService, InspectionService {

    private ElectionsState electionsState;
    private HashMap<Inspector, InspectorCallback> callbaks;
    private VotingSystemsHelper votingSystemsHelper;

    protected Servant() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
        this.callbaks = new HashMap<>();
        this.votingSystemsHelper = new VotingSystemsHelper();
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
        switch (this.getElectionsState()){
            case NON_INITIALIZED: throw new IllegalStateException("Elections didn't started yet.");
            case RUNNING: return this.votingSystemsHelper.calculatePartialResults(dimension, filter);
            case FINISHED:
                switch (dimension){
                    case NATIONAL: return this.votingSystemsHelper.calculateNationalResults();
                    case PROVINCE: return this.votingSystemsHelper.calculateProvinceResults(filter);
                    case TABLE: return this.votingSystemsHelper.calculateTableResults(filter);
                    default: throw new IllegalStateException("Invalid Dimension.");
                }
            default: throw new IllegalStateException("Invalid Election State.");
        }
    }

    @Override
    public void vote(Vote vote) throws RemoteException {
        if (this.getElectionsState() != ElectionsState.RUNNING){
            throw new IllegalStateException("There aren't elections running.");
        }
        if (!this.votingSystemsHelper.getVotes().containsKey(vote.getProvince())){
            this.votingSystemsHelper.getVotes().put(vote.getProvince(), new ConcurrentHashMap<>());
            this.votingSystemsHelper.getVotes().get(vote.getProvince()).put(vote.getTable(), Collections.synchronizedList(new LinkedList<>()));
            this.votingSystemsHelper.getTableProvinceMap().put(vote.getTable(), vote.getProvince());
            this.votingSystemsHelper.getParties().add(vote.getFirstSelection());
            vote.getSecondSelection().ifPresent(party -> this.votingSystemsHelper.getParties().add(party));
            vote.getThirdSelection().ifPresent(party -> this.votingSystemsHelper.getParties().add(party));
        }
        this.votingSystemsHelper.getVotes().get(vote.getProvince()).get(vote.getTable()).add(vote);
        this.alertInspector(vote);
    }
}
