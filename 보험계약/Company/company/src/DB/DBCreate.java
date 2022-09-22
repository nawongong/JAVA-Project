package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// 데이터베이스와 테이블이 생성될 클래스
public class DBCreate {
	public DBCreate() {
		String url = "jdbc:mysql://localhost";
		String id = "root";
		String pw = "1234";
		// 데이터베이스 생성 명령어, company라는 데이터베이스가 존재하지 않으면 생성
		String createdb = "create database if not exists company";
		// company 데이터베이스 사용 
		String usedb = "use company";
		
		//테이블 생성
		String [] createtb = {"admin(name varchar(20) not null, passwd varchar(20) not null, position varchar(20), jumin char(14), inputDate date, primary key(name,passwd))",
								"customer(code char(7) not null, name varchar(20) not null, birth date, tel varchar(20), address varchar(100), company varchar(20), primary key(code,name))",
								"contract(customerCode char(7) not null, contractName varchar(20) not null, regPrice int, regDate date not null, monthPrice int, adminName varchar(20) not null)"};
		
		// 데이터베이스 연결
		Connection con = null;
		Statement st = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url,id,pw);
			st = (Statement) con.createStatement();
			// 데이터베이스 생성 명령어 실행
			st.execute(createdb);
			// 데이터베이스 사용 명령어 실행
			st.execute(usedb);
			// 테이블 생성 명령어 실행
			for(String s:createtb) {
				st.executeUpdate("create table " + s);
			}
			System.out.println("테이블 생성 완료");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
