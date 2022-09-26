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
		Connection con = Driver_con.Driver_con();
		// 각 테이블명에 맞는 insert sql문
		String [] sql = {"insert into cuisine values(?,?)", "insert into meal values(?,?,?,?,?,?)", "insert into member values(?,?,?)", "insert into orderlist values(?,?,?,?,?,?,?)"};
		// 파일이름
		String [] filename = {"cuisine", "meal", "member", "orderlist"};
		PreparedStatement psmt = null;
		try {
			for(int i=0; i<filename.length; i++) {
				psmt = (PreparedStatement) con.prepareStatement(sql[i]);
				// 절대경로를 사용하지 않기 위해서 
				BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename[i]+ ".txt")));
				String str = ""; // 파일에서 한줄
				in.readLine(); // 위에 한줄은 타이틀이라서 한번 읽어낸 뒤
				while((str = in.readLine())!=null) { // 마지막줄이 아니면 계속 
					StringTokenizer st = new StringTokenizer(str, "\t"); // 한줄에서 탭을 기준으로 자르기
					int count = st.countTokens(); // 잘라낸 갯수
					String [] sr = new String[count]; // 문자열 담아낼 배열
					
					// 잘라낸 배열만큼 돌려서 테이블 삽입
					for(int j=0; j<count; j++) {
						sr[j] = st.nextToken();
						psmt.setString(j+1, sr[j]);
					}
					psmt.executeUpdate();
				}
			}
			System.out.println("테이블 insert 완료");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
