package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.services.QueryService;
import javafx.util.Pair;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Optional;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

        CommandLine cmd = getOptions(args);
        String ip = "//" + cmd.getOptionValue("DserverAddress") + "/queryService";
        QueryService queryService;
        try {
            queryService = (QueryService) Naming.lookup(ip);
        }catch (Exception e){
            System.out.println("Bad ip");
            return;
        }

        String state = cmd.getOptionValue("Dstate");
        String table = cmd.getOptionValue("Did");
        if(state!=null && table!= null){
            System.out.println("Bad input. Please select state or table, not both.");
            return;
        }
        Pair<Map<String, Double>, ElectionsState> results = null;
        if(state!= null){
             results = queryService.getResults(VotingDimension.PROVINCE, state);
        }
        if(table!= null){
            results = queryService.getResults(VotingDimension.TABLE, table);
        }
        if(state == null && table == null){
            results = queryService.getResults(VotingDimension.NATIONAL, null);
        }
        // TODO: mandarlo a csv RESULTADOS
        if (results.getValue() == ElectionsState.FINISHED){
            String[] winners = (String[]) results.getKey().keySet().toArray();
            for (int i = 0 ; i < winners.length ; i++) {
                if (i != winners.length - 1){
                    System.out.print(winners[i] + ", ");
                }else{
                    System.out.print(winners[i]);
                }
            }
            System.out.println(" won the election");
        }
    }

    private static CommandLine getOptions(String[] args){

        Options options = new Options();

        Option ip = new Option("DserverAddress", "DserverAddress", true, "IP address of the server");
        ip.setRequired(true);
        options.addOption(ip);

        Option table = new Option("Did", "Did", true, "Table id");
        table.setRequired(false);
        options.addOption(table);

        Option state = new Option("Dstate", "Dstate", true, "State name");
        state.setRequired(false);
        options.addOption(state);

        Option path = new Option("DoutPath", "DoutPath", true, "Path to elections results");
        path.setRequired(true);
        options.addOption(path);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd=null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
        return cmd;
    }


}
