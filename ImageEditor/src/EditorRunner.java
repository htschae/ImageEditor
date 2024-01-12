import javax.swing.JFrame;

public class EditorRunner {

  public static void main(String[] args) {
    JFrame f = new JFrame("Image Editor"); 
    EditorPanel p = new EditorPanel();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.add(p);
    f.pack();
    f.setVisible(true);
    p.setFocusable(true);
    p.requestFocusInWindow();
  }
}