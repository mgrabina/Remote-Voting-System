package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.services.VotingService;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class Servant extends UnicastRemoteObject implements AdministrationService, VotingService, QueryService, InspectionService {

    private ElectionsState electionsState;
    private HashMap<String, HashMap<String, List<InspectorCallback>>> callbacks;
    private VotingSystemsHelper votingSystemsHelper;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10); // TODO: Why not CachedPool?

    protected Servant() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
        this.callbacks = new HashMap<>();
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

        if (!this.callbacks.containsKey(table)){
            this.callbacks.put(table, new HashMap<>());
        }

        HashMap<String, List<InspectorCallback>> partyMap = this.callbacks.get(table);

        if (partyMap.containsKey(party))
            partyMap.get(party).add(callback);
        else
            partyMap.put(party, Arrays.asList(callback));

    }

    @Override
    public void alertInspector(Vote vote) throws RemoteException{

        CompletableFuture.runAsync(()->{
            if (callbacks.containsKey(vote.getTable())){
                HashMap<String, List<InspectorCallback>> partyMap = callbacks.get(vote.getTable());
                sendAlert(partyMap, vote.getFirstSelection());
                if(vote.getSecondSelection().isPresent())
                    sendAlert(partyMap, vote.getSecondSelection().get());
                if(vote.getThirdSelection().isPresent())
                    sendAlert(partyMap, vote.getThirdSelection().get());
            }
        }, threadPool);
    }

    private void sendAlert(HashMap<String, List<InspectorCallback>> partyMap, String party){

        if (party == null) return;

        if (partyMap.containsKey(party)){
            partyMap.get(party).forEach( c -> {
                try {
                    c.inspect();
                } catch (RemoteException e) {
                    System.out.println(e.getCause());
                }
            });
        }
    }

    @Override
    public Pair<Map<String, Double>, ElectionsState> getResults(VotingDimension dimension, String filter) throws RemoteException {
        switch (this.getElectionsState()){
            case NON_INITIALIZED: throw new IllegalStateException("Elections didn't started yet.");
            case RUNNING: return new Pair<>(this.votingSystemsHelper.calculatePartialResults(dimension, Optional.ofNullable(filter)), getElectionsState());
            case FINISHED:
                switch (dimension){
                    case NATIONAL: return new Pair<>(this.votingSystemsHelper.calculateNationalResults(), getElectionsState());
                    case PROVINCE: return new Pair<>(this.votingSystemsHelper.calculateProvinceResults(Optional.ofNullable(filter)), getElectionsState());
                    case TABLE: return new Pair<>(this.votingSystemsHelper.calculateTableResults(Optional.ofNullable(filter)), getElectionsState());
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
        // if table not exists
        if(!this.votingSystemsHelper.getVotes().get(vote.getProvince()).containsKey(vote.getTable())){
            this.votingSystemsHelper.getVotes().get(vote.getProvince()).put(vote.getTable(), Collections.synchronizedList(new LinkedList<>()));
            this.votingSystemsHelper.getTableProvinceMap().put(vote.getTable(), vote.getProvince());
        }
        this.votingSystemsHelper.getVotes().get(vote.getProvince()).get(vote.getTable()).add(vote);
        this.alertInspector(vote);
    }
}
