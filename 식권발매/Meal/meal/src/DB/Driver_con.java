package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//JDBC를 이용할 수 있는 드라이브가 연결된지 확인, 데이터베이스 연결까지 
public class Driver_con {
	public static Connection Driver_con() {
		// 뒤에 meal은 데이터베이스명
		String url = "jdbc:mysql://localhost/meal";
		String id = "root";
		String pw = "1234";
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			con = (Connection)DriverManager.getConnection(url, id, pw);
			System.out.println("데이터베이스 연결 성공");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("연결 실패");
		}
		
		return con;
	}
}
