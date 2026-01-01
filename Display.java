import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.Math;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe utilitaire pour gérer l'affichage graphique du jeu Splendor.
 * 
 * Cette classe crée une interface graphique Swing composée de deux zones principales :
 * une zone supérieure pour afficher l'état du plateau de jeu, et une zone inférieure
 * pour la console d'interaction avec le joueur. Elle fournit des flux d'entrée/sortie
 * personnalisés permettant d'utiliser les méthodes print/println comme avec System.out,
 * et de lire les entrées utilisateur comme avec Scanner.
 * 
 * Cette classe est fournie par l'enseignant et n'a pas besoin d'être modifiée ni comprise en profondeur.
 * Elle gère automatiquement l'affichage des caractères Unicode nécessaires pour
 * le rendu visuel du jeu (symboles de ressources, bordures ASCII, etc.).
 * 
 * @author Fourni par l'enseignant
 * @version 01/01/2026
 */
class Display {
    /*
     * Voir l'utilisation de la classe au début de la classe Game.
     */
    private JFrame frame;
    private JTextArea boardTextAreaBoard, consoleTextArea;
    private JTextField textField;

    public Readable in;
    public JTextAreaPrintStream out;
    public JTextAreaPrintStream outBoard;
    
    private static int displayedLength(String str){
        int length = 0;
        for(int i=0;i<str.length();i++){
            int cp = str.codePointAt(i);
            if(cp==9450 || (cp>=9312 && cp<=9331) || cp==9450){
                length+=2;
            }else{
                length+=1;
            }
        }
        return length;
    }
    
    private static int width(String[] strarr){
        int maxLength = 0;
        for(String line : strarr){
            if(displayedLength(line) > maxLength){
                maxLength = displayedLength(line);
            }
        }
        return maxLength;
    }
    
    private static int height(String[] strarr){
        return strarr.length;
    }
    
    /**
     * Complete every line of the string array with space to have the same length everywhere
     */
    private static String[] pad(String[] strarr){
        return pad(strarr, " ");
    }
    
    /**
     * Complete every line of the string array to have the same length everywhere
     */
    private static String[] pad(String[] strarr, String c){
        return pad(strarr, c, height(strarr), width(strarr));
    }
    
    /**
     * Complete the string array to have the given shape
     */
    private static String[] pad(String[] strarr, String c, int height, int width){
        String[] paddedStrarr = new String[height];        
        for(int i=0;i<height;i++){
            if(i<strarr.length){
                paddedStrarr[i] = strarr[i] + String.join("", Collections.nCopies(width-displayedLength(strarr[i]), c));
            }else{
                paddedStrarr[i] = String.join("", Collections.nCopies(width, c));
            }
        }
        return paddedStrarr;
    }

    public static String[] concatStringArray(String[] strarr1, String[] strarr2, boolean vStacked){
        String[] arr;
        if(vStacked){
            int newWidth = Math.max(width(strarr1), width(strarr2));
            strarr1 = pad(strarr1, " ", height(strarr1), newWidth);
            strarr2 = pad(strarr2, " ", height(strarr2), newWidth);
            
            arr = new String[strarr1.length+strarr2.length];
            for(int i=0;i<strarr1.length;i++){
                arr[i] = strarr1[i];
            }
            for(int i=0;i<strarr2.length;i++){
                arr[strarr1.length+i] = strarr2[i];
            }
        }else{
            int newHeight = Math.max(height(strarr1), height(strarr2));
            strarr1 = pad(strarr1, " ", newHeight, width(strarr1));
            strarr2 = pad(strarr2, " ", newHeight, width(strarr2));
            
            arr = strarr1.clone();
            for(int i=0;i<strarr2.length;i++){
                arr[i] += strarr2[i];
            }
        }
        
        return arr;
    }

    public static String[] emptyStringArray(int rows, int cols, String str){
        String[] arr = new String[rows];
        Arrays.fill(arr, String.join("", Collections.nCopies(cols, str)));
        return arr;
    }

    public static String[] emptyStringArray(int rows, int cols){
        return emptyStringArray(rows, cols, " ");
    }

    public Display(int rowsBoard, int rowsConsole, int cols){
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("unifont.otf"));
            font  = font.deriveFont(Font.PLAIN, 14);
        } catch (FontFormatException | IOException e) {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
            e.printStackTrace();
        }

        boardTextAreaBoard = new JTextArea(rowsBoard, cols);
        boardTextAreaBoard.setEditable(false);
        boardTextAreaBoard.setLineWrap(true);
        boardTextAreaBoard.setFont(font);
        outBoard = new JTextAreaPrintStream(boardTextAreaBoard);

        consoleTextArea = new JTextArea(rowsConsole, cols);
        consoleTextArea.setBackground(new Color(240,240,240));
        consoleTextArea.setEditable(false);
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setFont(font);
        consoleTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        DefaultCaret consoleCaret = (DefaultCaret) consoleTextArea.getCaret();
        consoleCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        out = new JTextAreaPrintStream(consoleTextArea);

        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
                
        textField = new ReadableJTextField(cols);
        textField.setBackground(new Color(240,240,240));
        textField.setFont(font);
        NavigationFilter cursorPositionFilter = new NavigationFilter() {
            private int START_POSITION = 2;
            
            @Override
            public int getNextVisualPositionFrom(JTextComponent component, int position, Position.Bias bias, int direction, Position.Bias[] newBias) 
                    throws BadLocationException {
                if (position > START_POSITION || direction != SwingConstants.WEST) {
                    return super.getNextVisualPositionFrom(component, position, bias, direction, newBias);
                } else {
                    return position;
                }
            }
        };
        textField.setNavigationFilter(cursorPositionFilter);
        DocumentFilter cursorActionFilter = new DocumentFilter() {
            private int START_POSITION = 2;
            
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (offset >= START_POSITION) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                if (offset >= START_POSITION) {
                    super.remove(fb, offset, length);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (offset >= offset) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(cursorActionFilter);
        in = (Readable) textField;

        frame = new JFrame("Splendor Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(boardTextAreaBoard, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.pack();
        textField.requestFocusInWindow();
        textField.setCaretPosition(2);
        frame.setVisible(true);
        frame.setMinimumSize(frame.getSize());
    }

    public void close(){
        frame.dispose();
    }

    public int getBoardColumns(){
        return boardTextAreaBoard.getColumns();
    }
    
    public int getBoardRows(){
        return boardTextAreaBoard.getRows();
    }

    public class JTextAreaPrintStream {
        private JTextArea textArea;
    
        public JTextAreaPrintStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        public void clean(){
            textArea.setText("");
        }

        private void newLine() {
            print("\n");
        }

        public void print(boolean b) {
            print(b ? "true" : "false");
        }

        public void print(char c) {
            print(String.valueOf(c));
        }

        public void print(int i) {
            print(String.valueOf(i));
        }

        public void print(long l) {
            print(String.valueOf(l));
        }

        public void print(float f) {
            print(String.valueOf(f));
        }

        public void print(double d) {
            print(String.valueOf(d));
        }

        public void print(char s[]) {
            print(String.valueOf(s));
        }

        public void print(String s) {
            if (s == null) {
                s = "null";
            }
            textArea.append(s);
        }

        public void print(Object obj) {
            print(String.valueOf(obj));
        }


        public void println() {
            newLine();
        }

        public void println(boolean x) {
            print(x);
            newLine();
        }

        public void println(char x) {
            print(x);
            newLine();
        }

        public void println(int x) {
            print(x);
            newLine();
        }

        public void println(long x) {
            print(x);
            newLine();
        }

        public void println(float x) {
            print(x);
            newLine();
        }

        public void println(double x) {
            print(x);
            newLine();
        }

        public void println(char x[]) {
            print(x);
            newLine();
        }

        public void println(String x) {
            print(x);
            newLine();
        }

        public void println(Object x) {
            print(x);
            newLine();
        }

    }
    
    private class ReadableJTextField extends JTextField implements Readable {
        private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    
        public ReadableJTextField(int columns) {
            super("> ", columns);
    
            this.addActionListener(e -> {
                synchronized (queue) {
                    String text = this.getText().substring(2) + "\n";
                    this.setText("> ");
                    for (int i=0 ; i<text.length() ; i++ ) {
                        queue.offer(text.codePointAt(i));
                    }
                    queue.notify();
                }
            });
        }
    
        @Override
        public int read(CharBuffer cb) {
            synchronized (queue) {
                int n;
                for(n=0;!queue.isEmpty();n++){
                    cb.put((char)queue.poll().intValue());
                }
                return n;
            }
        }
       
    }
}
