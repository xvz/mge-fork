package viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class ProgressWindow extends JFrame{
	// Variables declaration - do not modify
	private JLabel avisynthText;
	private JButton closeButton;
	private JLabel createJobsText;
	private JLabel editJobXMLText;
	private JLabel hydrateText;
	private JPanel innerPanel;
	private JLabel outputJobXMLText;
	// End of variables declaration

	public ProgressWindow(){
		init();
	}

	private void init(){
		setTitle("Status Window");
		
		innerPanel = new JPanel();
		hydrateText = new JLabel();
		avisynthText = new JLabel();
		createJobsText = new JLabel();
		outputJobXMLText = new JLabel();
		editJobXMLText = new JLabel();
		closeButton = new JButton();
		
		closeButton.addActionListener(new CloseListener(this));

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		innerPanel.setBorder(BorderFactory.createEtchedBorder());

		hydrateText.setForeground(new java.awt.Color(153, 153, 153));
		hydrateText.setText("Hydrating Media Files");

		avisynthText.setForeground(new java.awt.Color(153, 153, 153));
		avisynthText.setText("Generate AviSynth Scripts");

		createJobsText.setForeground(new java.awt.Color(153, 153, 153));
		createJobsText.setText("Create MeGUI Jobs");

		outputJobXMLText.setForeground(new java.awt.Color(153, 153, 153));
		outputJobXMLText.setText("Output Job XML Files");

		editJobXMLText.setForeground(new java.awt.Color(153, 153, 153));
		editJobXMLText.setText("Edit Job XML File");

		GroupLayout innerPanelLayout = new GroupLayout(innerPanel);
		innerPanel.setLayout(innerPanelLayout);
		innerPanelLayout.setHorizontalGroup(
				innerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(innerPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(innerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(editJobXMLText)
								.addComponent(outputJobXMLText)
								.addComponent(createJobsText)
								.addComponent(avisynthText)
								.addComponent(hydrateText))
								.addContainerGap(187, Short.MAX_VALUE))
		);
		innerPanelLayout.setVerticalGroup(
				innerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(innerPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(hydrateText)
						.addGap(18, 18, 18)
						.addComponent(avisynthText)
						.addGap(18, 18, 18)
						.addComponent(createJobsText)
						.addGap(18, 18, 18)
						.addComponent(outputJobXMLText)
						.addGap(18, 18, 18)
						.addComponent(editJobXMLText)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		closeButton.setText("Close");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(innerPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(closeButton, GroupLayout.Alignment.TRAILING))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(innerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(closeButton)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}

	public void setHydrateMediaFilesStep(){
		hydrateText.setText("Hydrating Media Files...Complete");
		hydrateText.setForeground(new java.awt.Color(0, 0, 0));
	}

	public void setGenerateAviSynthScriptsStep(){
		avisynthText.setText("Generate AviSynth Scripts...Complete");
		avisynthText.setForeground(new java.awt.Color(0, 0, 0));
	}

	public void setCreateMeGUIJobsStep(){
		createJobsText.setText("Create MeGUI Jobs...Complete");
		createJobsText.setForeground(new java.awt.Color(0, 0, 0));
	}

	public void setOutputJobXMLFilesStep(){
		outputJobXMLText.setText("Ouput Job XML Files...Complete");
		outputJobXMLText.setForeground(new java.awt.Color(0, 0, 0));
	}

	public void setEditJobXMLFileStep(){
		editJobXMLText.setText("Edit Job XML File...Complete");
		editJobXMLText.setForeground(new java.awt.Color(0, 0, 0));
	}

}

class CloseListener implements ActionListener{

	private ProgressWindow progressWindow;
	
	public CloseListener(ProgressWindow pw){
		this.progressWindow = pw;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		progressWindow.setVisible(false);
	}
	
}
