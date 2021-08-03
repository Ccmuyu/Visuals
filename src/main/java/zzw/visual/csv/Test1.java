package zzw.visual.csv;

public class Test1 {
    public static void main(String[] args) {
        String d = "1630020002462958|aa90cac303fddc046bce7ca7e7f1ae45";
        String x = d.replace("\\|", ",");
        System.out.println(x);
        String[] split = x.split(",");
        System.out.println(split[1]);
    }
}
