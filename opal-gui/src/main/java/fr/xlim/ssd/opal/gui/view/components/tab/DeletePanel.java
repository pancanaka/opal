/******************************************************************************
 *                             OPAL - GUI                                     *
 ******************************************************************************
 * Authors : Chanaa Anas <anas.chanaa@etu.unilim.fr>                          *
 *           Tiana Razafindralambo <aina.razafindralambo@etu.unilim.fr>       *
 *           Thibault Desmoulins <thibault.desmoulins@etu.unilim.fr>          *
 ******************************************************************************
 * This file is part of the OPAL project.                                     *
 ******************************************************************************
 * Copyright : University of Limoges (Unilim), 2011                           *
 ******************************************************************************/

package fr.xlim.ssd.opal.gui.view.components.tab;

import fr.xlim.ssd.opal.gui.controller.DeleteController;
import fr.xlim.ssd.opal.library.utilities.Conversion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;



/**
 *
 * @author Chanaa Anas
 * @author Tiana Razafindralambo
 * @author Thibault Desmoulins
 *
 * the DeletePanel class serves to instantiate the deletion tab
 */
public class DeletePanel extends JPanel implements ActionListener{

    public String title = "Delete";
    public static JTextField AID;
    private DeleteController controller;
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

        Delete = new JButton("Delete");
        boutton_panel.add(Delete);
        Delete.addActionListener((ActionListener) this); 
    }
    public void setController(DeleteController controller)
    {
        this.controller = controller;
    }
    public static String gettext(){
        return AID.getText();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource(); 

        if(o instanceof JButton) {
            JButton b = (JButton) o; 
            if(b.equals(Delete))
            {
                byte[] APPLET_ID = Conversion.hexToArray(AID.getText());
                controller.deleteApplet(APPLET_ID);
            }
        }
    }
}