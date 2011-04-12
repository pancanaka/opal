package fr.xlim.ssd.opal.gui.view.components.tab;

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
 * @author Thibault
 *
 * the DeletePanel class serves to instantiate the deletion tab
 */
public class DeletePanel extends JPanel{

    public String title = "Delete";
    public static JTextField AID;
    public JButton Delete ;
    /**
     * DeletePanel constructor
     */
    public DeletePanel()
    {
        setLayout(new BorderLayout());
        JPanel jplPanel = new JPanel();
        add(jplPanel,BorderLayout.WEST);
        jplPanel.setLayout(new FlowLayout());

        JLabel deletelab = new JLabel("Object AID");
        jplPanel.add(deletelab);

        AID = new JTextField(40);
        jplPanel.add(AID);
      //  AID.addActionListener(new DeleteObject());

        JPanel boutton_panel = new JPanel();
        boutton_panel.setLayout(new FlowLayout());
        add(boutton_panel,BorderLayout.SOUTH);

        JButton Delete = new JButton("Delete");
        boutton_panel.add(Delete);
        Delete.addActionListener(new DeleteObject());



    }
    public static String gettext(){
        return AID.getText();
    }
}