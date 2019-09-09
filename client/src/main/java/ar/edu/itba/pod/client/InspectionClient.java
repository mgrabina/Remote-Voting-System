package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.client.handlers.InspectorCallbackHandlerImpl;
import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.services.InspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class InspectionClient {
    private static Logger logger = LoggerFactory.getLogger(InspectionClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        InspectionService inspectionService = (InspectionService) Naming.lookup(Constants.inspectionServiceHost + "inspectionService");

        InspectorCallback inspectorCallback = new InspectorCallbackHandlerImpl("TABLE", "PARTY");
        inspectionService.registerInspector("TABLE", "PARTY", inspectorCallback);
    }

}
