package gui;

import javax.swing.JPanel;
import java.awt.GridLayout; 
import javax.swing.JLabel;



/**
 *
 * @author razaina
 */
public class AppletPanel extends JPanel{

    public String title = "Applet";
    public AppletPanel()
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel("Applet view");
	jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);

        add(jplPanel);
    }
}
