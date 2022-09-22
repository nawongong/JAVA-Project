package Main;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Main.Driver_con;

// 제일 처음 실행될 화면, 관리자 로그인 화면
public class AdminLoginForm extends JFrame{
	private JTextField name; // 이름
	private JPasswordField pw;  // 비밀번호
	public AdminLoginForm() {
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(new NorthP(), BorderLayout.NORTH);
		getContentPane().add(new CenterP(), BorderLayout.CENTER);
		getContentPane().add(new SouthP(), BorderLayout.SOUTH);
		
		setSize(300,150);
		setVisible(true);
	}
	// 상단
	class NorthP extends JPanel{
		public NorthP() {
			// 타이틀
			this.add(new JLabel("관리자 로그인"));
		}
	}
	// 중간
	class CenterP extends JPanel{
		public CenterP() {
			this.setLayout(new GridLayout(2,2,0,10));
			
			// 이름 라벨 + 이름 입력창
			this.add(new JLabel("이름"));
			name = new JTextField(10);
			this.add(name);
			
			// 비밀번호 라벨 + 비밀번호 입력창
			this.add(new JLabel("비밀번호"));
			pw = new JPasswordField(10);
			this.add(pw);
		}
	}
	// 하단
	class SouthP extends JPanel{
		public SouthP() {
			String [] btns_s = {"확인","종료"};
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
		int i; // 클릭한 버튼의 번호
		public BtnListener(int i) {this.i = i;} // 생성자 
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(i) {
				case 0: // 확인, 로그인 실행
					if(Login()) {
						JOptionPane.showMessageDialog(null, "로그인되었습니다.");
						dispose();
						new MainForm(); // 메인폼으롷
					}else {JOptionPane.showMessageDialog(null, "일치하는 정보가 없습니다.");}
					break;
				case 1: // 종료
					System.exit(1);
					break;
			}
		}
	}
	// 로그인 확인 함수
	private boolean Login() {
		Connection con = (Connection) new Driver_con().Driver_con(); // 데이터베이스 연결
		String sql = "select name from admin where name=? and passwd = ?"; // admin 테이블에서 일치하는 정보가 있는지 sql 명령어
		boolean chk = false; // 로그인 성공 여부, 초기화
		try {
			PreparedStatement psmt = con.prepareStatement(sql);
			psmt.setString(1, name.getText()); // 이름 입력창에서 입력한 이름
			psmt.setString(2, new String(pw.getPassword())); // 스트링 객체에 넣거나, toString해도 됨
			ResultSet rs = psmt.executeQuery(); // sql문 실행
			if(rs.next()) {chk = true;} // 로그인 성공
			else {chk = false;} // 로그인 실패
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chk;
	}
	// 로그인 성공하면 시작, 메인은 여기에서만 -> 다음 폼으로 연결됨
	public static void main(String[] args) {
		new AdminLoginForm();
	}

}
