package Main;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;

// 신규 메뉴 등록폼, 메인폼->관리자 패스워드 입력폼->관리폼-> 메뉴 등록
public class NewMenuForm extends JFrame{
	private JComboBox cb1; // 메뉴 종류
	private JComboBox cb2; // 가격 
	private JComboBox cb3; // 조리가능수량
	private JTextField menuname; // 메뉴명 입력받을 field
	private Connection con; // 데이터베이스 연결
	public NewMenuForm() {
		setTitle("신규 메뉴 등록");
		// x버튼을 클릭했을 때 이전폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new ManageForm();}
		});
		
		con = (Connection) Driver_con.Driver_con();
		
		this.add(new MainP());
		setSize(350,200);
		setVisible(true);
	}
	class MainP extends JPanel{
		public MainP() {
			this.setLayout(new BorderLayout());
			this.add(new CenterP(), BorderLayout.CENTER);
			this.add(new SouthP(), BorderLayout.SOUTH);
		}
	}
	// 중간
	class CenterP extends JPanel{
		public CenterP() {
			this.setLayout(new GridLayout(1,2)); // 2행 1열
			this.add(new LeftP()); 
			this.add(new RightP());
		}
	}
	// 중간의 왼쪽, 입력폼의 타이틀이 들어갈 곳
	class LeftP extends JPanel{
		private String [] s = {"종류", "*메뉴명", "가격", "조리가능수량"};
		public LeftP() {
			this.setLayout(new GridLayout(4,1));
			JLabel [] labels = new JLabel[s.length];
			for(int i=0; i<labels.length; i++) {
				labels[i] = new JLabel(s[i]);
				this.add(labels[i]);
			}
		}
	}
	// 중간의 오른쪽, 입력폼이 들어갈 곳
	class RightP extends JPanel{
		public RightP() {
			this.setLayout(new GridLayout(4,1)); // 1행 4열
			
			// 메뉴종류
			String sql = "select cuisineName from cuisine"; // 메뉴 종류 select
			Vector<String> v1 = new Vector<String>(); // 메뉴 종류 데이터를 담을 벡터
			try {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql); // sql문 실행
				while(rs.next()) {
					v1.add(rs.getString(1)); // 메뉴 종류 add
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cb1 = new JComboBox(v1); // 메뉴 종류 콤보박스, select한 벡터값을 넣음
			// 메뉴명
			menuname = new JTextField(20); // 메뉴이름 입력폼
			// 가격
			int min = 1000; // 최솟값 1000 부터 시작
			Vector<Integer> v2 = new Vector<Integer>(); // 가격 최소 1000~ 최대12000
			do {
				v2.add(min);
				min += 500; //  500씩 +
			}while(min<=12000); // 12000까지만
			cb2 = new JComboBox(v2); // 가격 콤보박스
			
			// 조리가능수량
			Vector<Integer> v3 = new Vector<Integer>(); //  조리가능수량 0 ~ 50
			for(int i=0; i<=50; i++) {v3.add(i);}
			cb3 = new JComboBox(v3); // 조리가능수량
			
			this.add(cb1); 
			this.add(menuname);
			this.add(cb2);
			this.add(cb3);
		}
	}
	// 하단, 버튼들이 들어갈 곳
	class SouthP extends JPanel{
		private String [] s = {"등록", "닫기"};
		private JButton [] btns; // 등록, 닫기 버튼
		public SouthP() {
			this.setLayout(new GridLayout(1,2)); // 2행 1열
			btns = new JButton[s.length];
			
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(s[i]);
				btns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						switch(e.getActionCommand()) {
							case "등록":
								if(menuname.getText().equals("")) { // 메뉴명에 공백을 입력했을 시
									JOptionPane.showMessageDialog(null, "메뉴명을 입력해주세요.","Message",JOptionPane.ERROR_MESSAGE);
								}else {
									String sql = "insert into meal(cuisineNo,mealName,price,maxCount,todayMeal) values(?,?,?,?,?)"; // meal 테이블에 추가 명령어
									try {
										PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql); 
										psmt.setInt(1, cb1.getSelectedIndex()+1); // 0부터 시작해서 +1, 메뉴 종류는 숫자이기 때문에
										psmt.setString(2, menuname.getText()); // 메뉴명
										psmt.setInt(3, Integer.parseInt(cb2.getSelectedItem().toString())); // 가격, 콤보박스에서 선택한 텍스트값
										psmt.setInt(4, Integer.parseInt(cb3.getSelectedItem().toString())); // 조리가능수량, 콤모박스에서 선택한 텍스트값
										psmt.setInt(5, 1); // 오늘의 메뉴는 기본값 1로 설정
										
										int rs = psmt.executeUpdate(); // sql문 실행
										if(rs==1) { // 성공 시
											JOptionPane.showMessageDialog(null, "메뉴가 정상적으로 등록되었습니다.","Message",JOptionPane.INFORMATION_MESSAGE);
											dispose(); // 폼닫기
											new ManageForm(); // 관리폼으로
										}
										
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								break;
							case "닫기":new ManageForm(); break; // 관리자 메인폼으로
						}
					}
				});
				this.add(btns[i]);
			}
		}
	}
}
