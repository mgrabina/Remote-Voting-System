package ar.edu.itba.pod.client.helpers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;

public class CSVhelper {

    public static void parseData(String path, VoteCreator voteCreator) throws RemoteException {

        CSVParser csvParser = null;
        int voteCount = 0;

        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            csvParser = new CSVParser(reader, CSVFormat.newFormat(';'));
        } catch (IOException ex) {
            System.out.println("Error while parsing csv file.");
        }

        for (CSVRecord csvRecord : csvParser) {

            String table = csvRecord.get(0);
            String province = csvRecord.get(1);
            String[] choices = csvRecord.get(2).split(",");

            voteCreator.create(table, province, choices[0], choices.length == 2 ? choices[1] : null, choices.length == 3 ? choices[2] : null);
            voteCount++;
        }

        System.out.println(voteCount + " votes registered");
    }


}
