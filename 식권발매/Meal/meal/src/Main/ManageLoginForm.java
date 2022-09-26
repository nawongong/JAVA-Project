package Main;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// 관리자 패스워드 입력폼 , 메인폼->관리자
public class ManageLoginForm extends JFrame{
	private JPasswordField inputpw; // 패스워드 입력창
	private String pws = "";// 입력받은 패스워드 문자열 
	public ManageLoginForm() {
		setTitle("관리자 패스워드 입력");
		
		int r = JOptionPane.showConfirmDialog(null, new MainP(),"관리자 패스워드입력",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if(r==JOptionPane.OK_OPTION) {
			if(pws.equals("0000")) { // 비밀번호가 0000이 맞는지 확인
				new ManageForm(); // 관리 폼으로 
			}else {
				JOptionPane.showMessageDialog(null, "관리자 패스워드를 확인하십시오.","Message",JOptionPane.ERROR_MESSAGE);
				new ManageLoginForm(); // 다시 비밀번호 창 띄우기
			}
		}else {
			new MainForm(); // 취소버튼 클릭 시 메인화면으로
		}
	}
	// optionPane안에 들어갈 입력폼
	class MainP extends JPanel{
		public MainP() {
			this.setLayout(new BorderLayout());
			this.add(new NorthP(), BorderLayout.NORTH);
			this.add(new CenterP(), BorderLayout.CENTER);
		}
	}
	// 입력폼안의 상단, 비밀번호 입력폼 
	class NorthP extends JPanel{
		public NorthP() {
			inputpw = new JPasswordField(20); // 비밀번호 입력폼
			inputpw.setEchoChar('●'); // 입력받을때 표시할 문자
			inputpw.setEnabled(false); // 직접입력불가
			
			this.add(inputpw);
		}
	}
	// 입력폼안의 중간, 숫자입력 + 버튼
	class CenterP extends JPanel{
		private JButton [] btns; // 숫자 1-9버튼
		private JButton btn0; // 0 버튼
		public CenterP() {
			this.setLayout(new BorderLayout());
			
			JPanel panel1 = new JPanel(); // 1-9버튼 넣을 panel
			panel1.setLayout(new GridLayout(3,3)); // 3행 3열
			btns = new JButton[9]; // 1-9
			
			// 1-9버튼 생성
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(Integer.toString(i+1));
				btns[i].addActionListener(new BtnListener());
				panel1.add(btns[i]); 
			}
			
			btn0 = new JButton("0"); // 0버튼
			btn0.addActionListener(new BtnListener());
			
			this.add(panel1, BorderLayout.CENTER); // 1-9는 중간에
			this.add(btn0, BorderLayout.SOUTH); // 0은 하단에 
		}
	}
	// 버튼 이벤트 리스너
	class BtnListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(new String(inputpw.getPassword()).length() < 4) { // 입력받은 비밀번호가 4자 미만일 때
				pws = pws.concat(e.getActionCommand()); // 비밀번호 텍스트값에 입력한 숫자 이어붙이기
				inputpw.setText(pws); // 비밀번호 입력폼에 텍스트값 넣기
			}
		}
	}
}
