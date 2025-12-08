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
    private String path;
    private String exPath;

    private JTable table;
    private DefaultTableModel model;
    private String[] header = {"영단어", "뜻"};

    public VocabFrame(VocabManager manager,String path, String exPath) {
        this.manager = manager;
        this.path = path;
        this.exPath = exPath;

        setTitle("단어장 프로그램");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        frame = getContentPane();
        frame.setLayout(new BorderLayout());

        initTable();     // 테이블 객체 먼저 생성
        initTopMenu();   //상단 메뉴(단어관리 / 퀴즈 / 유용한 기능 선택)
        initMainCards();  //중앙은 카드 레이아웃으로 배치, 여기서 WORD 카드 안에 테이블이 붙음
        refreshTable();  // 파일에서 읽어온 단어들 한 번 뿌리기

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

        // 가운데(CENTER) --> 각 기능별 화면 패널(카드)
        CardLayout wordCard = new CardLayout();
        JPanel wordCardPanel = new JPanel(wordCard);

        wordCardPanel.add(createAddPanel(), "ADD");
        wordCardPanel.add(createEditPanel(), "EDIT");
        wordCardPanel.add(createDeletePanel(), "DELETE");
//        wordCardPanel.add(createSearchPanel(), "SEARCH");

        panel.add(wordCardPanel, BorderLayout.CENTER);

        //아래(SOUTH)에 테이블 붙이기
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.SOUTH);

        //버튼 누르면 해당 이름 갖는 카드로 전환
        btnAdd.addActionListener(e -> wordCard.show(wordCardPanel, "ADD"));
        btnEdit.addActionListener(e -> wordCard.show(wordCardPanel, "EDIT"));
        btnDelete.addActionListener(e -> wordCard.show(wordCardPanel, "DELETE"));
        btnSearch.addActionListener(e -> wordCard.show(wordCardPanel, "SEARCH"));

        return panel;
    }

    //단어 추가하는 패널
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

    //테이블만 만들어두고,(준비 상태) 단어관리 패널에서 붙일거임
    private void initTable() {
        model = new DefaultTableModel(header, 0);
        table = new JTable(model);
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

    //단어 수정하는 패널
    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // ---- 1. 검색/불러오기 영역 ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfSearchEng = new JTextField(15);
        JButton btnLoad = new JButton("불러오기");

        top.add(new JLabel("수정할 영단어:"));
        top.add(tfSearchEng);
        top.add(btnLoad);

        panel.add(top, BorderLayout.NORTH);

        // ---- 2. 가운데: 현재 상태 + 수정 폼 ----
        JPanel center = new JPanel(new BorderLayout(10, 10));

        // 2-1) 현재 영어, 뜻 리스트, 예문
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));

        JLabel lblCurrentEng = new JLabel("현재 영어: -");

        DefaultListModel<String> korListModel = new DefaultListModel<>();
        JList<String> korList = new JList<>(korListModel);
        JScrollPane korScroll = new JScrollPane(korList);

        JTextArea taExample = new JTextArea(4, 30);
        taExample.setLineWrap(true);
        taExample.setWrapStyleWord(true);
        JScrollPane exScroll = new JScrollPane(taExample);

        infoPanel.add(lblCurrentEng, BorderLayout.NORTH);

        JPanel centerLists = new JPanel(new GridLayout(1, 2, 5, 5));
        centerLists.add(new JScrollPane(korList));
        centerLists.add(exScroll);

        infoPanel.add(centerLists, BorderLayout.CENTER);

        center.add(infoPanel, BorderLayout.CENTER);

        // 2-2) 아래쪽: 수정용 입력/버튼들
        JPanel editButtons = new JPanel();
        editButtons.setLayout(new GridLayout(3, 1, 5, 5));

        // (a) 영어 수정
        JPanel pEng = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfNewEng = new JTextField(15);
        JButton btnRenameEng = new JButton("영어 수정");
        pEng.add(new JLabel("새 영어:"));
        pEng.add(tfNewEng);
        pEng.add(btnRenameEng);

        // (b) 뜻 추가
        JPanel pAddKor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfAddKor = new JTextField(20);
        JButton btnAddKor = new JButton("뜻 추가");
        pAddKor.add(new JLabel("추가할 뜻(/로 구분):"));
        pAddKor.add(tfAddKor);
        pAddKor.add(btnAddKor);

        // (c) 뜻 삭제 + 예문 저장
        JPanel pKorEx = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDelKor = new JButton("선택 뜻 삭제");
        JButton btnSaveEx = new JButton("예문 저장");
        pKorEx.add(btnDelKor);
        pKorEx.add(btnSaveEx);

        editButtons.add(pEng);
        editButtons.add(pAddKor);
        editButtons.add(pKorEx);

        center.add(editButtons, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // ---- 내부에서 사용할 상태 ----
        final Word[] currentWord = new Word[1]; // 람다에서 변경하려고 배열로

        // ---- 3. 불러오기 버튼 동작 ----
        btnLoad.addActionListener(e -> {
            String eng = tfSearchEng.getText().trim();
            if (eng.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "영단어를 입력하세요.");
                return;
            }

            Word w = manager.findExact(eng);
            if (w == null) {
                JOptionPane.showMessageDialog(panel, "해당 단어가 존재하지 않습니다.");
                return;
            }
            currentWord[0] = w;

            // 화면에 현재 상태 반영
            lblCurrentEng.setText("현재 영어: " + w.getEng());

            korListModel.clear();
            for (String k : w.getKors()) {
                korListModel.addElement(k);
            }

            String ex = manager.getExampleMap().get(w.getEng());
            taExample.setText(ex != null ? ex : "");
        });

        // ---- 4. 영어 수정 버튼 ----
        btnRenameEng.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(panel, "먼저 단어를 불러와 주세요.");
                return;
            }
            String oldEng = currentWord[0].getEng();
            String newEng = tfNewEng.getText().trim();

            String msg = manager.renameEng(oldEng, newEng);
            JOptionPane.showMessageDialog(panel, msg);

            // 성공 시 화면 갱신
            if (msg.startsWith("영어 단어가")) {
                // currentWord[0]의 eng도 이미 바뀐 상태
                lblCurrentEng.setText("현재 영어: " + currentWord[0].getEng());
                refreshTable(); // 전체 테이블 갱신
            }
        });

        // ---- 5. 뜻 추가 버튼 ----
        btnAddKor.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(panel, "먼저 단어를 불러와 주세요.");
                return;
            }
            String eng = currentWord[0].getEng();
            String korLine = tfAddKor.getText().trim();
            String msg = manager.addKorMeanings(eng, korLine);
            JOptionPane.showMessageDialog(panel, msg);

            // 성공이면 리스트 갱신
            korListModel.clear();
            for (String k : currentWord[0].getKors()) {
                korListModel.addElement(k);
            }
            refreshTable();
        });

        // ---- 6. 선택 뜻 삭제 버튼 ----
        btnDelKor.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(panel, "먼저 단어를 불러와 주세요.");
                return;
            }
            int sel = korList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(panel, "삭제할 뜻을 선택해주세요.");
                return;
            }
            String eng = currentWord[0].getEng();
            // 1-based index로 넘기기
            String msg = manager.removeKorMeaning(eng, sel + 1);
            JOptionPane.showMessageDialog(panel, msg);

            korListModel.clear();
            for (String k : currentWord[0].getKors()) {
                korListModel.addElement(k);
            }
            refreshTable();
        });

        // ---- 7. 예문 저장 버튼 ----
        btnSaveEx.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(panel, "먼저 단어를 불러와 주세요.");
                return;
            }
            String eng = currentWord[0].getEng();
            String newEx = taExample.getText();
            String msg = manager.updateExample(eng, newEx);
            JOptionPane.showMessageDialog(panel, msg);
        });

        return panel;
    }

    // 삭제 패널
    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // ---- 1. 상단: 삭제할 단어 입력 + 불러오기 ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfEng = new JTextField(15);
        JButton btnLoad = new JButton("불러오기");

        top.add(new JLabel("삭제할 영단어:"));
        top.add(tfEng);
        top.add(btnLoad);

        panel.add(top, BorderLayout.NORTH);

        // ---- 2. 가운데: 단어 정보 미리보기 ----
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel lblEng   = new JLabel("영어: -");
        JLabel lblKors  = new JLabel("뜻: -");
        JLabel lblEx    = new JLabel("예문: -");

        // 줄이 너무 길어질 수 있으니 스크롤 가능하게
        JTextArea taInfo = new JTextArea(5, 30);
        taInfo.setEditable(false);
        taInfo.setLineWrap(true);
        taInfo.setWrapStyleWord(true);

        center.add(lblEng);
        center.add(lblKors);
        center.add(lblEx);
        center.add(new JScrollPane(taInfo));

        panel.add(center, BorderLayout.CENTER);

        // ---- 3. 하단: 삭제 버튼 ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("삭제");
        bottom.add(btnDelete);
        panel.add(bottom, BorderLayout.SOUTH);

        // 현재 선택된 단어를 기억할 용도
        final Word[] currentWord = new Word[1];

        // ---- 4. 불러오기 버튼 동작 ----
        btnLoad.addActionListener(e -> {
            String eng = tfEng.getText().trim();
            if (eng.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "영단어를 입력하세요.");
                return;
            }

            Word w = manager.findExact(eng);
            if (w == null) {
                JOptionPane.showMessageDialog(panel, "해당 단어가 존재하지 않습니다.");
                currentWord[0] = null;
                lblEng.setText("영어: -");
                lblKors.setText("뜻: -");
                lblEx.setText("예문: -");
                taInfo.setText("");
                return;
            }

            currentWord[0] = w;

            // 화면에 정보 표시
            lblEng.setText("영어: " + w.getEng());
            String kors = String.join("/ ", w.getKors());
            lblKors.setText("뜻: " + kors);

            String ex = manager.getExampleMap().get(w.getEng());
            if (ex == null || ex.trim().isEmpty()) {
                ex = "(등록된 예문 없음)";
            }
            lblEx.setText("예문: " + ex);

            // 밑에 요약 정보도 보여주기
            taInfo.setText(
                    "영어: " + w.getEng() + "\n" +
                            "뜻: " + kors + "\n" +
                            "예문: " + ex + "\n" +
                            "틀린 횟수: " + w.getWrong_number()
            );
        });

        // ---- 5. 삭제 버튼 동작 ----
        btnDelete.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(panel, "먼저 삭제할 단어를 불러와 주세요.");
                return;
            }

            String eng = currentWord[0].getEng();

            int result = JOptionPane.showConfirmDialog(
                    panel,
                    "'" + eng + "' 단어를 정말 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                String msg = manager.deleteWord(eng);
                JOptionPane.showMessageDialog(panel, msg);

                // UI 초기화
                currentWord[0] = null;
                tfEng.setText("");
                lblEng.setText("영어: -");
                lblKors.setText("뜻: -");
                lblEx.setText("예문: -");
                taInfo.setText("");

                // 테이블도 갱신
                refreshTable();
            }
        });

        return panel;
    }





}



