package gui;

import javax.swing.JPanel;
import java.awt.GridLayout; 
import javax.swing.JLabel;



/**
 *
 * @author razaina
 */
public class SendAPDUPanel extends JPanel{

    public String title = "Send APDU";
    public SendAPDUPanel()
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel("Send APDU view");
	jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);

        add(jplPanel);
    }
}
