package Main;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import Main.Driver_con;

public class UpdateCustomerForm extends JFrame{
	private Vector<String> vc; // 가져온 데이터 벡터
	private JTextField [] field; // 입력받을 창
	public UpdateCustomerForm(Vector<String> vc) {
		this.vc = vc;
		setTitle("고객수정");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().add(new CenterP(), BorderLayout.CENTER);
		getContentPane().add(new SouthP(), BorderLayout.SOUTH);
		
		setSize(500, 300);
		setVisible(true);
	}
	// 중간
	class CenterP extends JPanel{
		public CenterP() {
			this.setLayout(new GridLayout(6,2)); // 2행 6열
			
			String [] s = {"고객 코드:", "*고객 명:", "*생년월일(YYYY-MM-DD):", "*연락처:", "주소:", "회사:"};
			JLabel [] label = new JLabel[s.length];
			field = new JTextField[s.length];
			
			for(int i=0; i<s.length; i++) {
				label[i] = new JLabel(s[i]);
				field[i] = new JTextField(10);
				// 고객코드랑 고객명은 수정 불가, 입력된 데이터 불러와서 입력폼에 넣기
				if(i==0 || i==1) {
					field[i].setFocusable(false);
					field[i].setEnabled(false);
					field[i].setText(vc.get(i));
				}else {
					field[i].setText(vc.get(i));
				}
				this.add(label[i]);
				this.add(field[i]);
			}
		}
	}
	// 하단
	class SouthP extends JPanel{
		public SouthP() {
			String [] btns_s = {"수정", "취소"};
			JButton [] btns = new JButton[btns_s.length];
			
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(btns_s[i]);
				btns[i].addActionListener(new BtnListener(i));
				this.add(btns[i]);
			}
		}
	}
	// 버튼 이벤트리스너
	class BtnListener implements ActionListener{
		int i;
		public BtnListener(int i) {this.i= i;}
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(i) {
				case 0: // 수정
					Update();
					break;
				case 1: // 취소
					dispose();
					break;
			}
		}
	}
	// 수정
	public void Update() {
		Connection con = Driver_con.Driver_con();
		String sql = "update customer set birth=?, tel=?, address=?, company=? where code=?"; // 수정 sql 명령문, 생년월일,전화번호, 주소, 회사를 코드명이 ~ 인 고객
		
		try {
			PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
			for(int i=0; i<field.length-2; i++) { // 2,3,4,5 입력폼값을 가져와야함, 4까지
				psmt.setString(i+1, field[i+2].getText());
			}
			psmt.setString(5, field[0].getText()); // 고객코드
			
			int re = psmt.executeUpdate(); // sql문 실행
			if(re>0) { // 성공 시
				JOptionPane.showMessageDialog(null, "고객수정이 완료되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			}else { // 실패 시
				JOptionPane.showMessageDialog(null, "입력을 확인해주세요", "고객수정 에러",JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
