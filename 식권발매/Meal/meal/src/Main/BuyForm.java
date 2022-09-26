package Main;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// 결제폼  / 메인폼-> 사용자 -> 결제폼
public class BuyForm extends JFrame{
	private String CuisineName; // 메뉴종류
	private String CuisineNum; // 메뉴 숫자
	private Connection con; //  db연결
	private Vector<BtnsC> Menubtns; //버튼 메뉴 클래스를 담을 벡터
	private int allprice; // 총 금액
	private JLabel allpricetxt; // 총 금액 나타낼 텍스트
	private Vector<String> colData; // table의 컬럼명
	private Vector<Vector<String>> rowData; // 테이블 안에 데이터값
	private JTable jt; // 테이블
	private DefaultTableModel model; // 테이블 모델 
	private JTextField selectproduct; // 선택한 품목
	private JTextField selectproduct_num; // 선택한 품목 수량
	private String selectnum_s = "";//선택한 버튼 수량 연결할 텍스트
	private int maxCountnum; // 조리가능수량
	private int mealNum; // 메뉴 번호
	private String selectmealName; // 선택한 메뉴 이름
	private int menuprice; // 메뉴 가격
	private JComboBox memberNum; // 사원번호
	private JPasswordField memberpw; // 사원 비밀번호
	private String selectmemberNum; // 선택한 사원번호 
	public BuyForm(String CuisineName) {
		con = Driver_con.Driver_con();
		
		setTitle("결제");
		// x버튼을 클릭했을 때 전폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new CuisineForm();}
		});
		
		// 받아온 메뉴 종류, 메뉴 번호
		this.CuisineName = CuisineName;
		this.CuisineNum = SelectCuisineNum();
		// 메뉴 번호에 따른 메뉴리스트 값을 가져옴
		SelectMeal(); 
		
		this.add(new NorthP(), BorderLayout.NORTH);
		this.add(new CenterP(),BorderLayout.CENTER);
		this.add(new EastP(), BorderLayout.EAST);
		
		setSize(1300,1000);
		setVisible(true);
	}
	// 메뉴 번호 가져오기(메뉴 종류에 따른 메뉴들을 가져올려면 필요)
	public String SelectCuisineNum() {
		// 메뉴이름이 ~ 인 메뉴의 번호값을 가져오라는 sql명령어
		String sql = "select cuisineNo from cuisine where cuisineName='" + CuisineName + "'";
		Statement st = null;
		ResultSet rs = null;
		String num = null;
		
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql); // sql문 실행
			while(rs.next()) {num = rs.getString("cuisineNo");} // 번호가져오기 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num; // 메뉴 번호 return
	}
	// 메뉴 종류에 따른 메뉴 select, 메뉴 버튼 벡터 생성
	public void SelectMeal() {
		// 메뉴 분류 번호가 ~ 인 메뉴정보를 가져오라는 sql 명령어
		String sql = "select * from meal where cuisineNo='" + CuisineNum + "'";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql); // sql 명령어 실행
			
			Menubtns = new Vector<BtnsC>();  // 메뉴 버튼들을 벡터값에 클래스로 저장
			while(rs.next()) { 
				// 메뉴번호, 메뉴명, 가격, 조리가능수량, 오늘의메뉴
				Menubtns.add(new BtnsC(rs.getInt("mealNo"),rs.getString("mealName"), rs.getString("price"), rs.getInt("maxCount"), rs.getInt("todayMeal")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 버튼 생성 클래스
	class BtnsC extends JButton{
		int mealNo; // 
		// 메뉴번호, 메뉴명, 가격, 조리가능수량, 오늘의메뉴 값을 받아옴
		public BtnsC(int mealNo, String mealName, String price, int maxCount, int todayMeal) {
			this.mealNo = mealNo;
			this.setEnabled(false); // 일반적으론 버튼 비활성화
			// 조리가능수가 0이아니고 오늘의 메뉴가 0이 아닐때, 버튼 활성화
			if(maxCount!=0 && todayMeal!=0) {this.setEnabled(true);}
			this.setText("<html><center>"+ mealName + "<br><br>" + price + "원</html>"); // 버튼 텍스트값
			// 버튼 클릭했을 때 이벤트
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JButton clickbtn = (JButton) e.getSource(); // 클릭한 버튼의 값을 가져옴
					// 선택품목, 수량 초기화
					selectproduct.setText(mealName);
					selectnum_s = "";
					selectproduct_num.setText(selectnum_s);
					
					// 메뉴번호, 조리가능수량, 메뉴이름,메뉴 금액 전역변수에 담기
					mealNum = mealNo; // 선택한 메뉴번호
					maxCountnum = maxCount; // 선택한 메뉴 조리가능수량
					selectmealName = mealName; // 선택한 메뉴 이름
					menuprice = Integer.parseInt(price); // 선택한 메뉴 가격
				}
			});
		}
	}
	// 상단 
	class NorthP extends JPanel{
		public NorthP() {
			this.setLayout(new BorderLayout()); // panel은 기본값이 flowlayout
			
			JPanel panel1 = new JPanel(); // 메뉴 종류를 담을 panel
			JPanel panel2 = new JPanel(); // 총금액글자를 담을 panel;
			JPanel panel3 = new JPanel(); // 총금액을 담을 panel;
			
			// 메뉴종류, panel1
			JLabel title = new JLabel(CuisineName);
			title.setFont(new Font("SanSerif", Font.BOLD, 30));
			panel1.add(title); // 추가
			
			// 총금액, panel2 
			panel2.setLayout(new GridLayout(1,2,110,0)); // 2행 1열 hgap 110
			allprice = 0; // 총금액 초기화
			JLabel allpricetitle = new JLabel("총결제금액:");
			allpricetitle.setFont(new Font("SanSerif", Font.PLAIN, 30));
			allpricetitle.setHorizontalAlignment(JLabel.LEFT);
			
			allpricetxt = new JLabel(allprice + "원"); // 총금액 label 텍스트값
			allpricetxt.setFont(new Font("SanSerif", Font.PLAIN, 30));
			allpricetxt.setHorizontalAlignment(JLabel.RIGHT);
			
			panel2.add(allpricetitle);
			panel2.add(allpricetxt);
			
			panel3.add(panel2);
			
			// NorthP에 add
			this.add(panel1, BorderLayout.NORTH);
			this.add(panel3, BorderLayout.EAST);
		}
	}
	
	// 중간, 버튼들
	class CenterP extends JPanel{
		public CenterP() {
			int count = Menubtns.size()/5; // 버튼 벡터값에 레이아웃 설정
			// 나눈 나머지 값이 0이 아닐경우에는 한줄더추가,아닌경우에는 나눈값만큼 열설정
			if(Menubtns.size()%5!=0) { this.setLayout(new GridLayout(count+1,5)); }
			else {this.setLayout(new GridLayout(count, 5));}
			// 벡터값만큼 for문 돌린 뒤, 메뉴 추가
			for(int i=0; i<Menubtns.size(); i++) {this.add(Menubtns.get(i));}
		}
	}
	// 오른쪽
	class EastP extends JPanel{
		public EastP() {
			this.setLayout(new BorderLayout(0,20));
			this.add(new EastN(), BorderLayout.NORTH);
			this.add(new EastC(), BorderLayout.CENTER);
			this.add(new EastS(), BorderLayout.SOUTH);
		}
	}
	// 오른쪽 East 안의 North에 올릴 Panel
	class EastN extends JPanel{
		public EastN() {
			this.setLayout(new BorderLayout(0,20));
			// 구매할 메뉴랑 수량 금액 나올 JTable
			colData = new Vector<String>();
			rowData = new Vector<Vector<String>>();
			
			// 컬럼명, 타이틀명
			colData.add("상품번호");
			colData.add("품명");
			colData.add("수량");
			colData.add("금액");
				
			// 테이블 수정 불가 
			model = new DefaultTableModel(rowData,colData) {
				@Override
				public boolean isCellEditable(int row, int column) {return false;}
			};
			
			jt = new JTable(model);
			jt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) { // 클릭시, 선택취소하기
					if(e.getClickCount()==2) { // 2번 클릭했을 때, 선택한 레코드 취소
						int selectrow = jt.getSelectedRow(); // 선택한 레코드 row번호
						if(selectrow==-1) return; // 클릭한게 없으면 -1

						// 선택한 레코드 버튼 활성화 시키기
						for(int i=0; i<Menubtns.size(); i++) {
							// 데이터값에서 선택한 인덱스의 메뉴번호와 메뉴 벡터값에 메뉴번호와 일치하면
							if(Integer.parseInt(rowData.get(selectrow).get(0)) == Menubtns.get(i).mealNo) {
								Menubtns.get(i).setEnabled(true); // 메뉴 버튼 활성화
							}
						}
						allprice -= Integer.parseInt(rowData.get(selectrow).get(3)); // 총금액 빼주기, 데이터값의 선택한 인덱스값 안의 3번째값
						allpricetxt.setText(String.format("%,d", allprice) + "원"); // 1000단위 ,
							
						// 선택한 레코드 삭제
						model.removeRow(selectrow);
					}
				}
			});
			JScrollPane jps = new JScrollPane(jt);

			JPanel panel1 = new JPanel(); // 선택품명, 수량 입력 넣을 panel
			// 선택품명, 수량 입력하는 곳
			// 선택품명 타이틀 label
			JLabel label1 = new JLabel("선택품명:");
			label1.setFont(new Font("SanSerif", Font.BOLD, 15));
			// 선택품명 입력폼
			selectproduct = new JTextField(26);
			selectproduct.setEnabled(false); // 비활성화
			selectproduct.setPreferredSize(new Dimension(150,30)); // 너비,높이
			
			//수량 타이틀 label
			JLabel label2 = new JLabel("수량:");
			label2.setFont(new Font("SanSerif", Font.BOLD, 15));
			// 수량 입력폼	
			selectproduct_num = new JTextField(5);
			selectproduct_num.setEnabled(false); // 비활성화
			selectproduct_num.setPreferredSize(new Dimension(150,30)); // 너비, 높이
			
			// 추가
			panel1.add(label1);
			panel1.add(selectproduct);
			panel1.add(label2);
			panel1.add(selectproduct_num);
				
			this.add(jps,BorderLayout.NORTH);
			this.add(panel1, BorderLayout.CENTER);
		}
	}
	// 오른쪽 East 안의 Center에 올릴 Panel
	class EastC extends JPanel{
		private JButton [] select_num; // 직접 수량 정할 버튼
		public EastC() {
			this.setLayout(new BorderLayout());
			// 숫자 입력 버튼 1-9, 0
			JPanel panel1 = new JPanel(); // 숫자 입력 버튼 넣을 panel
			panel1.setLayout(new BorderLayout());
				
			JPanel panel2 = new JPanel(); // 1-9가 들어갈 panel
			panel2.setLayout(new GridLayout(3,3)); // 3행 3열
				
			select_num = new JButton[10];
				
			for(int i=0; i<10; i++) {
				select_num[i] = new JButton(Integer.toString(i));
				select_num[i].addActionListener((new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(selectproduct_num.getText().length()<2) { // 입력받은 숫자가 2글자 미만일 때
							selectnum_s = selectnum_s.concat(e.getActionCommand()); // 선택한 숫자
							selectproduct_num.setText(selectnum_s); // 선택한 숫자 입력폼에 넣기
						}
					}
				}));
				if(i==0) {panel1.add(select_num[i], BorderLayout.SOUTH);} // 0일땐 panel1의 하단에
				else {panel2.add(select_num[i]);} // 아닐땐 panel2에 add
			}
			panel1.add(panel2,BorderLayout.CENTER); // panel2를 panel1의 중간에 
				
			JPanel panel3 = new JPanel(); // 입력, 초기화 버튼 넣을 panel;
			panel3.setLayout(new BorderLayout());

			// 입력 버튼
			JButton inputbtn = new JButton("입력");
				
			inputbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(selectproduct.getText().length()==0) { // 선택하지 않고 입력을 눌렀을 때
						JOptionPane.showMessageDialog(null, "품명을 선택해주세요.", "Message",JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(selectproduct_num.getText().length()==0) { // 수량을 입력하지 않았을 때
						JOptionPane.showMessageDialog(null, "수량을 지정해주세요.", "Message",JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(Integer.parseInt(selectproduct_num.getText())>maxCountnum || Integer.parseInt(selectproduct_num.getText())>= 10) { // 조리가능수량보다 많은 수량을 입력했을 때, 10이상일때
						JOptionPane.showMessageDialog(null, "조리가능수량을 초과하였습니다.","Message", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// 테이블에 삽입
					int selectAllprice = Integer.parseInt(selectproduct_num.getText()) * menuprice;// 수량 * 가격
					Vector<String> inputjt = new Vector<String>(); // 데이터값
					inputjt.add(Integer.toString(mealNum)); // 선택한 메뉴 번호
					inputjt.add(selectmealName); // 선택한 메뉴 이름
					inputjt.add(selectproduct_num.getText()); // 선택한메뉴 수량
					inputjt.add(Integer.toString(selectAllprice)); // 선택한 메뉴 총금액(수량 * 금액)
					allprice += selectAllprice; // 총금액에 더해주기
					allpricetxt.setText(String.format("%,d", allprice) + "원"); // 1000단위 ,
						
					// 입력한 레코드 버튼 비활성화 시키기
					for(int i=0; i<Menubtns.size(); i++) {
						if(mealNum == Menubtns.get(i).mealNo) {
							Menubtns.get(i).setEnabled(false);
						}
					}
					// 테이블 업데이트
					rowData.add(inputjt); // 데이터값 추가
					jt.updateUI(); // 테이블 update
						
					// 선택한 품명, 수량 
					selectproduct.setText(""); // 선택한 품명 field 공백으로 
					selectnum_s = ""; // 선택한 버튼 수량 연결할 텍스트 초기화
					selectproduct_num.setText(""); // 선택한 품목 수량 입력폼 공백
				}
			});
			// 초기화 버튼 / 선택한 품명과 수량 입력폼 초기화 
			JButton resetbtn = new JButton("초기화");
			resetbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectnum_s = ""; // 선택한 버튼 수량 연결할 텍스트 초기화
					selectproduct_num.setText(""); // 선택한 품목 수량 입력폼 공백
				}
			});
			
			panel3.add(inputbtn, BorderLayout.CENTER);  // 입력 버튼 panel3의 중간에
			panel3.add(resetbtn, BorderLayout.SOUTH); // 초기화 버튼 panel3의 하단에
				
			this.add(panel1, BorderLayout.CENTER); // 숫자 입력폼 
			this.add(panel3, BorderLayout.EAST); // 입력, 초기화
		}
	}
	// East 안의 South에 올릴 Panel
	class EastS extends JPanel{
		private JButton buybtn; // 결제버튼
		private JButton deletebtn; // 취소버튼
		public EastS() {
			this.setLayout(new GridLayout(1,2));
			buybtn = new JButton("결제");
			// 결제버튼 클릭시
			buybtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 결제자 인증폼으로
					int result = JOptionPane.showConfirmDialog(null,new BuyOptionPane(), "결제자 인증",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
					if(result==JOptionPane.OK_OPTION) { //확인 눌렀을 때
						String sql = "select * from member where memberNo=? and passwd=?"; // 비밀번호 확인
						try {
							PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
							psmt.setString(1, selectmemberNum); // 콤보박스에서 선택한 사원번호
							psmt.setString(2, new String(memberpw.getPassword())); // 입력받은 비밀번호 
							ResultSet rs = psmt.executeQuery(); // sql문 실행
							if(rs.next()) { // 비밀번호가 일치하면 식권폼으로
								Calendar cal = Calendar.getInstance(); // 현재 컴퓨터의 날짜 시간을 가져옴
								int year = cal.get(Calendar.YEAR); // 년
								int month = cal.get(Calendar.MONTH)+1; // 월
								int day  = cal.get(Calendar.DAY_OF_MONTH); // 일
								int hour = cal.get(Calendar.HOUR_OF_DAY); // 시
								int min = cal.get(Calendar.MINUTE); // 분
								int sec = cal.get(Calendar.SECOND); // 초
								String alltime = Integer.toString(year) + Integer.toString(month) + Integer.toString(day) + Integer.toString(hour) + Integer.toString(min) + Integer.toString(sec); // 결제한 날짜 
								// 조리가능수량 테이블에서 수정
								for(int i=0; i<model.getRowCount(); i++) {
									String sql2 = "update meal set maxCount=? where mealName=?";//테이블 수정
									try {
										PreparedStatement psmt2 = (PreparedStatement) con.prepareStatement(sql2);
										int updateMaxnum = maxCountnum - Integer.parseInt((String) model.getValueAt(i, 2)); // 조리가능수량에서 i번째의 수량(2번째)을 뺌
										psmt2.setInt(1, updateMaxnum); // 수정할 조리가능수량
										psmt2.setString(2, (String) model.getValueAt(i, 1)); // i번째의 메뉴이름(1번째)를 뺌
										int re = psmt2.executeUpdate(); // sql문 실행
										if(i==model.getRowCount()-1 && re>0) {
											JOptionPane.showMessageDialog(null, "결제가 완료되었습니다.\n식권을 출력합니다.","Message",JOptionPane.INFORMATION_MESSAGE);
											dispose();
											// 선택한 사원번호와 메뉴종류번호, 현재시간 텍스트, 선택한 메뉴들 데이터값 
											new TicketForm(selectmemberNum, CuisineNum, alltime, model);
										}
									}finally {}
								}
								// orderlist에 추가
								String add_dbstring = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day) + " " +Integer.toString(hour) + ":" + Integer.toString(min) + ":" +Integer.toString(sec);
								for(int i=0; i<model.getRowCount(); i++) { // 데이터의 열만큼
									// 주문리스트에 추가
									String sql2 = "insert into orderlist(cuisineNo, mealNo, memberNo, orderCount, amount, orderDate) values(?,?,?,?,?,?)";
									PreparedStatement psmt2 = (PreparedStatement) con.prepareStatement(sql2);
									psmt2.setInt(1, Integer.parseInt(CuisineNum)); // 메뉴 종류 숫자
									psmt2.setString(2, (String) model.getValueAt(i, 0)); // 메뉴 번호
									psmt2.setInt(3,Integer.parseInt(selectmemberNum)); // 사원번호
									psmt2.setString(4, (String) model.getValueAt(i, 2)); // 수량
									psmt2.setString(5, (String) model.getValueAt(i, 3)); // 총가격
									psmt2.setString(6, add_dbstring); // 날짜
									psmt2.execute(); // sql문 실행
								}
							}else { // 비밀번호가 일치하지 않을 때
								JOptionPane.showMessageDialog(null, "패스워드가 일치하지 않습니다.","Message", JOptionPane.ERROR_MESSAGE);
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			deletebtn = new JButton("취소");
			deletebtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 선택한 품명, 수량 초기화
					selectnum_s = "";
					selectproduct_num.setText("");
					selectproduct.setText("");
				}
			});
				
			this.add(buybtn);
			this.add(deletebtn);
		}
	}
	// 결제버튼 클릭시, 결제자 인증폼
	class BuyOptionPane extends JPanel{
		public BuyOptionPane() {
			this.setLayout(new GridLayout(2,2)); // 2행 2열
			JLabel label1 = new JLabel("사원번호");
			JLabel label2 = new JLabel("패스워드");
			memberpw = new JPasswordField(6);
			
			Vector<String> v = new Vector<String>(); // member 사원번호를 담을 벡터
			String sql = "select memberNo from member"; //  member 사원번호 select 
			try {
				Statement st = (Statement) con.createStatement();
				ResultSet rs = st.executeQuery(sql); // sql문 실행
				while(rs.next()) {
					v.add(rs.getString(1)); // 사원번호 벡터에 담기
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			memberNum = new JComboBox(v); // 사원번호 콤보박스
			memberNum.setSelectedIndex(0); // 사원번호 초기값
			selectmemberNum = memberNum.getSelectedItem().toString(); // 사원번호 초기값 
			
			memberNum.addActionListener(new ActionListener() { // 사원번호 콤보박스 이벤트, 다른 사원번호로 바꿀때 
				@Override
				public void actionPerformed(ActionEvent e) {
					selectmemberNum = memberNum.getSelectedItem().toString(); // 사원번호 전역변수에 담기
				}
			});
			
			this.add(label1);
			this.add(memberNum);
			this.add(label2);
			this.add(memberpw);
		}
	}
}
