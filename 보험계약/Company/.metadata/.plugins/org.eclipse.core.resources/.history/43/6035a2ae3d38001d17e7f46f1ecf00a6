package DB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

// 테이블에 데이터 삽입, 데이터파일에 있는 txt파일 이용
public class InsertDB {
	public InsertDB() {
		// 데이터베이스 연결
		Connection con = Driver_con.Driver_con();
		// 테이블 삽입 명령어 배열
		String [] sql = {"insert into admin values(?,?,?,?,?)", "insert into customer values(?,?,?,?,?,?)", "insert into contract values(?,?,?,?,?,?)"};
		// 파일 이름 배열
		String [] filename = {"admin", "customer", "contract"};
		PreparedStatement psmt = null;
		
		try {
			for(int i=0; i<filename.length; i++) {
				psmt = con.prepareStatement(sql[i]);
				// 파일 읽어오기
				BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename[i] + ".txt")));
				String str = ""; // 파일에서 한줄 
				in.readLine(); // 위에 한줄은 타이틀이라서 한번 읽어낸 뒤
				while((str = in.readLine())!=null) {
					StringTokenizer st = new StringTokenizer(str, "\t"); // 한줄에서 탭을 기준으로 자르기
					int count = st.countTokens(); // 잘라낸 갯수
					String [] sr = new String[count]; // 문자열 담아낼 배열
					
					// 잘라낸 배열만큼 돌려서 테이블에 삽입
					for(int j=0; j<count; j++) {
						sr[j] = st.nextToken();
						psmt.setString(j+1, sr[j]);
					}
					psmt.executeUpdate();
				}
			}
			System.out.println("테이블 삽입 완료");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
