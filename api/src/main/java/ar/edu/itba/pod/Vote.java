package ar.edu.itba.pod;

import java.util.List;

public class Vote {

    private String table;
    private String province;
    private List<String> politicalParties;

    public Vote(String table, String province, List<String> politicalParties) {
        this.table = table;
        this.province = province;
        this.politicalParties = politicalParties;
    }

    public String getTable() {
        return table;
    }

    public String getProvince() {
        return province;
    }

    public List<String> getPoliticalParties() {
        return politicalParties;
    }
}
