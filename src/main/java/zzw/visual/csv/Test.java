package zzw.visual.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Test {

    public static void main(String[] args) {

        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("tmp/合规库有业务库status不为0的没有的数据.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String sql = "select * from TB_BANK_ACCOUNT where (CORE_ACCOUNT_ID='" +
                "' and BANK_ACCOUNT_INDEX ='')or()";
        StringBuilder builder = new StringBuilder("");
        builder.append("select * from TB_BANK_ACCOUNT where ");
        try {
            String line ;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                String[] split = line.split(",");
                builder.append("(CORE_ACCOUNT_ID='").append(split[0]).append("' ")
                        .append(" and ")
                        .append("BANK_ACCOUNT_INDEX ='").append(split[1]).append("') ");
                builder.append(" or ");
            }
//            System.out.println("===================");
            System.out.println(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
