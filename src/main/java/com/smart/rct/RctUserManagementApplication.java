package com.smart.rct;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.smart.rct.common.repositoryImpl.RFDBRepositoryImpl;

@SpringBootApplication
@EnableScheduling
public class RctUserManagementApplication extends SpringBootServletInitializer
{
	final static Logger logger = LoggerFactory.getLogger(RctUserManagementApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RctUserManagementApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(RctUserManagementApplication.class, args);
	}
	
	/*@PostConstruct
	public List<String> getData() {
		
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.mariadb.jdbc.Driver");
		ds.setUrl("jdbc:mariadb://localhost:3306/RctUserMgmt");
		ds.setUsername("root");
		ds.setPassword("root123");
		
		JdbcTemplate jdbc = new JdbcTemplate(ds);
		String query = "select * from VZW_4G_CIQ_IP_PLAN";
		ResultSetExtractor<List<String>> ex = new ResultSetExtractor<List<String>>() {

			@Override
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> data = new ArrayList<>();
				System.out.println(rs);
				if (rs.next()) {
					
					data.add(rs.getString("MARKET"));
					}
				return data;
			}
		};
		List<String> data = jdbc.query(query, ex);
		if(data!=null) {
			for(String data1: data) {
				logger.error("Data rfdb: "+data1);
			}
		}
		return jdbc.query(query, ex);
	}*/
	
	
	
}



