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
            String firstChoice = csvRecord.get(2);
            String secondChoice = null;
            String thirdChoice = null;

            try {
                secondChoice = csvRecord.get(3);
                thirdChoice = csvRecord.get(4);
            } catch (ArrayIndexOutOfBoundsException ex){
            }

            voteCreator.create(table, province, firstChoice, secondChoice, thirdChoice);
            voteCount++;
        }

        System.out.println(voteCount + " votes registered");
    }


}
