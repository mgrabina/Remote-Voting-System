package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Inspector implements Serializable {

    private static final long serialVersionUID = 8383170578958425496L;
    private String table;
    private String party;

    public Inspector(String table, String party) {
        this.table = table;
        this.party = party;
    }

    public String getTable() {
        return table;
    }

    public String getParty() {
        return party;
    }
}
