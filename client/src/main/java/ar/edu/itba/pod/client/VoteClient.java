package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.helpers.CSVhelper;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.VotingService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class VoteClient {
    private static Logger logger = LoggerFactory.getLogger(VoteClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

        CommandLine cmd = getOptions(args);
        String ip = "//" + cmd.getOptionValue("DserverAddress") + "/votingService";
        VotingService votingService;
        try {
            votingService = (VotingService) Naming.lookup(ip);
        }catch (Exception e){
            System.out.println("Bad ip");
            return;
        }

        CSVhelper.generateRandomData();

        String VOTE_FILE = cmd.getOptionValue("DvotesPath");

        int voteCount = CSVhelper.parseData(VOTE_FILE, ((table, province, firstVote, secondVote, thirdVote) -> {
            votingService.vote(new Vote(
                    table,
                    province,
                    firstVote,
                    secondVote,
                    thirdVote
            ));
        }));

        System.out.println(voteCount + " votes registered");
    }

    private static CommandLine getOptions(String[] args){

        Options options = new Options();

        Option ip = new Option("DserverAddress", "DserverAddress", true, "IP address of the server");
        ip.setRequired(true);
        options.addOption(ip);

        Option path = new Option("DvotesPath", "DvotesPath", true, "Votes file path");
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
