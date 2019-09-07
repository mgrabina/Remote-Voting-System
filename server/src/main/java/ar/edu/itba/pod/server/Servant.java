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
    private Map<String, Map<String, List<Vote>>> votes;
    private Map<String, String> tableProvinceMap;



    protected Servant() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
        this.callbaks = new HashMap<>();
        this.votes = new ConcurrentHashMap<>();
        this.tableProvinceMap = new HashMap<>();

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
            case RUNNING: return calculatePartialResults(dimension, filter);
            case FINISHED:
                switch (dimension){
                    case NATIONAL: return calculateNationalResults();
                    case PROVINCE: return calculateProvinceResults(filter);
                    case TABLE: return calculateTableResults(filter);
                    default: throw new IllegalStateException("Invalid Dimension.");
                }
            default: throw new IllegalStateException("Invalid Election State.");
        }
    }


    private Map<String, Double> calculateNationalResults(){
        List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithAV(nationalVotes);
    }

    private Map<String, Double> calculateProvinceResults(Optional<String> filter){
        if (!filter.isPresent()){
            throw new IllegalStateException("Filter not found.");
        }
        List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithSTV(provinceVotes);
    }

    private Map<String, Double> calculateTableResults(Optional<String> filter){
        if (!filter.isPresent()){
            throw new IllegalStateException("Filter not found.");
        }
        String province = tableProvinceMap.get(filter.get());
        List<Vote> tableVotes = this.votes.get(province).get(filter.get());
        return calculateResultWithFPTP(tableVotes);
    }

    private Map<String, Double> calculatePartialResults(VotingDimension dimension, Optional<String> filter){
        switch (dimension){
            case NATIONAL:
                List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                        flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTP(nationalVotes);
            case PROVINCE:
                List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                        flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTP(provinceVotes);
            case TABLE:
                String province = tableProvinceMap.get(filter.get());
                List<Vote> tableVotes = this.votes.get(province).get(filter.get());
                return calculateResultWithFPTP(tableVotes);
            default: throw new IllegalStateException("Invalid Dimension.");
        }
    }

    private Map<String, Double> calculateResultWithAV(List<Vote> votes){
        // TODO Implement
        return null;
    }
    private Map<String, Double> calculateResultWithSTV(List<Vote> votes){
        // TODO Implement
        return null;
    }
    private Map<String, Double> calculateResultWithFPTP(List<Vote> votes){
        // TODO Implement
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
        if (!votes.containsKey(vote.getProvince())){
            votes.put(vote.getProvince(), new ConcurrentHashMap<>());
            votes.get(vote.getProvince()).put(vote.getTable(), Collections.synchronizedList(new LinkedList<>()));
            tableProvinceMap.put(vote.getTable(), vote.getProvince());
        }
        votes.get(vote.getProvince()).get(vote.getTable()).add(vote);
        this.alertInspector(vote);
    }
}
