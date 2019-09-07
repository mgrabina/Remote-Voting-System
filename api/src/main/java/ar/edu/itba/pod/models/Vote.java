package ar.edu.itba.pod.models;

import java.util.List;
import java.util.Optional;

public class Vote {

    private String table;
    private String province;
    private String firstSelection;
    private String secondSelection;
    private String thirdSelection;

    public Vote(String table, String province, String firstSelection, String secondSelection, String thirdSelection) {
        this.table = table;
        this.province = province;
        this.firstSelection = firstSelection;
        this.secondSelection = secondSelection;
        this.thirdSelection = thirdSelection;
    }

    public String getTable() {
        return table;
    }

    public String getProvince() {
        return province;
    }

    public String getFirstSelection() {
        return firstSelection;
    }

    public Optional<String> getSecondSelection() {
        return Optional.ofNullable(secondSelection);
    }

    public Optional<String> getThirdSelection() {
        return Optional.ofNullable(thirdSelection);
    }
}
