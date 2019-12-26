import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
class GUI extends JFrame{
    private JTextField blockWidth_tb, blockHeight_tb, codebookSize_tb;
    private JButton browse_btn, compress_btn, restore_btn;
    private String selected_file;
    GUI(){
        super("Vector quantization");
        this.getContentPane().setLayout(new GridLayout(5, 2));
        blockWidth_tb = new JTextField();
        blockHeight_tb = new JTextField();
        codebookSize_tb = new JTextField();
        browse_btn = new JButton("Browse");
        compress_btn = new JButton("Compress image");
        restore_btn = new JButton("Restore image");
        browse_btn.setActionCommand("browse");
        compress_btn.setActionCommand("compress");
        restore_btn.setActionCommand("restore");
        GUIActionListener al = new GUIActionListener();

        browse_btn.addActionListener(al);
        compress_btn.addActionListener(al);
        restore_btn.addActionListener(al);

        this.add(new JLabel("Block width: "));
        this.add(blockWidth_tb);
        this.add(new JLabel("Block Height: "));
        this.add(blockHeight_tb);
        this.add(new JLabel("Codebook size"));
        this.add(codebookSize_tb);
        this.add(new JLabel("Image file: "));
        this.add(browse_btn);
        this.add(restore_btn);
        this.add(compress_btn);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    class GUIActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch(e.getActionCommand()){
                case "browse":
                    JFileChooser fileChooser = new JFileChooser();
                    int status = fileChooser.showOpenDialog(GUI.this);
                    if(status == JFileChooser.APPROVE_OPTION){
                        selected_file = fileChooser.getSelectedFile().getAbsolutePath();
                    }
                    break;
                case "compress":
                    ImageVectorQuantizer.compressImage(selected_file, selected_file + ".dat", Integer.valueOf(blockWidth_tb.getText()), Integer.valueOf(blockHeight_tb.getText()), Integer.valueOf(codebookSize_tb.getText()));
                    JOptionPane.showMessageDialog(null, "Selected file was compressed");
                    break;
                case "restore":
                    try {
                        ImageVectorQuantizer.restoreImage(selected_file, selected_file + ".bmp");
                        JOptionPane.showMessageDialog(null, "Selected file was restored");
                    } catch (ClassNotFoundException | IOException e1) {
                        JOptionPane.showMessageDialog(null, "The selected file is not valid");
                    }
                    break;
            }
        }
    }
}
