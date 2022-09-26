package Main;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;

public class UpdateMenuForm extends JFrame{
	private JComboBox menus; // 메뉴 종류
	private JComboBox prices; // 가격
	private JComboBox maxCounts; // 조리가능수량
	private JTextField menunames; //  메뉴명
	private Connection con; // 데이터베이스 연결
	private Vector<Object> v;
	public UpdateMenuForm(Vector<Object> v) {
		setTitle("메뉴 수정");
		
		con = (Connection) Driver_con.Driver_con();
		this.v = v;
		
		this.add(new CenterP(), BorderLayout.CENTER);
		this.add(new SouthP(), BorderLayout.SOUTH);
		setSize(400,400);
		setVisible(true);
	}
	// 중간, 입력폼타이틀 입력폼 
	class CenterP extends JPanel{
		public CenterP() {
			this.setLayout(new GridLayout(1,2));
			this.add(new LeftP());
			this.add(new RightP());
		}
		
	}
	// 중간의 왼쪽, 입력폼 타이틀
	class LeftP extends JPanel{
		private String [] s1 = {"종류","*메뉴명","가격","조리가능수량"};
		public LeftP() {
			this.setLayout(new GridLayout(4,1)); // 4열 1행
			JLabel [] label = new JLabel[s1.length];
			for(int i=0; i<s1.length; i++) {
				label[i] = new JLabel(s1[i]);
				this.add(label[i]);
			}
		}
	}
	// 중간의 오른쪽, 입력폼 
	class RightP extends JPanel{
		private String [] s2 = {"한식","중식","일식","양식"};
		public RightP() {
			this.setLayout(new GridLayout(4,1)); // 4열 1행
			
			menus = new JComboBox(s2); // 메뉴 종류
			menus.setSelectedIndex((int) v.get(0)); // 받아온 메뉴 종류 index
			
			menunames = new JTextField(20); // 메뉴명
			menunames.setText((String) v.get(1)); // 받아온 메뉴명 
			
			int price_num = 1000; // 가격 500 단위로 +
			Vector<Integer> price_all = new Vector<Integer>();
			do {
				price_all.add(price_num);
				price_num+=500;
			}while(price_num<=12000); // 12000까지
			
			prices = new JComboBox(price_all); // 가격
			prices.setSelectedItem(v.get(2)); // 받아온 가격
			
			Vector<Integer> count = new Vector<Integer>(); //조리가능 수량
			// 0~50
			for(int i=0; i<=50; i++) { count.add(i);}
			
			maxCounts = new JComboBox(count); // 조리가능수량
			maxCounts.setSelectedIndex((int) v.get(3)); // 받아온 조리가능 수량
			
			this.add(menus);
			this.add(menunames);
			this.add(prices);
			this.add(maxCounts);
		}
	}
	// 하단, 버튼들 
	class SouthP extends JPanel{
		public SouthP()
		{
			this.setLayout(new GridLayout(1,2)); // 2행 1열
			JButton updatebtn = new JButton("수정");
			updatebtn.addActionListener(new ActionListener() { // 수정 
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String sql = "update meal set cuisineNo=?, mealName=?, price=?, maxCount=? where mealName=?";
					try {
						PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
						
						psmt.setInt(1, menus.getSelectedIndex()+1); // 선택한 종류인덱스값 +1
						psmt.setString(2, menunames.getText()); // 메뉴이름 텍스트
						psmt.setInt(3, (int) prices.getSelectedItem()); // 가격
						psmt.setInt(4, (int) maxCounts.getSelectedItem()); // 조리가능수량
						psmt.setString(5, (String) v.get(1));
						
						int rs = psmt.executeUpdate(); // sql문 실행
						if(rs==1) {
							JOptionPane.showMessageDialog(null, "메뉴가 정상적으로 수정되었습니다.","Message",JOptionPane.INFORMATION_MESSAGE);
							dispose();
							new ManageMenuForm();
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			JButton closebtn = new JButton("닫기");
			closebtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			this.add(updatebtn);
			this.add(closebtn);
		}
	}
}
