package ar.edu.itba.pod.models;

public class Inspector {
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
