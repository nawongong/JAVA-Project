package DB;

// DB가 생성되고 table이 생성되고 table안에 데이터값이 insert 될 메인, 순차적으로
public class DBMain {
	public static void main(String[] args) {
		// 데이터베이스 생성 + 테이블 생성
		new DBCreate();
		// 테이블에 데이터 삽입
		new InsertDB();
	}

}
