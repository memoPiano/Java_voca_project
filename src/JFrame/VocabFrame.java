package JFrame;

import Service.VocabManager;
import Vocab.Word;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VocabFrame extends JFrame {

    private Container frame;  //이게 메인 프레임(부모 컨테이너)
    private CardLayout mainCard;
    private JPanel mainCardPanel;

    private VocabManager manager;
    private JTable table;
    private DefaultTableModel model;
    private String[] header = {"영단어", "뜻"};

    public VocabFrame(VocabManager manager) {
        this.manager = manager;

        setTitle("단어장 프로그램");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        frame = getContentPane();
        frame.setLayout(new BorderLayout());

        initTopMenu();   //상단 메뉴(단어관리 / 퀴즈 / 유용한 기능 선택)
        initMainCards();  //중앙은 카드 레이아웃으로 배치
        initTable();  //단어장 테이블 만들기

        setVisible(true);
    }

    //북쪽에 달릴 메뉴
    private void initTopMenu() {
        JPanel northPanel = new JPanel();

        JButton btnWord = new JButton("단어 관리");
        JButton btnQuiz = new JButton("퀴즈");
        JButton btnUtil = new JButton("유용한 기능");

        northPanel.add(btnWord);
        northPanel.add(btnQuiz);
        northPanel.add(btnUtil);

        frame.add(northPanel, BorderLayout.NORTH);

        btnWord.addActionListener(e -> mainCard.show(mainCardPanel, "WORD"));
        btnQuiz.addActionListener(e -> mainCard.show(mainCardPanel, "QUIZ"));
        btnUtil.addActionListener(e -> mainCard.show(mainCardPanel, "UTIL"));
    }


    //중앙 카드 레이아웃
    private void initMainCards() {
        mainCard = new CardLayout();
        mainCardPanel = new JPanel(mainCard);

        mainCardPanel.add(createWordManagePanel(), "WORD");  // (기능 1,2,3,4)
        //mainCardPanel.add(createQuizPanel(), "QUIZ");   //TODO : 퀴즈패널  (기능 5)
        //mainCardPanel.add(createUtilPanel(), "UTIL");   //TODO : 유틸 패널 (기능 6,7,8,9)

        frame.add(mainCardPanel, BorderLayout.CENTER);
    }

    private JPanel createWordManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 왼쪽(WEST) 메뉴, 단어 관리 버튼 4개
        JPanel menu = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton btnAdd = new JButton("단어 추가");
        JButton btnEdit = new JButton("단어 수정");
        JButton btnDelete = new JButton("단어 삭제");
        JButton btnSearch = new JButton("단어 검색");

        menu.add(btnAdd);
        menu.add(btnEdit);
        menu.add(btnDelete);
        menu.add(btnSearch);

        panel.add(menu, BorderLayout.WEST);

        // 가운데(CENTER) --> 각 기능별 화면 패널
        CardLayout wordCard = new CardLayout();
        JPanel wordCardPanel = new JPanel(wordCard);

        wordCardPanel.add(createAddPanel(), "ADD");
//        wordCardPanel.add(createEditPanel(), "EDIT");
//        wordCardPanel.add(createDeletePanel(), "DELETE");
//        wordCardPanel.add(createSearchPanel(), "SEARCH");

        panel.add(wordCardPanel, BorderLayout.CENTER);

        //버튼 누르면 해당 이름 갖는 카드로 전환
        btnAdd.addActionListener(e -> wordCard.show(wordCardPanel, "ADD"));
        btnEdit.addActionListener(e -> wordCard.show(wordCardPanel, "EDIT"));
        btnDelete.addActionListener(e -> wordCard.show(wordCardPanel, "DELETE"));
        btnSearch.addActionListener(e -> wordCard.show(wordCardPanel, "SEARCH"));

        return panel;
    }

    private JPanel createAddPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // ----- 입력 폼 영역 -----
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel lblEng = new JLabel("영단어:");
        JTextField tfEng = new JTextField(15);

        JLabel lblKor = new JLabel("한글 뜻 (/로 여러 개):");
        JTextField tfKor = new JTextField(20);

        JLabel lblEx = new JLabel("예문 (선택):");
        JTextField tfEx = new JTextField(30);

        form.add(lblEng);
        form.add(tfEng);
        form.add(lblKor);
        form.add(tfKor);
        form.add(lblEx);
        form.add(tfEx);

        panel.add(form, BorderLayout.NORTH);

        // ----- 버튼 영역 -----
        JButton btnAdd = new JButton("추가");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // ----- 버튼 동작 -----
        btnAdd.addActionListener(e -> {
            String eng = tfEng.getText();
            String korLine = tfKor.getText();
            String ex = tfEx.getText();

            // VocabManager의 추가 메서드 호출
            String msg = manager.addVocabCore(eng, korLine, ex);

            // 메시지 다이얼로그로 보여주기
            JOptionPane.showMessageDialog(panel, msg, "단어 추가", JOptionPane.INFORMATION_MESSAGE);

            // 성공적으로 추가됐다면 입력창 비우고 테이블 새로고침
            if (msg.contains("새로 추가") || msg.contains("새 뜻")) {
                tfEng.setText("");
                tfKor.setText("");
                tfEx.setText("");

                // 만약 전체 단어를 보여주는 JTable이 있다면 여기서 갱신
                refreshTable();  // <- MainFrame에 이런 메서드 하나두고 여기서 호출
            }
        });

        return panel;
    }

    private void initTable() {
        model = new DefaultTableModel(header, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
    }

    // manager.voc --> 테이블 model 에 반영
    public void refreshTable() {
        model.setRowCount(0); // 기존 행 제거

        for (Word w : manager.getVoc()) {
            String eng = w.getEng();
            String kor = String.join("/ ", w.getKors());
            model.addRow(new Object[]{eng, kor});
        }
    }



}



