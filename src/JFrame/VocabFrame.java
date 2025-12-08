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
        //initTable();  //단어장 테이블 만들기

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
        mainCardPanel.add(createUtilPanel(), "UTIL");   //TODO : 유틸 패널 (기능 6,7,8,9)

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

        panel.add(wordCardPanel, BorderLayout.NORTH); //센터에서 바꿈

        //수정
        model = new DefaultTableModel(header, 0);
        table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

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



    private JPanel createUtilPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. 왼쪽(WEST) 메뉴 버튼 구성
        JPanel menu = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton btnBookmark = new JButton("즐겨찾기");
        JButton btnWrongNote = new JButton("오답 노트");
        JButton btnRetest = new JButton("오답 재시험");
        JButton btnRandom = new JButton("오늘의 단어");

        menu.add(btnBookmark);
        menu.add(btnWrongNote);
        menu.add(btnRetest);
        menu.add(btnRandom);

        panel.add(menu, BorderLayout.WEST);

        // 2. 중앙(CENTER) 카드 레이아웃 구성
        CardLayout utilCard = new CardLayout();
        JPanel utilCardPanel = new JPanel(utilCard);

        // 각 기능별 패널 생성 및 추가
        utilCardPanel.add(createBookmarkPanel(), "BOOKMARK");  // 즐겨찾기 패널
        utilCardPanel.add(createWrongNotePanel(), "WRONG");    // 오답노트 패널
        utilCardPanel.add(createRetestPanel(), "RETEST");      // 재시험 패널
        utilCardPanel.add(createRandomWordPanel(), "RANDOM");  // 오늘의 단어 패널

        panel.add(utilCardPanel, BorderLayout.CENTER);

        // 3. 버튼 이벤트 연결 (화면 전환 & 데이터 갱신)
        btnBookmark.addActionListener(e -> {
            refreshBookmarkTable(); // 즐겨찾기 목록 최신화
            utilCard.show(utilCardPanel, "BOOKMARK");
        });

        btnWrongNote.addActionListener(e -> {
            refreshWrongNoteTable(); // 오답 목록 최신화
            utilCard.show(utilCardPanel, "WRONG");
        });

        btnRetest.addActionListener(e -> utilCard.show(utilCardPanel, "RETEST"));

        btnRandom.addActionListener(e -> {
            updateRandomWords(); // 랜덤 단어 새로고침
            utilCard.show(utilCardPanel, "RANDOM");
        });

        return panel;
    }

    //  1. 즐겨찾기 패널

    private DefaultTableModel bookmarkModel;

    private JPanel createBookmarkPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("★ 즐겨찾기 목록 ★", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // 테이블 생성
        bookmarkModel = new DefaultTableModel(new String[]{"영단어", "뜻"}, 0);
        JTable table = new JTable(bookmarkModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 하단 버튼 (즐겨찾기 해제 기능)
        JButton btnDelete = new JButton("선택한 단어 즐겨찾기 해제");
        panel.add(btnDelete, BorderLayout.SOUTH);

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "해제할 단어를 선택해주세요.");
                return;
            }
            String eng = (String) bookmarkModel.getValueAt(row, 0);

            // 매니저를 통해 즐겨찾기 해제 처리
            for(Word w : manager.getVoc()) {
                if(w.getEng().equals(eng)) {
                    w.setBookMark(false);
                    break;
                }
            }
            refreshBookmarkTable(); // 테이블 갱신
            JOptionPane.showMessageDialog(panel, "즐겨찾기가 해제되었습니다.");
        });

        return panel;
    }

    // 즐겨찾기 테이블 갱신 메서드
    private void refreshBookmarkTable() {
        bookmarkModel.setRowCount(0);
        for (Word w : manager.getVoc()) {
            if (w.isBookMark()) {
                bookmarkModel.addRow(new Object[]{w.getEng(), String.join(", ", w.getKors())});
            }
        }
    }

    //  2. 오답노트 패널

    private DefaultTableModel wrongNoteModel;
    private JLabel lblWrongStats; // 통계 라벨

    private JPanel createWrongNotePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단 통계 표시
        lblWrongStats = new JLabel("학습 통계 로딩 중...", JLabel.CENTER);
        lblWrongStats.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        panel.add(lblWrongStats, BorderLayout.NORTH);

        // 테이블 생성
        wrongNoteModel = new DefaultTableModel(new String[]{"영단어", "뜻", "틀린 횟수"}, 0);
        JTable table = new JTable(wrongNoteModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // 오답노트 갱신 메서드
    private void refreshWrongNoteTable() {
        wrongNoteModel.setRowCount(0);
        int totalWrong = 0;

        java.util.List<Word> list = new java.util.ArrayList<>(manager.getVoc());
        list.sort((w1, w2) -> w2.getWrong_number() - w1.getWrong_number());

        for (Word w : list) {
            if (w.getWrong_number() > 0) {
                wrongNoteModel.addRow(new Object[]{
                        w.getEng(),
                        String.join(", ", w.getKors()),
                        w.getWrong_number() + "회"
                });
                totalWrong++;
            }
        }

        // 통계 멘트 업데이트
        lblWrongStats.setText("총 틀린 단어: " + totalWrong + "개 (틀린 횟수 내림차순 정렬)");
    }


    //  3. 오늘의 단어 (랜덤 추천) 패널

    private JTextArea taRandomWords;

    private JPanel createRandomWordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("♣ 오늘의 추천 단어 (5개) ♣", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        taRandomWords = new JTextArea();
        taRandomWords.setEditable(false);
        taRandomWords.setFont(new Font("Monospaced", Font.PLAIN, 16));
        panel.add(new JScrollPane(taRandomWords), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("다른 단어 보기");
        panel.add(btnRefresh, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> updateRandomWords());

        return panel;
    }

    private void updateRandomWords() {
        java.util.List<Word> voc = manager.getVoc();
        if (voc.isEmpty()) {
            taRandomWords.setText("단어장이 비어있습니다.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        java.util.Random rand = new java.util.Random();

        // 5개 랜덤 뽑기
        for(int i=0; i<5; i++) {
            Word w = voc.get(rand.nextInt(voc.size()));
            sb.append("[").append(i+1).append("] ").append(w.getEng()).append("\n");
            sb.append("   뜻: ").append(String.join(", ", w.getKors())).append("\n");

            String ex = manager.getExampleMap().get(w.getEng());
            if(ex != null) sb.append("   예문: ").append(ex).append("\n");
            sb.append("\n----------------------------------\n");
        }
        taRandomWords.setText(sb.toString());
    }


    //  4. 재시험 패널 (미완)

    private JPanel createRetestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel msg = new JLabel("재시험 기능 아직 구현안함", JLabel.CENTER);

        // 퀴즈 패널을 재활용하는 게 좋을 것 같아서 아직 안했습니다

        panel.add(msg, BorderLayout.CENTER);
        return panel;
    }



}



