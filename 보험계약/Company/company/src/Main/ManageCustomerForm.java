package Main;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// 계약관리폼
public class ManageCustomerForm extends JFrame{
	private JComboBox combo1; // 고객명 콤보박스
	private JComboBox combo2; // 보험상품 콤보박스
	private JComboBox combo3; // 담당자 콤보박스
	private JTextField [] jf1; // 왼쪽 입력폼
	private JTextField [] jf2; // 오른쪽 입력폼
	private JTable jt; // 조회된 결과값을 넣을 테이블
	private String [] col_s = {"customerCode", "contractName", "regPrice", "regDate", "monthPrice", "adminName"}; // 컬럼명 배열
	private Vector<String> colData = new Vector<String>(); // 컬럼명
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>(); // 데이터가 들어갈 벡터
	private Connection con = Driver_con.Driver_con(); // 데이터베이스 연결
	public ManageCustomerForm() {
		setTitle("보험 계약");
		// x버튼을 클릭했을 때 메인폼으로
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {new MainForm();}
		});
		
		getContentPane().add(new NorthP(), BorderLayout.NORTH);
		getContentPane().add(new CenterP(), BorderLayout.CENTER);
		
		setSize(600,600);
		setVisible(true);
	}
	// sql문에 해당하는 데이터값을 반환하는 함수
	public Vector<String> ReVector(String sql,int i) {
		Vector<String> v = new Vector<String>();
		try {
			Statement st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				for(int j=0; j<i; j++) {
					v.add(rs.getString(j+1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}
	// 상단
	class NorthP extends JPanel{
		public NorthP() {
			this.setLayout(new GridLayout(2,1,40,0)); // 2행 1열 hgap 40
			JPanel panel = new JPanel(); // 입력폼이 들어갈 panel
			panel.setLayout(new GridLayout(1,2,40,0)); // 1행 2열
			
			panel.add(new Panel1());
			panel.add(new Panel2());
			this.add(panel);
			this.add(new Panel3());
		}
	}
	// 입력폼이 들어갈 panel1, 왼쪽
	class Panel1 extends JPanel{
		String [] s = {"고객코드:", "고객명:", "생년월일:", "연락처"};
		String sql1 = "select name from customer"; // 고객명을 뽑아낼 sql문
		String selectedName = ""; // 선택된 이름
		int count = 0;
		public Panel1() {
			this.setLayout(new GridLayout(4,2,10,0)); // 4행 2열 hgap 10
			combo1 = new JComboBox(ReVector(sql1,1)); // combobox에 빼온 데이터값 추가
			jf1 = new JTextField[s.length-1];
			
			for(int i=0; i<s.length; i++) {
				this.add(new JLabel(s[i])); // 입력폼의 타이틀 라벨 추가
				if(i==1) { // 고객명은 콤보 박스
					combo1.addActionListener(new ActionListener() { // 콤보박스를 선택하면 발생하는 이벤트
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							selectedName = combo1.getSelectedItem().toString();
							String sql2 = "select code, birth, tel from customer where name='" + selectedName + "'"; // 선택한 이름의 데이터 값을 가져올 sql문, 고객코드,생년월일, 전화번호
							Vector<String> v = ReVector(sql2, 3); 
							// 데이터값을 입력폼에 넣기
							jf1[0].setText(v.get(0));
							jf1[1].setText(v.get(1));
							jf1[2].setText(v.get(2));
						}
					});
					this.add(combo1);
				}else {
					jf1[count] = new JTextField(20);
					this.add(jf1[count]);
					count++;
				}
			}
		}
	}
	// 입력폼이 들어갈 panel2, 오른쪽
	class Panel2 extends JPanel{
		String [] s = {"보험상품", "가입금액:", "월보험료:"};
		String sql = "select distinct contractName from contract";
		int count = 0;
		public Panel2() {
			this.setLayout(new GridLayout(3,2,10,0)); // 3행 2열 hgap 10
			jf2 = new JTextField[s.length-1];
			
			for(int i=0; i<s.length; i++) {
				this.add(new JLabel(s[i]));
				if(i==0) { // 보험상품은 콤보박스
					combo2 = new JComboBox(ReVector(sql,1));
					this.add(combo2);
				}else {
					jf2[count] = new JTextField(20);
					this.add(jf2[count]);
					count++;
				}
			}
		}
	}
	// 버튼이 들어갈 panel, 입력폼 밑에 
	class Panel3 extends JPanel{
		String sql = "select name from admin";
		String [] btns_s = {"가입", "삭제", "파일로저장", "닫기"};
		JButton [] btns;
		public Panel3() {
			this.add(new JLabel("담당자:"));
			
			combo3 = new JComboBox(ReVector(sql,1));
			this.add(combo3);
			
			btns = new JButton[btns_s.length];
			for(int i=0; i<btns.length; i++) {
				btns[i] = new JButton(btns_s[i]);
				btns[i].addActionListener(new BtnListener());
				this.add(btns[i]);
			} 
		}
	}
	// 버튼 이벤트 리스너
	class BtnListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			switch(e.getActionCommand()) {
				case "가입":
					Join();
					break;
				case "삭제":
					Delete();
					break;
				case "파일로저장":
					SaveFile();
					break;
				case "닫기":
					dispose();
					new MainForm();
					break;
			}
		}
	}
	// 중간, 가입하면 밑에 결과화면 나오는 곳
	class CenterP extends JPanel{
		public CenterP() {
			for(int i=0; i<col_s.length; i++) {
				colData.add(col_s[i]);
			}
			DefaultTableModel model = new DefaultTableModel(rowData,colData);
			jt = new JTable(model);
			JScrollPane jsp = new JScrollPane(jt);
			
			this.add(jsp);
		}
	}
	// 가입 후, 고객코드로 된 보험계약을 최근가입일 순서로 보여주는 함수
	private void SelectedContract(String code){
		rowData.removeAllElements();
		Vector<String> v;
		String sql = "select * from contract where customerCode='" + code + "' order by regDate desc";
		Statement st;
		try {
			st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				v = new Vector<String>();
				for(int i=0; i<col_s.length; i++) {v.add(rs.getString(i+1));}
				rowData.add(v);
			}
			
			jt.updateUI();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 가입
	private void Join() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date time = new Date();
		String date = format1.format(time); // 현재 날짜
		
		String sql = "insert into contract values(?,?,?,?,?,?)";
		try {
			PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
			// 각 입력값을 넣어줌
			psmt.setString(1, jf1[0].getText());
			psmt.setString(2, combo2.getSelectedItem().toString());
			psmt.setString(3, jf2[0].getText());
			psmt.setString(4, date);
			psmt.setString(5, jf2[1].getText());
			psmt.setString(6, combo3.getSelectedItem().toString());
			
			int rs = psmt.executeUpdate(); // sql문 실행
			if(rs==1) { // 성공시
				JOptionPane.showMessageDialog(null, "고객추가가 완료되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
				SelectedContract(jf1[0].getText()); // 추가한 고객의 보험계약을 보여줌
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 삭제
	private void Delete() {
		int selectrow = jt.getSelectedRow(); // 선택한 행의 인덱스
		Vector<String> data = rowData.get(selectrow); // 특정 인덱스에 있는 데이터값을 뽑아옴
		
		if(selectrow==-1) return; // 선택하지 않았으면 실행 x
		
		String id = data.get(0); // 고객코드
		String contractName = data.get(1); // 고객명
		String str = id + "(" + contractName + ")" + "을 삭제하시겠습니까?";
		
		int result = JOptionPane.showConfirmDialog(null, str ,"계약정보삭제", JOptionPane.YES_NO_OPTION);
		
		if(result== JOptionPane.YES_OPTION){ // 삭제 실행
			String sql = "delete from contract where customerCode=? and contractName=? and regPrice=? and regDate=? and monthPrice=? and adminName=?"; // 전부 확인
			try {
				PreparedStatement psmt = (PreparedStatement) con.prepareStatement(sql);
				for(int i=0; i<data.size(); i++) {psmt.setString(i+1, data.get(i));} // 데이터값 넣기
				int r = psmt.executeUpdate(); // sql문 실행
				if(r==1) { // 성공 시
					JOptionPane.showMessageDialog(null, "삭제되었습니다", "삭제", JOptionPane.INFORMATION_MESSAGE);
					SelectedContract(jf1[0].getText()); // 계약한 보험 조회
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 파일로 저장
	private void SaveFile() {
		JFrame f = new JFrame();
		f.setSize(300,400);
		f.setVisible(false);
		
		FileDialog dialog = new FileDialog(f, "텍스트 파일로 저장하기", FileDialog.SAVE);
		dialog.setVisible(true);
		
		String path = dialog.getDirectory() + dialog.getFile(); // 경로
		String selectsave = "select contractName, regPrice, regDate, monthPrice, adminName from contract where customerCode='" + jf1[0].getText() + "'";
		Statement st = null;
		
		try {
			FileWriter w = new FileWriter(path);
			w.write("고객명: " + combo1.getSelectedItem().toString() + "(" + jf1[0].getText() + ")\r\n");
			w.write("보험상품      \t 가입금액 \t가입일\t     월보험료\t담당자\r\n");
			st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(selectsave); 
			
			while(rs.next()) {
				w.write(rs.getString(1)+ "      \t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5) + "\r\n");
			}
			w.close();
		}catch(Exception e) {
			System.out.println("파일 오류");
		}
	}
}
