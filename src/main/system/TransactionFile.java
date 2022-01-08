package src.main.system;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class that reads from a file named daily.txt containing transaction codes of transactions that
 * have occurred in a singular session.
 */
public class TransactionFile implements Iterable<String> {
    private BufferedReader reader;
    private final ArrayList<String> transactionCodes = new ArrayList<>();

    /**
     * Initializes a TransactionFile iterable.
     * @param file the file to be made into an iterable.
     */
    public TransactionFile(File file) {
        if (!file.exists()) {
            System.out.println("daily.txt does not exist and will try to create one.\n" +
                    "Please modify daily.txt file first.");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.reader = new BufferedReader(new FileReader(file.getPath()));
            this.readTransactions();
        } catch (FileNotFoundException e) {
            System.out.println("daily.txt does not exist");
        }
    }

    /**
     * Reads every line from a file and stores them to be iterated.
     */
    private void readTransactions() {
        String transactionCode;
        try {
            while((transactionCode = this.reader.readLine()) != null){
                this.transactionCodes.add(transactionCode);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @return an iterator for this transaction file.
     */
    @Override
    public Iterator<String> iterator() {
        return new TransactionFileIterator() ;
    }

    /**
     * An iterator for a Transaction file.
     */
    private class TransactionFileIterator implements Iterator<String> {
        private int index;

        /**
         * Initializes an iterator for a given TransactionFile
         */
        public TransactionFileIterator() {
            index = 0;
        }

        /**
         * @return true if there is another transaction code to be read.
         */
        @Override
        public boolean hasNext() {
            return index < transactionCodes.size();
        }

        /**
         * @return the next transaction code in the TransactionFile
         */
        @Override
        public String next() {
            String code = transactionCodes.get(index);
            index++;
            return code;
        }
    }
}
