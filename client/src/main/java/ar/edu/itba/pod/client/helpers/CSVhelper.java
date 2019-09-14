package ar.edu.itba.pod.client.helpers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Map;

public class CSVhelper {

    public static int parseData(String file, VoteCreator voteCreator) throws RemoteException {

        CSVParser csvParser = null;
        int voteCount = 0;

        try {
            Reader reader = Files.newBufferedReader(Paths.get(file));
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
        return voteCount;
    }

    public static void writeCsv(String file, Map<String, Double> results) {

        CSVPrinter csvPrinter = null;

        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file));
            csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(';')
                    .withHeader("Porcentaje", "Partido"));

            for (Map.Entry<String, Double> entry : results.entrySet()) {
                String party = entry.getKey();
                Double result = entry.getValue();
                csvPrinter.printRecord(result.toString(), party);
            }

            csvPrinter.flush();

        } catch (IOException e){
            System.out.println("Error while printing csv file.");
        }
    }


}
