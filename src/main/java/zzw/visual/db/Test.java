package zzw.visual.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static zzw.visual.db.DbUtil.jdbcTemplate;

public class Test {
    static final String TIMESTAMP = "select * from TEST_TIMESTAMP";
    static final String DATETIME = "select * from TEST_DATETIME";

    public static void main(String[] args) throws SQLException {
        final JdbcTemplate jdbcTemplate = jdbcTemplate();

        final List<Map<String, Object>> show_tables = jdbcTemplate.queryForList("show tables");
        System.out.println(show_tables);
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        final Date date = jdbcTemplate.queryForObject(DATETIME, Date.class);
        System.out.println(date);
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");

        final Timestamp t = jdbcTemplate.queryForObject(DATETIME, Timestamp.class);
        System.out.println(t);
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");

        final List<Map<String, Object>> maps = jdbcTemplate.queryForList(TIMESTAMP);
        System.out.println(maps);


    }

}
