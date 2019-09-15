package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.helpers.CommandLineHelper;
import ar.edu.itba.pod.exceptions.IllegalActionException;
import ar.edu.itba.pod.services.AdministrationService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagementClient {

    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);
    private enum ACTIONS { Open, Close, State};

    public static void main(String[] args) {

        CommandLine cmd = getOptions(args);
        ACTIONS a;
        try {
            a = ACTIONS.valueOf(cmd.getOptionValue("Daction"));
        }catch (Exception e){
            System.out.println("Bad action. Actions: Close, Open, State");
            return;
        }
        String ip = "//" + cmd.getOptionValue("DserverAddress") + "/administrationService";
        AdministrationService administrationService;
        try {
            administrationService = (AdministrationService) Naming.lookup(ip);
        }catch (Exception e){
            System.out.println("Bad ip");
            return;
        }

        switch (a){
            case Open:
                try {
                    administrationService.openElections();
                    System.out.println("Election Started");
                    return;
                } catch (RemoteException e) {
                    System.out.println("Could not connect to server.");
                } catch (IllegalActionException e) {
                    System.out.println("Illegal Action: " + e.getMessage());
                }
            case Close:
                try {
                    administrationService.closeElections();
                    System.out.println("Election closed");
                    return;
                } catch (RemoteException e) {
                    System.out.println("Could not connect to server.");
                } catch (IllegalActionException e) {
                    System.out.println("Illegal Action: " + e.getMessage());
                }
            case State:
                try {
                    System.out.println(administrationService.getElectionsState());
                    return;
                } catch (RemoteException e) {
                    System.out.println("Could not connect to server.");
                }
            default: System.out.println("Error");
        }
    }


    private static CommandLine getOptions(String[] args){


        Options options = new Options();

        Option ip = new Option("DserverAddress", "DserverAddress", true, "IP address of the server");
        ip.setRequired(true);
        options.addOption(ip);

        Option action = new Option("Daction", "Daction", true, "Actions: Close, Open, State");
        action.setRequired(true);
        options.addOption(action);

        return CommandLineHelper.generateCommandLineParser(options, args);
    }

}
