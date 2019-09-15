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
    private Map<String, Map<String, List<InspectorCallback>>> callbacks;
    private VotingSystemsHelper votingSystemsHelper;
    private Object callbackLock = "callbackLock";
    private Object voteLock = "voteLock";
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    protected Servant() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
        this.callbacks = new HashMap<>();
        this.votingSystemsHelper = new VotingSystemsHelper();
    }

    ////////////////////////////////////////////////////// Management client //////////////////////////////////////////////////////////////////////////////////////

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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////// Fiscal client //////////////////////////////////////////////////////////////////////
    @Override
    public void registerInspector(String table, String party, InspectorCallback callback) throws RemoteException, IllegalStateException {

        if (this.getElectionsState() != ElectionsState.NON_INITIALIZED){
            throw new IllegalStateException("Elections already started or finished.");
        }

        if(!votingSystemsHelper.getParties().contains(party))
            throw new IllegalArgumentException("Party not registered");


        synchronized (callbackLock) {
            if (!this.callbacks.containsKey(table)) {
                this.callbacks.put(table, Collections.unmodifiableMap(new HashMap<String,List<InspectorCallback>>(){
                    {
                        VotingSystemsHelper.PARTIES.forEach(o ->
                            put(o, Collections.synchronizedList(new LinkedList<>())));
                    }
                }));

            }
        }

        this.callbacks.get(table).get(party).add(callback);
    }

    private void alertInspector(Vote vote) throws RemoteException{

        CompletableFuture.runAsync(()->{
            if (callbacks.containsKey(vote.getTable())){
                Map <String, List<InspectorCallback>> partyMap = callbacks.get(vote.getTable());
                sendAlert(partyMap, vote.getFirstSelection());
                if(vote.getSecondSelection().isPresent())
                    sendAlert(partyMap, vote.getSecondSelection().get());
                if(vote.getThirdSelection().isPresent())
                    sendAlert(partyMap, vote.getThirdSelection().get());
            }
        }, threadPool);
    }

    private void sendAlert(Map<String, List<InspectorCallback>> partyMap, String party){

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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////// Query client/////////////////////////////////////////////////////////////////////////////
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////// Vote client //////////////////////////////////////////////////////////////////////////

    @Override
    public void vote(Vote vote) throws RemoteException {

        if (this.getElectionsState() != ElectionsState.RUNNING){
            throw new IllegalStateException("There aren't elections running.");
        }
        // if table not exists
        synchronized (voteLock) {
            if (!this.votingSystemsHelper.getVotes().get(vote.getProvince()).containsKey(vote.getTable())) {
                this.votingSystemsHelper.getVotes().get(vote.getProvince()).put(vote.getTable(), Collections.synchronizedList(new LinkedList<>()));
                this.votingSystemsHelper.getTableProvinceMap().put(vote.getTable(), vote.getProvince());
            }
        }
        this.votingSystemsHelper.getVotes().get(vote.getProvince()).get(vote.getTable()).add(vote);
        this.alertInspector(vote);
    }
}
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////