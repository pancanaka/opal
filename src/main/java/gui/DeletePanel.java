package gui;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;



/**
 *
 * @author razaina
 */
public class DeletePanel extends JPanel{

    public String title = "Delete";
    public DeletePanel()
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel("Delete view");
	jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);
        add(jplPanel);
    }
}
