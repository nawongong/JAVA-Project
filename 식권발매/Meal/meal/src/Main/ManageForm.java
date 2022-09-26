package Main;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// 관리폼, 메인폼->관리자 패스워드 입력폼->관리폼
public class ManageForm extends JFrame{
	public ManageForm() {
		setTitle("관리");
		// x버튼을 클릭했을 때 메인폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new MainForm();}
		});
		
		this.setLayout(new BorderLayout(10,10));
		
		this.add(new NorthP(), BorderLayout.NORTH);
		this.add(new CenterP(), BorderLayout.CENTER);
		
		setSize(600,480);
		setVisible(true);
	}
	// 상단, 버튼들이 들어갈 곳
	class NorthP extends JPanel{
		private String [] btn = {"메뉴 등록", "메뉴 관리", "결제 조회", "종류별 차트", "종료"};
		private JButton [] btns;
		public NorthP() {
			btns = new JButton[btn.length];
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(btn[i]);
				btns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						switch(e.getActionCommand()) {
							case "메뉴 등록": dispose(); new NewMenuForm(); break;
							case "메뉴 관리": dispose(); new ManageMenuForm(); break;
							case "결제 조회": dispose(); new OrderListForm(); break;
							case "종류별 차트": dispose(); new ChartForm(); break;
							case "종료": System.exit(0); break;
						}
					}
				});
				this.add(btns[i]);
			}
		}
	}
	// 중간, 이미지가 들어갈 곳
	class CenterP extends JPanel{
		public CenterP() {
			ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("main.jpg"));
			JLabel label = new JLabel(icon);
			this.add(label);
		}
	}
}
