package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;



/**
 * 
 * @author chanaa
 * @author razaina
 *
 * the DeletePanel class serves to instantiate the deletion tab
 */
public class DeletePanel extends JPanel{

    public String title = "Delete";
    /**
     * DeletePanel constructor
     */
    public DeletePanel()
    {
        setLayout(new BorderLayout());
        JPanel jplPanel = new JPanel();
        add(jplPanel,BorderLayout.WEST);
        jplPanel.setLayout(new FlowLayout());

        JLabel deletelab = new JLabel("AID");
        jplPanel.add(deletelab);

        JTextField AID = new JTextField(40);
        jplPanel.add(AID);

        JPanel boutton_panel = new JPanel();
        boutton_panel.setLayout(new FlowLayout());
        add(boutton_panel,BorderLayout.SOUTH);

        JButton Delete = new JButton("Delete");
        boutton_panel.add(Delete);
        
        
       
    }
}
