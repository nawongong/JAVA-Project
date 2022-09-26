package Main;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// 메인폼, 메인은 여기에만 존재
public class MainForm extends JFrame{
	private String [] btns_s = {"사용자", "관리자", "사원등록", "종료"}; // 버튼 배열
	private JButton [] btns; // 버튼 배열
	public MainForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // x버튼 클릭시 화면 닫힘
		setTitle("메인");

		this.setLayout(new GridLayout(4,1)); // 1행 4열
		btns = new JButton[btns_s.length];
		for(int i=0; i<btns_s.length; i++) { // 버튼 생성
			btns[i] = new JButton(btns_s[i]);
			// 버튼 각 이벤트, 각 화면으로 넘어가게
			btns[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					switch(e.getActionCommand()) {
						case "사용자": dispose(); new CuisineForm();break;
						case "관리자": dispose(); new ManageLoginForm(); break;
						case "사원등록":dispose(); new NewMemberForm(); break;
						case "종료": System.exit(0); break;
					}
				}
			});
			this.add(btns[i]); // 버튼 추가
		}
		setSize(250,250);
		setVisible(true);
	}
	public static void main(String[] args) {
		new MainForm();
	}

}
