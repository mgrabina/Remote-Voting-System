package ar.edu.itba.pod.server;

import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.exceptions.IllegalActionException;
import ar.edu.itba.pod.models.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VotingSystemsHelper {

    private final int SEATS = 3;

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
    private Map<String, Double> calculateResultWithSTV(List<Vote> votes, Set<String> currentParties, Map<String,Double> winners, Map<String,Map.Entry<List<Vote>,Double>> transferable, double mayorityRequired, long total){

        if(winners.size() == SEATS)
            return winners;

        List<Map.Entry<String,Long>> ranking = getCurrentRanking(votes, currentParties);
        List<Map.Entry<String,Long>> realRanking =  new LinkedList<>(ranking);
        transferable.forEach( (party, transfer) -> {
            if(currentParties.contains(party)){
                boolean exists = false;
                for (Map.Entry<String, Long> entry: realRanking){
                    if(entry.getKey().equals(party)){
                        entry.setValue(transfer.getValue().longValue() + entry.getValue());
                        exists = true;
                    }
                }
                if(!exists)
                    realRanking.add(new AbstractMap.SimpleEntry<>(party,transfer.getValue().longValue()));
            }
        });

        if (realRanking.isEmpty()){
            return Collections.emptyMap();
        }

        Collections.sort(realRanking,Map.Entry.comparingByValue());

        Map.Entry<String,Long> rank = realRanking.get(realRanking.size() - 1);

        if (rank.getValue()>=mayorityRequired) {
                List<Vote> auxTotalVotes = new LinkedList<>();
                auxTotalVotes.addAll(votes);
                if(transferable.containsKey(rank.getKey()))
                    auxTotalVotes.addAll(transferable.get(rank.getKey()).getKey());
                Map<String, List<Vote>> currentVotes = getDistribution(auxTotalVotes,rank.getKey(),currentParties);
                currentVotes.entrySet().forEach( stringListEntry -> {
                    double realQuote = 0;
                    for (Map.Entry<String,Long> entry: ranking){
                        if(entry.getKey().equals(rank.getKey()))
                            realQuote = entry.getValue();
                    }
                    if(transferable.containsKey(rank.getKey()))
                        realQuote += transferable.get(rank.getKey()).getKey().size();
                    double quota = (new Double(stringListEntry.getValue().size()) / realQuote) * (rank.getValue().doubleValue() - mayorityRequired);
                    votes.removeAll(stringListEntry.getValue());
                    if(transferable.containsKey(stringListEntry.getKey())){
                        transferable.get(stringListEntry.getKey()).getKey().addAll(stringListEntry.getValue());
                        transferable.get(stringListEntry.getKey()).setValue(transferable.get(stringListEntry.getKey()).getValue() + quota);
                    }else {
                        transferable.put(stringListEntry.getKey(),new AbstractMap.SimpleEntry<>(stringListEntry.getValue(),quota));
                    }
            });
            currentParties.remove(rank.getKey());
            winners.put(rank.getKey(), mayorityRequired/total);
            return calculateResultWithSTV(votes, currentParties, winners, transferable, mayorityRequired, total);
        }

        //esta mal hay que agarrar los de party
        if(winners.size() + realRanking.size() == SEATS) {
            realRanking.forEach(p -> winners.put(p.getKey(), p.getValue().doubleValue()/total));
            return winners;
        }

        currentParties.remove(realRanking.get(0).getKey());
        return calculateResultWithSTV(votes, currentParties, winners, transferable, mayorityRequired, total);
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
        Double mayorityRequired = Math.floor((double)provinceVotes.size()/(SEATS + 1)) + 1;
        return calculateResultWithSTV(provinceVotes, new HashSet<>(parties), new HashMap<>(), new HashMap<>(), mayorityRequired, provinceVotes.size());
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
