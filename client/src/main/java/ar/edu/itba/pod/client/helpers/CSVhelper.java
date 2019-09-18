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
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

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

            voteCreator.create(table, province, choices[0], choices.length >= 2 ? choices[1] : null, choices.length == 3 ? choices[2] : null);
            voteCount++;
        }

        return voteCount;
    }

    public static void writeCsv(String file, Map<String, Double> results) {

        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file));
            final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(';')
                    .withHeader("Porcentaje", "Partido").withRecordSeparator('\n'));

            results.entrySet().stream().sorted((o1, o2) -> {
                if(!o1.getValue().equals(o2.getValue()))
                    return Double.compare(o2.getValue(),o1.getValue());
                return o1.getKey().compareTo(o2.getKey());
            }).forEach(entry -> {
                String party = entry.getKey();
                DecimalFormat format = new DecimalFormat("##.00");
                String percent = format.format(entry.getValue() * 100) + "%";
                try {
                    csvPrinter.printRecord(percent, party);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            csvPrinter.flush();

        } catch (IOException e){
            System.out.println("Error while printing csv file.");
        }
    }

    public static void generateRandomData() {

        CSVPrinter csvPrinter = null;
        List<String> parties = Arrays.asList("GORILLA","LEOPARD","TURTLE","OWL","TIGER","TARSIER","MONKEY","LYNX",
                "WHITE_TIGER","WHITE_GORILLA","SNAKE","JACKALOPE","BUFFALO");
        List<String> provinces = Arrays.asList("JUNGLE");
        Random r = new Random();

        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("votos.csv"));
            csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(';').withRecordSeparator('\n'));
            int randomSize = ThreadLocalRandom.current().nextInt(10,10);
            for (int i = 1; i < randomSize ; i++) {
                String table = String.valueOf(r.nextInt(1000) + 1000);
                String province = provinces.get(r.nextInt(provinces.size()));

                int polNumber = r.nextInt(3) + 1;
                List<String> selectedParties = new LinkedList<>();
                IntStream.rangeClosed(1, polNumber).forEach(j -> {
                    selectedParties.add(parties.get(r.nextInt(parties.size())));
                });
                String partiesStr = selectedParties.stream().collect(Collectors.joining(","));

                csvPrinter.printRecord(table, province, partiesStr);
            }

            csvPrinter.flush();

        } catch (IOException e){
            System.out.println("Error while printing csv file.");
        }
    }

}
