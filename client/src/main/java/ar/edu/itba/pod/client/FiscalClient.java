package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.client.handlers.InspectorCallbackHandlerImpl;
import ar.edu.itba.pod.services.InspectionService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class FiscalClient {
    private static Logger logger = LoggerFactory.getLogger(FiscalClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        CommandLine cmd = getOptions(args);
        String ip = "//" + cmd.getOptionValue("DserverAddress") + "/inspectionService" ;
        InspectionService inspectionService;
        try {
            inspectionService = (InspectionService) Naming.lookup(ip);
        }catch (Exception e){
            System.out.println("Bad ip");
            return;
        }

        String table = cmd.getOptionValue("Did");
        String party = cmd.getOptionValue("Dparty");

        InspectorCallback inspectorCallback = new InspectorCallbackHandlerImpl(table, party);
        // TODO: handlear errores de eleccion ya empezada.
        inspectionService.registerInspector(table, party, inspectorCallback);
        System.out.println("Fiscal of " + party + " registered on polling place " + table);
    }

    private static CommandLine getOptions(String[] args){

        Options options = new Options();

        Option ip = new Option("DserverAddress", "DserverAddress", true, "IP address of the server");
        ip.setRequired(true);
        options.addOption(ip);

        Option table = new Option("Did", "Did", true, "Table id");
        table.setRequired(true);
        options.addOption(table);

        Option party = new Option("Dparty", "Dparty", true, "Party name");
        party.setRequired(true);
        options.addOption(party);

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
