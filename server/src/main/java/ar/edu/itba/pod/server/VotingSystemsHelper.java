package ar.edu.itba.pod.server;

import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.models.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VotingSystemsHelper {
    private final Map<String, Map<String, List<Vote>>> votes = Collections.unmodifiableMap(new HashMap<String, Map<String, List<Vote>>>()
            {
                    {
                        put("JUNGLE",new ConcurrentHashMap<>());
                        put("SAVANNAH",new ConcurrentHashMap<>());
                        put("TUNDRA",new ConcurrentHashMap<>());
                    }
            }
    );
    private Map<String, String> tableProvinceMap;
    private final Set<String> parties = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("GORILLA","LEOPARD","TURTLE","OWL","TIGER","TARSIER","MONKEY","LYNX",
                                                                                                "WHITE_TIGER","WHITE_GORILLA","SNAKE","JACKALOPE","BUFFALO")));
    private final double MAJORITY = 0.5;

    public VotingSystemsHelper() {
        this.tableProvinceMap = new HashMap<>();

    }

    // Voting System helpers

    /**
     * Returns a Map with a percentage of votes for parties using AV
     */
    private Map<String, Double> calculateResultWithAV(List<Vote> votes, Set<String> currentParties){
        int votesQuantity = votes.size();
        // Calculates current rankings
        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, currentParties);
        if (ranking.isEmpty()){
            return Collections.emptyMap();
        } else if ((double)ranking.get(0).getValue()/(double)votesQuantity >= MAJORITY){
            // There is a majority
            return ranking.stream().map(entry ->
                    new AbstractMap.SimpleEntry<>(entry.getKey(), (double)entry.getValue()/(double)votesQuantity))
                    .collect(Collectors.toMap(AbstractMap.Entry::getKey, AbstractMap.Entry::getValue));
        }
        // There isn't a majority -> Need to remove the last party and distribute its votes.
        currentParties.remove(ranking.get(ranking.size() - 1).getKey());
        return calculateResultWithAV(votes, currentParties);
    }

    private List<Map.Entry<String,Long>> getCurrentRanking(List<Vote> votes, Set<String> currentParties){
        return votes.parallelStream().map(vote -> {
            if (currentParties.contains(vote.getFirstSelection())){
                return vote.getFirstSelection();
            } else if (vote.getSecondSelection().isPresent() && currentParties.contains(vote.getSecondSelection().get())){
                return vote.getSecondSelection().get();
            } else if (vote.getThirdSelection().isPresent() && currentParties.contains(vote.getThirdSelection().get())){
                return vote.getThirdSelection().get();
            } else {
                return null;
            }
        }).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().
                sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
    }


    /**
     * Returns a Map with a percentage of votes for parties using AV
     */
    private Map<String, Double> calculateResultWithSTV(List<Vote> votes, Set<String> currentParties){
        int totalVotes = votes.size();
        // Calculates current rankings
        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, currentParties);
        if (ranking.isEmpty()){
            return Collections.emptyMap();
        }

        // find first if above limit and discard last vote option for this party
        for(Map.Entry<String, Long> rank: ranking) {
            if (rank.getValue()>totalVotes*MAJORITY) {
                getVotesForSelectedParty(votes,rank.getKey(), currentParties)
                        .parallelStream().reduce((first,second)-> second).get().cancelNextOption();
                return calculateResultWithSTV(votes, currentParties);
            }
        }

        // if nobody above majority and have arribed at required number of winners then finish
        if(currentParties.size()<=5)
            return ranking.stream().map(entry ->
                    new AbstractMap.SimpleEntry<>(entry.getKey(), (double)entry.getValue()/(double)totalVotes))
                    .collect(Collectors.toMap(AbstractMap.Entry::getKey, AbstractMap.Entry::getValue));


        // There isn't a majority -> Need to remove the last party and distribute its votes.
        currentParties.remove(ranking.get(ranking.size() - 1).getKey());
        return calculateResultWithAV(votes, currentParties);
    }

    /**
     * Return all votes for a selectedParty that is in currentParties *in voted order*
     * @param votes
     * @param selectedParty
     * @param currentParties
     * @return
     */
    private List<Vote> getVotesForSelectedParty(List<Vote> votes, String selectedParty, Set<String> currentParties){
        // TODO check if this list ALWAYS returns in the right order all items
        return votes.parallelStream().map(vote -> {
            if (currentParties.contains(vote.getFirstSelection()) && vote.getFirstSelection().equals(selectedParty)){
                return vote;
            } else if (vote.getSecondSelection().isPresent()
                    && currentParties.contains(vote.getSecondSelection().get())
                    && vote.getSecondSelection().get().equals(selectedParty)){
                return vote;
            } else if (vote.getThirdSelection().isPresent()
                    && currentParties.contains(vote.getThirdSelection().get())
                    && vote.getThirdSelection().get().equals(selectedParty)){
                return vote;
            } else {
                return null;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Returns a Map with a percentage of votes for parties using FPTP
     */
    private Map<String, Double> calculateResultWithFPTP(List<Vote> votes){
        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, parties);
        if (ranking.isEmpty()) return Collections.emptyMap();
        return Collections.singletonMap(ranking.get(0).getKey(), (double)ranking.get(0).getValue()/(double)votes.size());
    }

    protected Map<String, Double> calculateNationalResults(){
        List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithAV(nationalVotes, new HashSet<>(parties));
    }

    protected Map<String, Double> calculateProvinceResults(Optional<String> filter){
        if (!filter.isPresent()){
            throw new IllegalStateException("Filter not found.");
        }
        List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithSTV(provinceVotes, new HashSet<>(parties));
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

    public Set<String> getParties() {
        return parties;
    }
}
