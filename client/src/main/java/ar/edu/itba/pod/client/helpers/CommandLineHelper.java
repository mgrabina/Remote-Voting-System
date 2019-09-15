package ar.edu.itba.pod.client.helpers;

import org.apache.commons.cli.*;

public class CommandLineHelper {

    public static CommandLine generateCommandLineParser(Options options, String[] args){
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
