package at.u4a.geometric_algorithms.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.ImageIcon;

public class PrincipalGui {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PrincipalGui window = new PrincipalGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public PrincipalGui() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 512, 585);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.setIcon(new ImageIcon("icons/page_add.png"));
        mnFile.add(mntmNew);
        
        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.setIcon(new ImageIcon("icons/page_edit.png"));
        mnFile.add(mntmOpen);
        
        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.setIcon(new ImageIcon("icons/page_save.png"));
        mnFile.add(mntmSave);
        
        mnFile.addSeparator();
        
        JMenuItem mntmOther = new JMenuItem("Other");
        mnFile.add(mntmOther);
        
        mnFile.addSeparator();
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setIcon(new ImageIcon("icons/page_delete.png"));
        mnFile.add(mntmExit);
    }

}
