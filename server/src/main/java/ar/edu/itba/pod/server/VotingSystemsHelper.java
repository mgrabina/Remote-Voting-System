package ar.edu.itba.pod.server;

import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.exceptions.IllegalActionException;
import ar.edu.itba.pod.models.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VotingSystemsHelper {

    private final int SEATS = 5;

    static final List<String> PARTIES = Collections.unmodifiableList(Arrays.asList("GORILLA","LEOPARD","TURTLE","OWL","TIGER","TARSIER","MONKEY","LYNX", "WHITE_TIGER","WHITE_GORILLA","SNAKE","JACKALOPE","BUFFALO"));

    private final Map<String, Map<String, List<Vote>>> votes = Collections.unmodifiableMap(new HashMap<String, Map<String, List<Vote>>>()
            {
                    {
                        put("JUNGLE",new HashMap<>());
                        put("SAVANNAH",new HashMap<>());
                        put("TUNDRA",new HashMap<>());
                    }
            }
    );
    private Map<String, String> tableProvinceMap;
    private final Set<String> parties = Collections.unmodifiableSet(new HashSet<>(PARTIES));


    private final double MAJORITY = 0.5;

    public VotingSystemsHelper() {
        this.tableProvinceMap = new HashMap<>();

    }

    // Voting System helpers

    /**
     * Returns a Map with a percentage of votes for parties using AV
     */
    private Map<String, Double> calculateResultWithAV(List<Vote> votes, Set<String> currentParties){
        // Calculates current rankings ordered by votes ascending
        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, currentParties);
        Long votesQuantity = ranking.stream().map(Map.Entry::getValue).reduce(Long::sum).orElse(0L);
        if (ranking.isEmpty()){
            return Collections.emptyMap();
        } else if ((double)ranking.get(ranking.size() - 1).getValue()/(double)votesQuantity >= MAJORITY){
            // There is a majority
            return Collections.singletonMap(ranking.get(ranking.size() - 1).getKey(),
                    (double)ranking.get(ranking.size() - 1).getValue()/(double)votesQuantity);
        }
        // There isn't a majority -> Need to remove the parties with the least quantity of votes.
        Long minimumVotesQuantity = ranking.stream().map(Map.Entry::getValue).reduce(Math::min).orElse(ranking.get(0).getValue());
        for (Map.Entry<String, Long> current : ranking){
            if (current.getValue().equals(minimumVotesQuantity)){
                currentParties.remove(current.getKey());
            } else {
                break;
            }
        }
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
        }).filter(Objects::nonNull).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().
                sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
    }


    private Map<String, List<Vote>> getDistribution(List<Vote> votes, String partyToDistribute, Set<String> availableParties){
        Map<String, List<Vote>> auxVotes = votes.parallelStream()
                .map(vote -> {
                    if (availableParties.contains(vote.getFirstSelection())){
                        return new AbstractMap.SimpleEntry<>(vote.getFirstSelection(), vote);
                    } else if (vote.getSecondSelection().isPresent() && availableParties.contains(vote.getSecondSelection().get())){
                        return new AbstractMap.SimpleEntry<>(vote.getSecondSelection().get(), vote);
                    } else if (vote.getThirdSelection().isPresent() && availableParties.contains(vote.getThirdSelection().get())){
                        return new AbstractMap.SimpleEntry<>(vote.getThirdSelection().get(), vote);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
        HashSet<String> auxAvailableParties = new HashSet<>(availableParties);
        auxAvailableParties.remove(partyToDistribute);
        return auxVotes.get(partyToDistribute).parallelStream()
                .map(vote -> {
                    if (auxAvailableParties.contains(vote.getFirstSelection())){
                        return new AbstractMap.SimpleEntry<>(vote.getFirstSelection(), vote);
                    } else if (vote.getSecondSelection().isPresent() && auxAvailableParties.contains(vote.getSecondSelection().get())){
                        return new AbstractMap.SimpleEntry<>(vote.getSecondSelection().get(), vote);
                    } else if (vote.getThirdSelection().isPresent() && auxAvailableParties.contains(vote.getThirdSelection().get())){
                        return new AbstractMap.SimpleEntry<>(vote.getThirdSelection().get(), vote);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }

    /**
     * Returns a Map with a percentage of votes for parties using STV
     */
    private Map<String, Double> calculateResultWithSTV(List<Vote> votes, Set<String> currentParties){
        // Calculates current rankings
        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, currentParties);
//        Long votesQuantity = ranking.stream().map(Map.Entry::getValue).reduce(Long::sum).orElse(0L);
        if (ranking.isEmpty()){
            return Collections.emptyMap();
        }

        Double mayorityRequired = Math.floor((double)votes.size()/(SEATS + 1)) + 1;

        // find first if above limit and discard last vote option for this party
        for(Map.Entry<String, Long> rank: ranking) {
            if (rank.getValue()>mayorityRequired) {
                getVotesForSelectedParty(votes,rank.getKey(), currentParties)
                        .parallelStream().reduce((first,second)-> second).get().cancelNextOption();
                return calculateResultWithSTV(votes, currentParties);
            }
        }

        // if nobody above majority and have arribed at required number of winners then finish
        if(currentParties.size()<=SEATS)
            return ranking.stream().map(entry ->
                    new AbstractMap.SimpleEntry<>(entry.getKey(), (double)entry.getValue()/(double)votes.size()))
                    .collect(Collectors.toMap(AbstractMap.Entry::getKey, AbstractMap.Entry::getValue));


        // There isn't a majority -> Need to remove the last party and distribute its votes.
        currentParties.remove(ranking.get(ranking.size() - 1).getKey());
        return calculateResultWithSTV(votes, currentParties);
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
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Returns a Map with a percentage of votes for parties using FPTP
     */
    private Map<String, Double> calculateResultWithFPTPResults(List<Vote> votes){
        return getCurrentRanking(votes, parties)
                .stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),(double)entry.getValue()/(double)votes.size()))
                .collect(Collectors.toMap(AbstractMap.Entry::getKey, AbstractMap.Entry::getValue));
    }

    protected Map<String, Double> calculateNationalResults(){
        List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithAV(nationalVotes, new HashSet<>(parties));
    }

    protected Map<String, Double> calculateProvinceResults(Optional<String> filter) throws IllegalActionException {
        if (!filter.isPresent()){
            throw new IllegalActionException("Filter not found.");
        }
        List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                flatMap(Collection::stream).collect(Collectors.toList());
        return calculateResultWithSTV(provinceVotes, new HashSet<>(parties));
    }

    protected Map<String, Double> calculateTableResults(Optional<String> filter) throws IllegalActionException {
        if (!filter.isPresent()){
            throw new IllegalActionException("Filter not found.");
        }
        String province = tableProvinceMap.get(filter.get());
        if (province == null){
            throw new IllegalActionException("Invalid table number.");
        }
        List<Vote> tableVotes = this.votes.get(province).get(filter.get());
        return calculateResultWithFPTPResults(tableVotes);
    }

    protected Map<String, Double> calculatePartialResults(VotingDimension dimension, Optional<String> filter) throws IllegalActionException {
        switch (dimension){
            case NATIONAL:
                List<Vote> nationalVotes = this.votes.values().stream().map(Map::values).
                        flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTPResults(nationalVotes);
            case PROVINCE:
                if (!filter.isPresent()){
                    throw new IllegalActionException("Filter not found.");
                }
                List<Vote> provinceVotes = this.votes.get(filter.get()).values().stream().
                        flatMap(Collection::stream).collect(Collectors.toList());
                return calculateResultWithFPTPResults(provinceVotes);
            case TABLE:
                if (!filter.isPresent()){
                    throw new IllegalActionException("Filter not found.");
                }
                String province = tableProvinceMap.get(filter.get());
                List<Vote> tableVotes;
                if (province == null){
                    tableVotes = Collections.emptyList();
                } else {
                    tableVotes = this.votes.get(province).get(filter.get());
                }
                return calculateResultWithFPTPResults(tableVotes);
            default: throw new IllegalActionException("Invalid Dimension.");
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
