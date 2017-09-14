package chat_client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class ChatHistory {

    private static final String FILE_NAME = "C:\\chat\\history.txt";
    JTextArea textArea;

    public void createFrame() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                JFrame frame = new JFrame("History");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setOpaque(true);
                textArea = new JTextArea(30, 50);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                textArea.setFont(Font.getFont(Font.SANS_SERIF));
                JScrollPane scroller = new JScrollPane(textArea);
                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                DefaultCaret caret = (DefaultCaret) textArea.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                panel.add(scroller);
                frame.getContentPane().add(BorderLayout.CENTER, panel);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                frame.setResizable(false);

            }
        });
    }

    public ArrayList<String> readHistory() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        Scanner in = new Scanner(new File("history.txt"));
        while (in.hasNextLine())
            list.add(in.nextLine());
        return list;
    }

    public synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater((new Runnable() {
            @Override
            public void run() {
                textArea.append(msg + " \n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }));

    }
}


