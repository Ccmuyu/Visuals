package zzw.visual.zk.dubbo;

import java.text.DecimalFormat;

public class GreetingsServiceImpl implements GreetingsService {

    @Override
    public String hi(String name) {
        return "hi! " + name;
    }


    public static void main(String[] args) {
        long a = 1234567809;
        String s = String.valueOf(a);
        System.out.println(s.length());

        DecimalFormat format = new DecimalFormat("00000000");
        System.out.println(format.format(a));;
    }
}
