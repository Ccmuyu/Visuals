package zzw.visual.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;

public class DbUtil {
    //mrcenter
    private static final String url = "jdbc:mysql://localhost:4332/mrcenter?useSSL=false";
    private static final String user = "root";
    private static final String pwd = "root";


    public static DataSource getDatasource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
        dataSource.setTestOnBorrow(true);
        try {
            dataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("druid初始化失败。");
            throw new RuntimeException("db init error.");
        }
        return dataSource;
    }

    public static JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDatasource());
        return jdbcTemplate;
    }


}
