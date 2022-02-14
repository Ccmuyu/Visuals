package zzw.visual.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ForeignTest {

    public static void main(String[] args) {

        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("tmp/foreign.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                final String[] data = line.split(",");
                String accountId = data[0];
                String sex = data[1];
                String validDate = data[2];
                String national = data[3];
                System.out.println(accountId + " " + sex + " " + validDate + " " + national);
            }
        }catch (IOException e){
            System.out.println(e);
        }

        System.out.println("============");
    }
}
