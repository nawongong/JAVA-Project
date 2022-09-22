// datePicker
// https://blog.eduonix.com/java-programming-2/how-to-use-date-picker-component-in-java/ 
package Main;
import Main.Driver_con;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

// 고객등록 폼 메인폼->고객등록
public class InsertCustomerForm extends JFrame{
	// 입력받을 창 배열
	private JTextField [] field;
	public InsertCustomerForm() {
		setTitle("고객 등록");
		// x버튼을 클릭했을 때 메인폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new MainForm();}
		});
		
		
		getContentPane().add(new CenterP(), BorderLayout.CENTER);
		getContentPane().add(new SouthP(), BorderLayout.SOUTH);
		
		setSize(500,300);
		setVisible(true);
	}
	// 중간, 입력받는 부분
	class CenterP extends JPanel{
		// 라벨 문자열 배열
		String [] s = {"고객 코드:", "*고객 명:", "*생년월일(YYYY-MM-DD):", "*연락처:", "주소:", "회사:"};
		public CenterP() {
			this.setLayout(new GridLayout(6,2)); // 2행 6열
			JLabel [] label = new JLabel[s.length]; 
			field = new JTextField[s.length];
			for(int i=0; i<s.length; i++) {
				label[i] = new JLabel(s[i]);
				field[i] = new JTextField(10);
				if(i==0) {
					// 고객코드는 직접입력 X
					field[i].setFocusable(false);
					field[i].setEnabled(false);
				}
				if(i==2) {
					// 생년월일 입력받고 엔터를 누르면 자동으로 고객코드 생성, 생년월일은 xxxx-xx-xx로 입력받을 것
					field[i].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							Calendar cal = Calendar.getInstance(); // 날짜 가져오기
							int year = cal.get(Calendar.YEAR)-2000; // 현재년도에서 십의자리만
							String str[] = field[2].getText().split("-"); // 입력
							int hap = Integer.valueOf(str[0]) + Integer.valueOf(str[1]) + Integer.valueOf(str[2]);
							field[0].setText("S" + year + hap);
							// 고객코드 S + 현재년도 + 생년월일
						}
					});
				}
				this.add(label[i]);
				this.add(field[i]);
			}
		}
	}
	// 하단, 버튼들
	class SouthP extends JPanel{
		public SouthP() {
			String [] btns_s = {"추가", "닫기"}; // 버튼들 문자열
			JButton [] btns = new JButton[btns_s.length]; // 버튼들
			
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(btns_s[i]);
				btns[i].addActionListener(new BtnListener(i));
				this.add(btns[i]);
			}
		}
	}
	// 버튼 이벤트 리스너
	class BtnListener implements ActionListener{
		int i;
		public BtnListener(int i) {this.i = i;}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			switch(i) {
				case 0: // 추가
					if(AddCustomer()) {
						JOptionPane.showMessageDialog(null, "고객추가가 완료되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
						dispose();
						new MainForm(); // 메인폼으로
					}
					else {JOptionPane.showMessageDialog(null, "필수 항목(*)을 모두 입력하세요", "고객 등록 에러", JOptionPane.ERROR_MESSAGE);}
					break;
				case 1: // 닫기
					dispose();
					new MainForm(); // 메인폼으로
					break;
			}
		}
	}
	// 데이터베이스에 입력한 고객추가
	private boolean AddCustomer() {
		boolean chk = false; // 고객추가가 되었는지 아닌지 체크 변수, 리턴값
		// 고객명, 생년월일, 연락처를 입력을 했으면 추가, 아니면 경고창
		if(!field[1].getText().equals("") && !field[2].getText().equals("") && !field[3].getText().equals("")) {
			Connection con = Driver_con.Driver_con(); // 데이터베이스 연결
			String sql = "insert into customer values(?,?,?,?,?,?)"; // customer 테이블에 삽입하는 명령어
			try {
				PreparedStatement psmt = con.prepareStatement(sql); 
				for(int i=0; i<field.length; i++) {psmt.setString(i+1, field[i].getText());}
				int rs = psmt.executeUpdate(); // sql 명령어 실행, 성공하면 1
				if(rs==1) {chk = true;} // 추가가 되면 true
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {chk = false;} // 필수 입력칸에 빈칸이 있으면 false
		return chk;
	}
}
