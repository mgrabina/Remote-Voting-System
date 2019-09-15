package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.helpers.CSVhelper;
import ar.edu.itba.pod.client.helpers.CommandLineHelper;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.exceptions.IllegalActionException;
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
import java.util.stream.Collectors;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) {

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

        try {
            if (state != null) {
                results = queryService.getResults(VotingDimension.PROVINCE, state);
            }
            if (table != null) {
                results = queryService.getResults(VotingDimension.TABLE, table);
            }
            if (state == null && table == null) {
                results = queryService.getResults(VotingDimension.NATIONAL, null);
            }
        } catch (RemoteException e){
            System.out.println("Could not connect to server.");
        } catch (IllegalActionException e) {
            System.out.println("Illegal Action: " + e.getMessage());
        }

        if (results.getValue() == ElectionsState.FINISHED){
            String winnerString = results.getKey().keySet().stream().collect(Collectors.joining(","));
            System.out.println(winnerString + " won the election");
        }

        String outFile = cmd.getOptionValue("DoutPath");
        CSVhelper.writeCsv(outFile, results.getKey());
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

        return CommandLineHelper.generateCommandLineParser(options, args);
    }


}
