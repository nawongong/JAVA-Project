package Main;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// 보험계약 메인 화면
public class MainForm extends JFrame{
	public MainForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("보험계약 관리 화면");
		
		getContentPane().add(new NorthP(), BorderLayout.NORTH);
		getContentPane().add(new CenterP(),BorderLayout.CENTER);
		
		setSize(600, 500);
		setVisible(true);
	}
	// 위쪽 panel, 버튼들이 담길 곳
	class NorthP extends JPanel{
		public NorthP() {
			String [] s = {"고객등록","고객조회","계약관리","종료"}; // 버튼들 안의 텍스트
			JButton [] btns = new JButton[s.length]; // 버튼들
			for(int i=0; i<s.length; i++) {
				btns[i] = new JButton(s[i]);
				btns[i].addActionListener(new BtnListener(i));
				this.add(btns[i]);
			}
		}
	}
	// 버튼 이벤트 리스너
	class BtnListener implements ActionListener{
		int i; // 클릭한 버튼의 번호
		public BtnListener(int i) {this.i = i;}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			switch(i) {
				case 0: // 고객등록
					dispose();
					new InsertCustomerForm();
					break; 
				case 1: // 고객조회
					dispose();
					new SelectCustomerForm();
					break; 
				case 2: 
					dispose();
					new ManageCustomerForm();
					break; // 계약관리
				case 3: System.exit(0); break; // 종료
			}
		}
	}
	// 중간에 이미지가 담길 곳
	class CenterP extends JPanel{
		public CenterP() {
			ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("img.jpg"));
			JLabel image = new JLabel();
			image.setIcon(img);
			this.add(image);
		}
	}
}
