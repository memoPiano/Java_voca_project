package JFrame;

import Service.VocabManager;
import Vocab.Word;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public VocabFrame(VocabManager manager, String path, String exPath) {
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

    public VocabFrame(VocabManager vm) {
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

        mainCardPanel.add(createHomePanel(), "HOME");

        mainCardPanel.add(createWordManagePanel(), "WORD");  // (기능 1,2,3,4)
        mainCardPanel.add(createQuizPanel(), "QUIZ");   //TODO : 퀴즈패널  (기능 5)
        mainCardPanel.add(createUtilPanel(), "UTIL");   //TODO : 유틸 패널 (기능 6,7,8,9)

        frame.add(mainCardPanel, BorderLayout.CENTER);
    }

    private JPanel createQuizPanel() {
        JPanel mainPanel =new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new GridLayout(2,1,10,10));
        CardLayout card = new CardLayout();
        JPanel showPanel = new JPanel(card);
        mainPanel.add(btnPanel, BorderLayout.WEST);
        mainPanel.add(showPanel,BorderLayout.CENTER);

        JButton essayBtn = new JButton("주관식 퀴즈");
        JButton choiceBtn = new JButton("객관식 퀴즈");

        btnPanel.add(essayBtn);
        btnPanel.add(choiceBtn);
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(new JLabel("왼쪽에서 퀴즈 유형을 선택하세요.", JLabel.CENTER), BorderLayout.CENTER);

        showPanel.add(homePanel, "home");
        showPanel.add(addChoiceCard(), "choice");
        showPanel.add(addEssayCard(), "essay");

        card.show(showPanel, "home");

        choiceBtn.addActionListener(e -> card.show(showPanel,"choice"));
        essayBtn.addActionListener(e -> card.show(showPanel, "essay"));

        return mainPanel;
    }

    private JPanel addChoiceCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        List<Word> voc = manager.getVoc();

        if (voc.size() < 4) {
            panel.add(new JLabel("단어가 4개 이상 있어야 객관식 퀴즈를 진행할 수 있습니다.", JLabel.CENTER),
                    BorderLayout.CENTER);
            return panel;
        }

        int numQuestions = Math.min(5, voc.size());
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(numQuestions, 1, 0, 10));
        JScrollPane scroll = new JScrollPane(content);
        panel.add(scroll, BorderLayout.CENTER);

        ButtonGroup[] groups = new ButtonGroup[numQuestions];
        JRadioButton[][] choiceButtons = new JRadioButton[numQuestions][4];
        Word[] answers = new Word[numQuestions];
        Random rand = new Random();

        class QuizBuilder {
            void reset() {
                content.removeAll();

                for (int q = 0; q < numQuestions; q++) {
                    Word answer = voc.get(rand.nextInt(voc.size()));
                    answers[q] = answer;

                    List<String> kors = answer.getKors();
                    String korText = "";
                    for (int i = 0; i < kors.size(); i++) {
                        if (i > 0) {
                            korText += " / ";
                        }
                        korText += kors.get(i);
                    }

                    JPanel questionPanel = new JPanel(new BorderLayout(5, 5));

                    JLabel qLabel = new JLabel("Q" + (q + 1) + ". " + korText);
                    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    labelPanel.add(qLabel);
                    questionPanel.add(labelPanel, BorderLayout.NORTH);

                    ButtonGroup group = new ButtonGroup();
                    groups[q] = group;

                    List<Word> options = new ArrayList<>();
                    options.add(answer);

                    while (options.size() < 4) {
                        Word candidate = voc.get(rand.nextInt(voc.size()));
                        if (!options.contains(candidate)) {
                            options.add(candidate);
                        }
                    }

                    Collections.shuffle(options, rand);

                    JPanel choicesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    for (int i = 0; i < 4; i++) {
                        Word optionWord = options.get(i);
                        JRadioButton rb = new JRadioButton(optionWord.getEng());
                        choiceButtons[q][i] = rb;
                        group.add(rb);
                        choicesPanel.add(rb);
                    }

                    questionPanel.add(choicesPanel, BorderLayout.CENTER);
                    content.add(questionPanel);
                }

                content.revalidate();
                content.repaint();
            }
        }

        QuizBuilder builder = new QuizBuilder();
        builder.reset();

        JButton submitBtn = new JButton("채점하기");
        panel.add(submitBtn, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> {
            int correct = 0;
            int wrong = 0;
            List<Word> wrongWords = new ArrayList<>();

            for (int i = 0; i < groups.length; i++) {
                ButtonGroup g = groups[i];
                if (g == null) {
                    continue;
                }
                Word answer = answers[i];
                if (answer == null) {
                    continue;
                }

                int selectedIndex = -1;
                for (int j = 0; j < 4; j++) {
                    JRadioButton rb = choiceButtons[i][j];
                    if (rb != null && rb.isSelected()) {
                        selectedIndex = j;
                        break;
                    }
                }

                int correctIndex = -1;
                for (int j = 0; j < 4; j++) {
                    JRadioButton rb = choiceButtons[i][j];
                    if (rb != null && rb.getText().equals(answer.getEng())) {
                        correctIndex = j;
                        break;
                    }
                }

                if (selectedIndex != -1 && selectedIndex == correctIndex) {
                    correct++;
                } else {
                    wrong++;
                    answer.setWrong_number(answer.getWrong_number() + 1);
                    if (!wrongWords.contains(answer)) {
                        wrongWords.add(answer);
                    }
                }
            }

            String msg = "총 " + (correct + wrong) + "문제 중 " + correct + "개 정답, " + wrong + "개 오답\n";
            if (!wrongWords.isEmpty()) {
                msg += "\n틀린 단어 목록:\n";
                for (Word w : wrongWords) {
                    msg += "- " + w.getEng() + " (현재 오답 횟수: " + w.getWrong_number() + "회)\n";
                }
            }
            JOptionPane.showMessageDialog(panel, msg);

            builder.reset();
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                builder.reset();
            }
        });

        return panel;
    }



    private JPanel addEssayCard() {
        JPanel essayPanel = new JPanel(new BorderLayout(10,10));
        JLabel title = new JLabel("한글 뜻을 보고 영어 단어를 입력하세요", JLabel.CENTER);
        essayPanel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2,1,5,5));
        JLabel korLabel = new JLabel("한글 뜻: ", JLabel.CENTER);
        JPanel korPanel =new JPanel(new FlowLayout(FlowLayout.CENTER));
        korPanel.add(korLabel);

        JPanel userInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel input = new JLabel("입력란: ");
        JTextField answer = new JTextField(20);
        userInputPanel.add(input);
        userInputPanel.add(answer);

        center.add(korPanel);
        center.add(userInputPanel);

        essayPanel.add(center,BorderLayout.CENTER);

        List<Word> voc = manager.getVoc();
        Word[] currentWord = new Word[1];
        Random rand = new Random();

        if(voc.isEmpty()){
            korLabel.setText("단어장이 비어 있어 퀴즈를 진행할 수 없습니다.");
            return essayPanel;
        }

        essayPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if(voc.isEmpty()){
                    korLabel.setText("단어장이 비어 있어 퀴즈를 진행할 수 없습니다.");
                    return;
                }
                Word w = voc.get(rand.nextInt(voc.size()));
                List<String> kors = w.getKors();
                String kor = kors.get(rand.nextInt(kors.size()));

                currentWord[0] = w;
                korLabel.setText("한글 뜻: " + kor);
                answer.setText("");
                answer.requestFocusInWindow();
            }
        });

        answer.addActionListener(e ->
        {
            if (currentWord[0] == null)
                return;

            String userInput = answer.getText().trim();
            if (userInput.isEmpty()) {
                JOptionPane.showMessageDialog(essayPanel, "영어 단어를 입력하세요.");
                return;
            }

            String correct = currentWord[0].getEng();
            if (userInput.equals(correct)) {
                JOptionPane.showMessageDialog(essayPanel, "정답입니다! (" + correct + ")");
            } else {
                int beforeWrong = currentWord[0].getWrong_number();
                currentWord[0].setWrong_number(beforeWrong + 1);

                JOptionPane.showMessageDialog(essayPanel, "틀렸습니다.\n정답: " + correct);
            }

            Container parent = essayPanel.getParent();
            CardLayout c = (CardLayout) parent.getLayout();
            c.show(parent, "home");
        });

        return essayPanel;
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
        wordCardPanel.add(createSearchPanel(), "SEARCH");

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

        // ----- 입력 폼 전체 래퍼 (여백 + 세로 정렬) -----
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        //위/왼/아래/오 여백: 화면에 좀 여유를 줌

        // 공통으로 쓸 헬퍼: 라벨 + 텍스트필드 한 줄 만들기
        JPanel rowEng = new JPanel(new BorderLayout(10, 0));
        JLabel lblEng = new JLabel("영단어:");
        JTextField tfEng = new JTextField(); // 컬럼 수는 굳이 안 줘도 됨 (자동으로 늘어남)
        rowEng.add(lblEng, BorderLayout.WEST);
        rowEng.add(tfEng, BorderLayout.CENTER);

        JPanel rowKor = new JPanel(new BorderLayout(10, 0));
        JLabel lblKor = new JLabel("한글 뜻 (/로 여러 개):");
        JTextField tfKor = new JTextField();
        rowKor.add(lblKor, BorderLayout.WEST);
        rowKor.add(tfKor, BorderLayout.CENTER);

        JPanel rowEx = new JPanel(new BorderLayout(10, 0));
        JLabel lblEx = new JLabel("예문 (선택):");
        JTextField tfEx = new JTextField();
        rowEx.add(lblEx, BorderLayout.WEST);
        rowEx.add(tfEx, BorderLayout.CENTER);


        // 행들을 세로로 쌓기
        formWrapper.add(rowEng);
        formWrapper.add(Box.createVerticalStrut(10)); // 행 사이 간격
        formWrapper.add(rowKor);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(rowEx);

        // 중앙에 배치 (이제 화면 가로를 꽉 채움)
        panel.add(formWrapper, BorderLayout.CENTER);

        // ----- 버튼 영역 (아래쪽, 가운데 정렬) -----
        JButton btnAdd = new JButton("추가");
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnPanel.add(btnAdd);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // 1) 영단어 입력 후 엔터 치면 뜻 입력칸으로 이동
        tfEng.addActionListener(e -> tfKor.requestFocusInWindow());

        // 2) 뜻 입력 후 엔터 치면 예문 칸으로 이동
        tfKor.addActionListener(e -> tfEx.requestFocusInWindow());

        // 3) 예문 칸에서 엔터 치면 "추가" 버튼 누른 것과 동일하게 실행
        tfEx.addActionListener(e -> btnAdd.doClick());


        // ----- 버튼 동작 -----
        btnAdd.addActionListener(e -> {
            String eng = tfEng.getText().trim();
            String korLine = tfKor.getText().trim();
            String ex = tfEx.getText().trim();

            // VocabManager의 추가 메서드 호출
            String msg = manager.addVocabCore(eng, korLine, ex);

            // 메시지 다이얼로그로 보여주기
            JOptionPane.showMessageDialog(
                    VocabFrame.this,
                    msg,
                    "단어 추가",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // 성공적으로 추가됐다면 입력창 비우고 테이블 새로고침
            if (msg.contains("추가")) {  // "새로 추가", "새 뜻 추가" 둘 다 포함
                tfEng.setText("");
                tfKor.setText("");
                tfEx.setText("");

                refreshTable();
            }
        });

        return panel;
    }

    //테이블만 만들어두고,(준비 상태) 단어관리 패널에서 붙일거임
    private void initTable() {
        model = new DefaultTableModel(header, 0);
        table = new JTable(model);
    }

    // manager.voc --> 테이블 model 에 반영 (새로 뒤덮음)
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- 1. 검색/불러오기 영역 (맨 위) ----
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

        JTextArea taExample = new JTextArea(5, 30);
        taExample.setLineWrap(true);
        taExample.setWrapStyleWord(true);
        JScrollPane exScroll = new JScrollPane(taExample);

        infoPanel.add(lblCurrentEng, BorderLayout.NORTH);

        // 왼쪽: 뜻 / 오른쪽: 예문 (제목 라벨까지 세트로)
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel leftPane = new JPanel(new BorderLayout(5, 5));
        leftPane.add(new JLabel("현재 뜻 목록"), BorderLayout.NORTH);
        leftPane.add(korScroll, BorderLayout.CENTER);

        JPanel rightPane = new JPanel(new BorderLayout(5, 5));
        rightPane.add(new JLabel("예문"), BorderLayout.NORTH);
        rightPane.add(exScroll, BorderLayout.CENTER);

        listsPanel.add(leftPane);
        listsPanel.add(rightPane);

        infoPanel.add(listsPanel, BorderLayout.CENTER);

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

            // 항상 먼저 입력창들 비우기
            tfSearchEng.setText("");
            tfNewEng.setText("");
            tfAddKor.setText("");

            if (eng.isEmpty()) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "영단어를 입력하세요."
                );
                // 현재 선택 상태도 초기화
                currentWord[0] = null;
                lblCurrentEng.setText("현재 영어: -");
                korListModel.clear();
                taExample.setText("");
                return;
            }

            Word w = manager.findExact(eng);
            if (w == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "해당 단어가 존재하지 않습니다."
                );
                // 단어 못 찾았을 때도 상태 초기화
                currentWord[0] = null;
                lblCurrentEng.setText("현재 영어: -");
                korListModel.clear();
                taExample.setText("");
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
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "먼저 단어를 불러와 주세요."
                );
                return;
            }
            String oldEng = currentWord[0].getEng();
            String newEng = tfNewEng.getText().trim();

            String msg = manager.renameEng(oldEng, newEng);
            JOptionPane.showMessageDialog(VocabFrame.this, msg);

            // 항상 입력창 비우기
            tfNewEng.setText("");

            // 성공 시 화면 갱신
            if (msg.startsWith("영어 단어가")) {
                lblCurrentEng.setText("현재 영어: " + currentWord[0].getEng());
                refreshTable();
            }
        });

        // ---- 5. 뜻 추가 버튼 ----
        btnAddKor.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "먼저 단어를 불러와 주세요."
                );
                return;
            }
            String eng = currentWord[0].getEng();
            String korLine = tfAddKor.getText().trim();

            String msg = manager.addKorMeanings(eng, korLine);
            JOptionPane.showMessageDialog(VocabFrame.this, msg);

            // 항상 입력창 비우기
            tfAddKor.setText("");

            // 목록 갱신
            korListModel.clear();
            for (String k : currentWord[0].getKors()) {
                korListModel.addElement(k);
            }
            refreshTable();
        });

        // ---- 6. 선택 뜻 삭제 버튼 ----
        btnDelKor.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "먼저 단어를 불러와 주세요."
                );
                return;
            }
            int sel = korList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "삭제할 뜻을 선택해주세요."
                );
                return;
            }
            String eng = currentWord[0].getEng();
            String msg = manager.removeKorMeaning(eng, sel + 1);
            JOptionPane.showMessageDialog(VocabFrame.this, msg);

            korListModel.clear();
            for (String k : currentWord[0].getKors()) {
                korListModel.addElement(k);
            }
            refreshTable();
        });

        // ---- 7. 예문 저장 버튼 ----
        btnSaveEx.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "먼저 단어를 불러와 주세요."
                );
                return;
            }
            String eng = currentWord[0].getEng();
            String newEx = taExample.getText();
            String msg = manager.updateExample(eng, newEx);
            JOptionPane.showMessageDialog(VocabFrame.this, msg);
        });

        return panel;
    }


    // 단어 삭제하는 패널
    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- 1. 상단: 삭제할 단어 입력 + 불러오기 ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfEng = new JTextField(15);
        JButton btnLoad = new JButton("불러오기");

        top.add(new JLabel("삭제할 영단어:"));
        top.add(tfEng);
        top.add(btnLoad);

        panel.add(top, BorderLayout.NORTH);

        // ---- 2. 가운데: 단어 정보 (요약 하나만) ----
        JTextArea taInfo = new JTextArea(7, 30);
        taInfo.setEditable(false);
        taInfo.setLineWrap(true);
        taInfo.setWrapStyleWord(true);

        JScrollPane infoScroll = new JScrollPane(taInfo);

        // ★ 즐겨찾기만 표시할 라벨
        JLabel lblBookmarkIcon = new JLabel("☆"); // 기본은 비활성
        lblBookmarkIcon.setFont(lblBookmarkIcon.getFont().deriveFont(24f)); // 조금 크게

        JPanel infoTop = new JPanel(new BorderLayout());
        infoTop.add(new JLabel("삭제 대상 단어 정보"), BorderLayout.WEST);
        infoTop.add(lblBookmarkIcon, BorderLayout.EAST);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        infoPanel.add(infoTop, BorderLayout.NORTH);
        infoPanel.add(infoScroll, BorderLayout.CENTER);

        panel.add(infoPanel, BorderLayout.CENTER);

        // ---- 3. 하단: 삭제 버튼 ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("삭제");
        bottom.add(btnDelete);
        panel.add(bottom, BorderLayout.SOUTH);

        // 현재 선택된 단어
        final Word[] currentWord = new Word[1];

        // ---- 작은 헬퍼: 화면 초기화 ----
        Runnable clearInfo = () -> {
            currentWord[0] = null;
            taInfo.setText("");
            lblBookmarkIcon.setText("☆");   // 즐겨찾기 초기화
        };

        // ---- 4. 불러오기 버튼 동작 ----
        btnLoad.addActionListener(e -> {
            String eng = tfEng.getText().trim();

            // 검색창은 항상 비우기
            tfEng.setText("");

            if (eng.isEmpty()) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "영단어를 입력하세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE
                );
                clearInfo.run();
                return;
            }

            Word w = manager.findExact(eng);
            if (w == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "해당 단어가 존재하지 않습니다.",
                        "단어 없음",
                        JOptionPane.INFORMATION_MESSAGE
                );
                clearInfo.run();
                return;
            }

            currentWord[0] = w;

            String kors = String.join("/ ", w.getKors());
            if (kors.isEmpty()) kors = "-";

            String ex = manager.getExampleMap().get(w.getEng());
            if (ex == null || ex.trim().isEmpty()) {
                ex = "(등록된 예문 없음)";
            }

            int wrong = w.getWrong_number();
            boolean bookmarked = w.isBookMark();
            lblBookmarkIcon.setText(bookmarked ? "⭐" : "☆");  // 즐겨찾기만 딱 보이게

            // 여기 하나에 모든 정보 정리해서 보여줌
            taInfo.setText(
                    "📌\n" +
                            "영어: " + w.getEng() + "\n" +
                            "뜻: " + kors + "\n" +
                            "예문: " + ex + "\n" +
                            "틀린 횟수: " + wrong + "\n"
            );
        });

        // ---- 5. 삭제 버튼 동작 ----
        btnDelete.addActionListener(e -> {
            if (currentWord[0] == null) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "먼저 삭제할 단어를 불러와 주세요.",
                        "알림",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            String eng = currentWord[0].getEng();

            int result = JOptionPane.showConfirmDialog(
                    VocabFrame.this,
                    "'" + eng + "' 단어를 정말 삭제하시겠습니까?\n" +
                            "이 작업은 되돌릴 수 없습니다.",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                String msg = manager.deleteWord(eng); // 공통 삭제 로직
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        msg,
                        "삭제 결과",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearInfo.run();
                refreshTable();
            }
        });

        return panel;
    }

    // 검색 패널
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- 1. 상단: 방향 + 검색창 + 정렬 ----
        JPanel top = new JPanel(new BorderLayout());

        // 1-1) 왼쪽: 방향 + 검색창 + 버튼
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JRadioButton rbEngToKor = new JRadioButton("영 → 한", true);
        JRadioButton rbKorToEng = new JRadioButton("한 → 영");
        ButtonGroup dirGroup = new ButtonGroup();
        dirGroup.add(rbEngToKor);
        dirGroup.add(rbKorToEng);

        JTextField tfQuery = new JTextField(20);
        JButton btnSearch = new JButton("검색");

        left.add(rbEngToKor);
        left.add(rbKorToEng);
        left.add(new JLabel("검색어:"));
        left.add(tfQuery);
        left.add(btnSearch);

        // 1-2) 오른쪽: 정렬 라디오
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JRadioButton rbAsc = new JRadioButton("Asc", true);
        JRadioButton rbDesc = new JRadioButton("Desc");
        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(rbAsc);
        sortGroup.add(rbDesc);
        right.add(new JLabel("정렬:"));
        right.add(rbAsc);
        right.add(rbDesc);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        // ---- 2. 가운데: 검색 결과 테이블 ----
        DefaultTableModel searchModel = new DefaultTableModel(header, 0); // header = {"영단어","뜻"}
        JTable searchTable = new JTable(searchModel);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);

        // 최근 검색 결과를 기억해두고 정렬만 바꿔서 다시 뿌릴 때 사용
        final List<Word>[] lastResult = new List[]{new ArrayList<>()};

        // ---- 3. 하단: 결과 개수 표시 ----
        JLabel lblStatus = new JLabel("검색 결과: 0개");
        panel.add(lblStatus, BorderLayout.SOUTH);

        // ---- 헬퍼: lastResult를 기준으로 정렬 + 테이블 갱신 ----
        Runnable refreshTableWithSort = () -> {
            searchModel.setRowCount(0);
            if (lastResult[0] == null || lastResult[0].isEmpty()) return;

            boolean asc = rbAsc.isSelected();

           List<Word> sorted = new ArrayList<>(lastResult[0]);
            sorted.sort((w1, w2) -> {
                int cmp = w1.getEng().compareToIgnoreCase(w2.getEng());
                return asc ? cmp : -cmp;
            });

            for (Word w : sorted) {
                String eng = w.getEng();
                String kor = String.join("/ ", w.getKors());
                searchModel.addRow(new Object[]{eng, kor});
            }
        };

        // ---- 실제 검색하는 헬퍼 ----
        Runnable doSearch = () -> {
            String q = tfQuery.getText().trim();
            if (q.isEmpty()) {
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "검색어를 입력하세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE
                );
                tfQuery.setText("");  //입력 칸 비우기
                return;
            }

           List<Word> result;
            if (rbEngToKor.isSelected()) {
                result = manager.findEngSubString(q);
            } else {
                result = manager.findKorSubString(q);
            }

            lastResult[0] = result;

            if (result.isEmpty()) {
                searchModel.setRowCount(0);
                lblStatus.setText("검색 결과: 0개");
                JOptionPane.showMessageDialog(
                        VocabFrame.this,
                        "검색 결과가 없습니다.",
                        "검색",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                lblStatus.setText("검색 결과: " + result.size() + "개");
                refreshTableWithSort.run();
            }

            tfQuery.setText("");
        };

        // ---- 이벤트 연결 ----
        // 버튼 클릭
        btnSearch.addActionListener(e -> doSearch.run());
        // 엔터 치면 검색
        tfQuery.addActionListener(e -> doSearch.run());
        // 정렬 라디오 바꾸면 현재 결과만 재정렬
        rbAsc.addActionListener(e -> refreshTableWithSort.run());
        rbDesc.addActionListener(e -> refreshTableWithSort.run());

        return panel;
    }



    //************* 유용한 기능 패널 ****************
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

        // 하단 버튼 영역 (추가 & 해제)
        JPanel btnPanel = new JPanel(); // 버튼 2개를 담을 패널
        JButton btnAdd = new JButton("단어 직접 추가");
        JButton btnDelete = new JButton("선택한 단어 해제");

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // 1) [추가] 버튼 동작
        btnAdd.addActionListener(e -> {
            // 입력 팝업 띄우기
            String input = JOptionPane.showInputDialog(panel, "즐겨찾기에 추가할 영단어를 입력하세요:");

            // 취소했거나 빈칸이면 종료
            if (input == null || input.trim().isEmpty()) return;

            String target = input.trim();
            boolean found = false;

            // 단어장에서 찾아서 즐겨찾기 설정
            for (Word w : manager.getVoc()) {
                if (w.getEng().equals(target)) {
                    if (w.isBookMark()) {
                        JOptionPane.showMessageDialog(panel, "이미 즐겨찾기에 등록된 단어입니다.");
                        return;
                    }
                    w.setBookMark(true); //즐겨찾기 true로 변경
                    found = true;
                    break;
                }
            }

            if (found) {
                refreshBookmarkTable(); // 테이블 새로고침
                JOptionPane.showMessageDialog(panel, "'" + target + "' 단어가 즐겨찾기에 추가되었습니다!");
            } else {
                JOptionPane.showMessageDialog(panel, "단어장에 존재하지 않는 단어입니다.\n먼저 '단어 관리'에서 단어를 추가해주세요.");
            }
        });

        // 2) [해제] 버튼 동작
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "해제할 단어를 표에서 선택해주세요.");
                return;
            }
            String eng = (String) bookmarkModel.getValueAt(row, 0);

            for (Word w : manager.getVoc()) {
                if (w.getEng().equals(eng)) {
                    w.setBookMark(false); // 즐겨찾기 false로 해제
                    break;
                }
            }
            refreshBookmarkTable();
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

    //************* 시작 화면 *********
    // 시작 화면 패널
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 상단 타이틀
        JLabel title = new JLabel("단어장 프로그램", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 32f));

        JLabel subtitle = new JLabel("위의 메뉴 또는 아래 버튼을 눌러 시작하세요.",
                SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(16f));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        // 가운데 큰 버튼들
        JButton btnGoWord = new JButton("단어 관리 시작하기");
        JButton btnGoQuiz = new JButton("퀴즈 풀기");
        JButton btnGoUtil = new JButton("유용한 기능");

        btnGoWord.setFont(btnGoWord.getFont().deriveFont(18f));
        btnGoQuiz.setFont(btnGoQuiz.getFont().deriveFont(18f));
        btnGoUtil.setFont(btnGoUtil.getFont().deriveFont(18f));

        // 누르면 해당 카드로 전환
        btnGoWord.addActionListener(e -> mainCard.show(mainCardPanel, "WORD"));
        btnGoQuiz.addActionListener(e -> mainCard.show(mainCardPanel, "QUIZ"));
        btnGoUtil.addActionListener(e -> mainCard.show(mainCardPanel, "UTIL"));

        JPanel centerButtons = new JPanel(new GridLayout(3, 1, 15, 15));
        centerButtons.add(btnGoWord);
        centerButtons.add(btnGoQuiz);
        centerButtons.add(btnGoUtil);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(centerButtons, BorderLayout.CENTER);

        return panel;
    }


}



