package Main;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ChartForm extends JFrame{
	private Connection con; //  db연결
	private int allcount; // 메뉴 전체 값
	private int [] one_count; // 한 메뉴값
	private double [] one_angle; // 한메뉴값/메뉴 전체값 * 360에 반올림, Math.round
	private Color [] colors; // 각 메뉴 색상
	private int r,g,b; // r,g,b값
	public ChartForm() {
		setTitle("종류별 결제현황차트");
		
		// x버튼을 클릭했을 때 이전폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new ManageForm();}
		});
		
		con = (Connection) Driver_con.Driver_con();
		
		setCount(); // 한 메뉴 값, 전체 값 
		setColor(); // 각 메뉴 색상 정하기
		
		this.add(new NorthP(), BorderLayout.NORTH);
		this.add(new CenterP_R(), BorderLayout.CENTER);
		
		setSize(600, 500);
		setVisible(true);
	}
	// 상단, 버튼들 
	class NorthP extends JPanel{ 
		private String [] s = {"차트이미지저장", "닫기"};
		private JButton [] btns; // 버튼들
		public NorthP() {
			this.setLayout(new FlowLayout(FlowLayout.RIGHT));
			btns = new JButton[s.length];
			
			for(int i=0; i<s.length; i++) {
				btns[i] = new JButton(s[i]);
				btns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						switch(e.getActionCommand()) {
							case "차트이미지저장":saveimg(); break;
							case "닫기":dispose(); new ManageForm();break;
						}
					}
				});
				this.add(btns[i]);
			}
		}
	}
	class CenterP_R extends JPanel{ // center의 오른쪽
		private String [] menus = {"한식","중식","일식","양식"};
		private JLabel [] labels; // 메뉴 숫자값 앞에 색상
		public CenterP_R() {
			this.setLayout(null);
			
			// 타이틀
			JLabel c_title = new JLabel("종류별 결제건수 통계차트");
			c_title.setLocation(125,20);
			c_title.setSize(200,100);
			
			// 색상 라벨
			int h_p = 135;
			labels = new JLabel[menus.length];
			for(int i=0; i<4; i++) {
				labels[i] = new JLabel();
				labels[i].setBackground(colors[i]);
				switch(i+1) {
					case 1:
						labels[i].setLocation(430,h_p);
						break;
					case 2:
						labels[i].setLocation(430,h_p+=26);
						break;
					case 3:
						labels[i].setLocation(430,h_p+=26);
						break;
					case 4:
						labels[i].setLocation(430,h_p+=26);
						break;
				}
				labels[i].setOpaque(true);
				labels[i].setSize(10,10);
				this.add(c_title);
				this.add(labels[i]);
			}
			
			// 메뉴별 현황 라벨
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(4,1)); // 4열 1행
			for(int i=0; i<4; i++) {panel.add(new JLabel(menus[i] + "(" + one_count[i] +")"));}
			
			panel.setSize(100,100);
			panel.setLocation(450,130);
			this.add(panel);
		}
		// 차트 그리기
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);
			int start = 0;
			for(int i=0; i<4; i++) {
				g.setColor(colors[i]);
				g.fillArc(100, 100, 200, 200, start, (int) one_angle[i]);
				start+=one_angle[i];
			}
		}
	}
	// 각 메뉴 색상 정하기
	public void setColor() {
		colors = new Color[4];
		for(int i=0; i<4; i++) {
			randomRGB(); // 각 메뉴 색상 랜덤
			colors[i] = new Color(r,g,b);
		}
		
	}
	// 각 메뉴 색상 랜덤
	public void randomRGB() {
		Random random = new Random();
		r = random.nextInt(256);
		g = random.nextInt(256);
		b = random.nextInt(256);
	}
	// 각 메뉴 각도 구하기
	public void getAngle() {
		one_angle = new double[4]; // 각 메뉴 각도
		for(int i=0; i<4; i++) {one_angle[i] = Math.ceil((double)one_count[i]/allcount * 360);}
	}
	// 한 메뉴 값, 전체 값 
	public void setCount() {
		one_count = new int[4];
		for(int i=1; i<5; i++){
			String sql = "select count(*) from orderlist where cuisineNo=" + i;
			Statement st;
			try {
				st = (Statement) con.createStatement();
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					allcount += Integer.parseInt(rs.getString(1));
					one_count[i-1] = Integer.parseInt(rs.getString(1)); // 한 메뉴 값
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		getAngle(); // 각 각도 정하기
	}
	// 사진 저장---
	public void saveimg() {
		BufferedImage img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		paint(g2);
		try {
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1;
			int day  = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			String filename = Integer.toString(year) + Integer.toString(month) + Integer.toString(day) + Integer.toString(hour) + Integer.toString(min) + Integer.toString(sec) + "-종류별결제현황차트.jpg"; 
			
			ImageIO.write(img,  "jpg", new File("imgs/" + filename));
			JOptionPane.showMessageDialog(null, "차트 이미지가 저장되었습니다.","Message",JOptionPane.INFORMATION_MESSAGE);
		}catch(Exception e) {
			
		}
	}
	@Override
	public void paintComponents(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponents(g);
	}
}
