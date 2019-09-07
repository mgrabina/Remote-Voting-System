package ar.edu.itba.pod.server;

import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.models.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VotingSystemsHelper {
    private Map<String, Map<String, List<Vote>>> votes;
    private Map<String, String> tableProvinceMap;


    public VotingSystemsHelper() {
        this.votes = new ConcurrentHashMap<>();
        this.tableProvinceMap = new HashMap<>();
    }

    // Voting System helpers

    /**
     * Returns a Map with a percentage of votes for parties using AV
     */
    private Map<String, Double> calculateResultWithAV(List<Vote> votes){
        // TODO Implement
        return null;
    }

    /**
     * Returns a Map with a percentage of votes for parties using STV
     */
    private Map<String, Double> calculateResultWithSTV(List<Vote> votes){
        // TODO Implement
        return null;
    }

    /**
     * Returns a Map with a percentage of votes for parties using FPTP
     */
    private Map<String, Double> calculateResultWithFPTP(List<Vote> votes){
        // TODO Implement
        return null;
    }

    protected Map<String, Double> calculateNationalResults(){
        List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithAV(nationalVotes);
    }

    protected Map<String, Double> calculateProvinceResults(Optional<String> filter){
        if (!filter.isPresent()){
            throw new IllegalStateException("Filter not found.");
        }
        List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithSTV(provinceVotes);
    }

    protected Map<String, Double> calculateTableResults(Optional<String> filter){
        if (!filter.isPresent()){
            throw new IllegalStateException("Filter not found.");
        }
        String province = tableProvinceMap.get(filter.get());
        List<Vote> tableVotes = this.votes.get(province).get(filter.get());
        return calculateResultWithFPTP(tableVotes);
    }

    protected Map<String, Double> calculatePartialResults(VotingDimension dimension, Optional<String> filter){
        switch (dimension){
            case NATIONAL:
                List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                        flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTP(nationalVotes);
            case PROVINCE:
                if (!filter.isPresent()){
                    throw new IllegalStateException("Filter not found.");
                }
                List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                        flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTP(provinceVotes);
            case TABLE:
                if (!filter.isPresent()){
                    throw new IllegalStateException("Filter not found.");
                }
                String province = tableProvinceMap.get(filter.get());
                List<Vote> tableVotes = this.votes.get(province).get(filter.get());
                return calculateResultWithFPTP(tableVotes);
            default: throw new IllegalStateException("Invalid Dimension.");
        }
    }


    // Getters

    public Map<String, String> getTableProvinceMap() {
        return tableProvinceMap;
    }

    public Map<String, Map<String, List<Vote>>> getVotes() {
        return votes;
    }
}
