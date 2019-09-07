package ar.edu.itba.pod.server.serviceClients;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.callbacks.InspectorCallback;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InspectionServiceImpl extends UnicastRemoteObject implements InspectionService {

    private Set<InspectorCallback> callbaks;
    private AdministrationService administrationService;

    public InspectionServiceImpl() throws RemoteException, MalformedURLException, NotBoundException {
        super();
        this.callbaks = new HashSet<>();
        this.administrationService = (AdministrationService) Naming.lookup(Constants.administrationServiceHost + "administrationService");
    }


    @Override
    public void registerInspector(String table, String party, InspectorCallback callback) throws RemoteException, IllegalStateException {
        if (administrationService.getElectionsState() != ElectionsState.NON_INITIALIZED){
            throw new IllegalStateException("Elections already started or finished.");
        }
        this.callbaks.add(callback);
    }
}
