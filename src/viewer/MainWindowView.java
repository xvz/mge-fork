package viewer;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;

import objects.ActionType;
import objects.MainControllerListener;
import objects.MediaFile;
import objects.MediaFileTableModel;
import objects.MediaFileTransferHandler;
import controller.MainController;
/**
 * This is the main window that the user sees
 * @author ajohnson
 *
 */
@SuppressWarnings("serial")
public class MainWindowView extends JFrame {
	//Elements of window
	private JMenuItem aboutMenuItem;
	private JComboBox audioProfileComboBox;
	private JPanel avisynthPanel;
	private JTextArea avisynthTextArea;
	private JButton clearButton;
	private JLabel delayLabel;
	private JSpinner delaySpinner;
	private JButton deleteButton;
	private JCheckBox directAudioJobsToAviSynthScript;
	private JLabel encodingProfilesLabel;
	private JMenuItem exitMenuItem;
	private JMenu fileMenu;
	private JTable fileTable;
	private JPanel fileTablePanel;
	private JButton goButton;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JMenuItem logMenuItem;
	private JSeparator mainDivider;
	private JMenuBar mainMenuBar;
	private JTabbedPane mainTabPane;
	private JComboBox mediaContainerComboBox;
	private JPanel otherOptionsPanel;
	private JComboBox videoProfileComboBox;

	//Private Data
	private TransferHandler transfer;
	private static final String TITLE = "MeGUI Expansion " + MainController.VERSION;
	private ArrayList<MediaFile> mediaFiles = new ArrayList<MediaFile>();
	private MediaFileTableModel model;
	private String[] listOfVideoProfiles;
	private String[] listOfAudioProfiles;
	private String[] listOfMediaContainers;

	public MainWindowView(String[] videoProfiles, String[] audioProfiles, String[] mediaContainers){
		this.listOfVideoProfiles = videoProfiles;
		this.listOfAudioProfiles = audioProfiles;
		this.listOfMediaContainers = mediaContainers;

		model = new MediaFileTableModel(mediaFiles);
		initializeWindow();
		attachListeners();
	}

	private void initializeWindow(){
		//Title of Window
		this.setTitle(TITLE);
		
		//Set Image
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("pictures" + File.separator + "Icon.png"));

		transfer = new MediaFileTransferHandler();
		mainDivider = new JSeparator();
		videoProfileComboBox = new JComboBox(listOfVideoProfiles);
		audioProfileComboBox = new JComboBox(listOfAudioProfiles);
		mediaContainerComboBox = new JComboBox(listOfMediaContainers);
		encodingProfilesLabel = new JLabel();
		mainTabPane = new JTabbedPane();
		fileTablePanel = new JPanel();
		clearButton = new JButton();
		deleteButton = new JButton();
		jScrollPane1 = new JScrollPane();
		fileTable = new JTable();
		avisynthPanel = new JPanel();
		jScrollPane2 = new JScrollPane();
		avisynthTextArea = new JTextArea();
		goButton = new JButton();
		mainMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		aboutMenuItem = new JMenuItem();
		logMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		delaySpinner = new JSpinner();
		delayLabel = new JLabel();
        otherOptionsPanel = new JPanel();
        directAudioJobsToAviSynthScript = new JCheckBox();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		encodingProfilesLabel.setText("Encoding Profiles");

		clearButton.setText("Clear");

		deleteButton.setText("Delete");

		fileTable.setModel(model);

		//Set Drag and Drop
		this.setTransferHandler(transfer);
		//End set drag and drop


		jScrollPane1.setViewportView(fileTable);
		fileTable.getColumnModel().getColumn(0).setResizable(false);

		javax.swing.GroupLayout fileTablePanelLayout = new javax.swing.GroupLayout(fileTablePanel);
		fileTablePanel.setLayout(fileTablePanelLayout);
		fileTablePanelLayout.setHorizontalGroup(
				fileTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileTablePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(fileTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
								.addGroup(fileTablePanelLayout.createSequentialGroup()
										.addComponent(deleteButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(clearButton)))
										.addContainerGap())
		);
		fileTablePanelLayout.setVerticalGroup(
				fileTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileTablePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(fileTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(clearButton)
								.addComponent(deleteButton))
								.addContainerGap())
		);

		mainTabPane.addTab("Selected Files", fileTablePanel);

		avisynthTextArea.setColumns(20);
		avisynthTextArea.setLineWrap(true);
		avisynthTextArea.setRows(5);
	
		avisynthTextArea.setWrapStyleWord(true);
		jScrollPane2.setViewportView(avisynthTextArea);

		javax.swing.GroupLayout avisynthPanelLayout = new javax.swing.GroupLayout(avisynthPanel);
		avisynthPanel.setLayout(avisynthPanelLayout);
		avisynthPanelLayout.setHorizontalGroup(
				avisynthPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(avisynthPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
						.addContainerGap())
		);
		avisynthPanelLayout.setVerticalGroup(
				avisynthPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(avisynthPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
						.addContainerGap())
		);

		mainTabPane.addTab("Avisynth", avisynthPanel);

		directAudioJobsToAviSynthScript.setText("Direct Audio Jobs to AviSynth Script");

		javax.swing.GroupLayout otherOptionsPanelLayout = new javax.swing.GroupLayout(otherOptionsPanel);
		otherOptionsPanel.setLayout(otherOptionsPanelLayout);
		otherOptionsPanelLayout.setHorizontalGroup(
				otherOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(otherOptionsPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(directAudioJobsToAviSynthScript)
						.addContainerGap(649, Short.MAX_VALUE))
		);
		otherOptionsPanelLayout.setVerticalGroup(
				otherOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(otherOptionsPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(directAudioJobsToAviSynthScript)
						.addContainerGap(303, Short.MAX_VALUE))
		);

		mainTabPane.addTab("Other Options", otherOptionsPanel);

		goButton.setText("Go");

		delayLabel.setText("Delay (ms)");

		fileMenu.setText("File");

		aboutMenuItem.setText("About");
		fileMenu.add(aboutMenuItem);

		logMenuItem.setText("View Log");
		fileMenu.add(logMenuItem);

		exitMenuItem.setText("Exit");
		fileMenu.add(exitMenuItem);

		mainMenuBar.add(fileMenu);

		setJMenuBar(mainMenuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(encodingProfilesLabel)
								.addComponent(mainTabPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(audioProfileComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(videoProfileComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 479, Short.MAX_VALUE)
												.addComponent(mediaContainerComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
																.addComponent(goButton))
																.addGroup(layout.createSequentialGroup()
																		.addGap(18, 18, 18)
																		.addComponent(delaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18, 18, 18)
																		.addComponent(delayLabel))))
																		.addComponent(mainDivider, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE))
																		.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
						.addGap(16, 16, 16)
						.addComponent(mainDivider, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(encodingProfilesLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(videoProfileComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(audioProfileComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(delaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(delayLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(mediaContainerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(goButton))
										.addContainerGap())
		);

		pack();
	}

	private void attachListeners(){
		aboutMenuItem.addActionListener(new MainControllerListener(ActionType.ABOUT));
		clearButton.addActionListener(new MainControllerListener(ActionType.CLEAR));
		deleteButton.addActionListener(new MainControllerListener(ActionType.DELETE));
		exitMenuItem.addActionListener(new MainControllerListener(ActionType.EXIT));
		goButton.addActionListener(new MainControllerListener(ActionType.GO));
		logMenuItem.addActionListener(new MainControllerListener(ActionType.LOG));

	}

	public String getAvisynthText(){
		return avisynthTextArea.getText().trim();
	}

	public void updateFileList(ArrayList<MediaFile> fileList){
		this.mediaFiles = fileList;
		model.setMediaFileList(mediaFiles);
		fileTable.tableChanged(new TableModelEvent(model));
	}

	public void clearFileList(){
		mediaFiles = new ArrayList<MediaFile>();
		model.setMediaFileList(mediaFiles);
		fileTable.tableChanged(new TableModelEvent(model));
	}

	public ArrayList<MediaFile> getSelectedFilesFromFileTable(){
		int[] selected = fileTable.getSelectedRows();
		ArrayList<MediaFile> selectedFiles = new ArrayList<MediaFile>();
		for(int i : selected){
			selectedFiles.add(mediaFiles.get(i));
		}
		return selectedFiles;
	}

	public String[] getListOfVideoProfiles() {
		return listOfVideoProfiles;
	}

	public void setListOfVideoProfiles(String[] listOfVideoProfiles) {
		this.listOfVideoProfiles = listOfVideoProfiles;
	}

	public String[] getListOfAudioProfiles() {
		return listOfAudioProfiles;
	}

	public void setListOfAudioProfiles(String[] listOfAudioProfiles) {
		this.listOfAudioProfiles = listOfAudioProfiles;
	}

	public String[] getListOfMediaContainers() {
		return listOfMediaContainers;
	}

	public void setListOfMediaContainers(String[] listOfMediaContainers) {
		this.listOfMediaContainers = listOfMediaContainers;
	}

	public String getSelectedAudioProfile(){
		return (String)audioProfileComboBox.getSelectedItem();
	}

	public String getSelectedVideoProfile(){
		return (String)videoProfileComboBox.getSelectedItem();
	}

	public String getSelectedMediaContainerProfile(){
		return (String)mediaContainerComboBox.getSelectedItem();
	}

	public String getAvisynthScript(){
		return avisynthTextArea.getText().trim();
	}

	public int getDelay(){
		return (Integer)delaySpinner.getValue();
	}
	
	public boolean getLinkAudioJobWithAviSynthScript(){
		return directAudioJobsToAviSynthScript.isSelected();
	}
	
	public void setAviSynthMessage(String message){
		avisynthTextArea.setText(message);
	}

}
