package ar.edu.itba.pod.server.serviceClients;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingSystems;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.services.VotingService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QueryServiceImpl extends UnicastRemoteObject implements QueryService {

    private AdministrationService administrationService;
    private VotingService votingService;


    public QueryServiceImpl() throws RemoteException, MalformedURLException, NotBoundException {
        super();
        this.administrationService = (AdministrationService) Naming.lookup(Constants.administrationServiceHost + "administrationService");
        this.votingService = (VotingService) Naming.lookup(Constants.votingServiceHost + "votingService");
    }

    @Override
    public Map<String, Double> getResults(VotingDimension dimension, Optional<String> filter) throws RemoteException {
        
    	List<Vote> votes = votingService.getVotes();
    	
    	switch (administrationService.getElectionsState()){
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


}
