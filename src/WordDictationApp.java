import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javazoom.jl.player.Player;

public class WordDictationApp extends JFrame {
    private DefaultListModel<String> wordListModel = new DefaultListModel<>();
    private JList<String> wordList = new JList<>(wordListModel);
    private JTextField inputField = new JTextField(15);
    private JComboBox<String> accentBox = new JComboBox<>(new String[]{"美音", "英音"});
    private JTextField intervalField = new JTextField("2000", 5); // 默认 2000ms
    private JButton playStopButton = new JButton("开始听写");  // 单按钮控制播放与停止
    private JCheckBox shuffleBox = new JCheckBox("随机播放");

    private volatile boolean isPlaying = false;  // 播放状态标志

    public WordDictationApp() {
        setTitle("英语单词听写程序");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 400);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("单词:"));
        topPanel.add(inputField);
        JButton addButton = new JButton("添加");
        JButton removeButton = new JButton("删除");
        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(new JLabel("发音:"));
        topPanel.add(accentBox);
        topPanel.add(new JLabel("间隔(ms):"));
        topPanel.add(intervalField);
        topPanel.add(shuffleBox);
        topPanel.add(playStopButton);

        JScrollPane scrollPane = new JScrollPane(wordList);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem loadItem = new JMenuItem("导入");
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 支持按下回车添加单词
        inputField.addActionListener(e -> addWord());

        // 添加按钮事件
        addButton.addActionListener(e -> addWord());

        // 删除按钮事件
        removeButton.addActionListener(e -> {
            List<String> selected = wordList.getSelectedValuesList();
            for (String word : selected) {
                wordListModel.removeElement(word);
            }
        });

        // 监听Delete键，选中单词时按Delete也能删除
        wordList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    List<String> selected = wordList.getSelectedValuesList();
                    for (String word : selected) {
                        wordListModel.removeElement(word);
                    }
                }
            }
        });

        // 保存单词列表到文件
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                    for (int i = 0; i < wordListModel.size(); i++) {
                        writer.println(wordListModel.get(i));
                    }
                    JOptionPane.showMessageDialog(this, "保存成功！");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 导入单词列表文件
        loadItem.addActionListener(e -> chooseFileAndLoad());

        // 支持拖放文件导入
        new DropTarget(wordList, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        if (file.getName().toLowerCase().endsWith(".txt")) {
                            loadFromFile(file);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 播放按钮监听，实现开始/停止切换
        playStopButton.addActionListener(e -> {
            if (!isPlaying) {
                // 开始播放线程
                new Thread(() -> {
                    int interval;
                    try {
                        interval = Integer.parseInt(intervalField.getText().trim());
                        if (interval < 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "请输入有效的正数间隔！");
                        return;
                    }

                    List<String> words = new ArrayList<>();
                    for (int i = 0; i < wordListModel.size(); i++) {
                        words.add(wordListModel.get(i));
                    }

                    if (words.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "请先添加一些单词！");
                        return;
                    }

                    if (shuffleBox.isSelected()) {
                        Collections.shuffle(words);
                    }

                    isPlaying = true;
                    SwingUtilities.invokeLater(() -> playStopButton.setText("停止播放"));

                    int type = accentBox.getSelectedIndex();

                    for (int i = 0; i < words.size() && isPlaying; i++) {
                        String word = words.get(i);
                        final int index = wordListModel.indexOf(word);
                        SwingUtilities.invokeLater(() -> wordList.setSelectedIndex(index));

                        playAudio(word, type);

                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    isPlaying = false;
                    SwingUtilities.invokeLater(() -> {
                        playStopButton.setText("开始听写");
                        wordList.clearSelection();
                    });
                }).start();
            } else {
                // 停止播放
                isPlaying = false;
            }
        });
    }

    private void addWord() {
        String word = inputField.getText().trim();
        if (!word.isEmpty()) {
            if (wordListModel.contains(word)) {
                JOptionPane.showMessageDialog(this, "单词已存在！");
            } else {
                wordListModel.addElement(word);
                inputField.setText("");
            }
        }
    }

    private void chooseFileAndLoad() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadFromFile(fileChooser.getSelectedFile());
        }
    }

    private void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty() && !wordListModel.contains(word)) {
                    wordListModel.addElement(word);
                }
            }
            JOptionPane.showMessageDialog(this, "导入成功！");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void playAudio(String word, int type) {
        String urlStr = "http://dict.youdao.com/dictvoice?type=" + type + "&audio=" + word;
        try (BufferedInputStream bis = new BufferedInputStream(new URL(urlStr).openStream())) {
            Player player = new Player(bis);
            player.play();
        } catch (Exception e) {
            System.err.println("无法播放：" + word);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordDictationApp().setVisible(true));
    }
}
