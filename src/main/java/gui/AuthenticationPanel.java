package gui;

import javax.swing.JPanel;
import java.awt.GridLayout; 
import javax.swing.JLabel;



/**
 *
 * @author razaina
 */
public class AuthenticationPanel extends JPanel{

    public String title = "Authentication";
    public AuthenticationPanel()
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel("Authentication view");
	jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);

        add(jplPanel);
    }
}
