package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import controller.RilevazioniController;
import view.AppLogger.DebugMode;

import static view.AppLogger.log;

public class RilevazioniPusherGUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1691637444499518758L;
	private static final int DEFAULT_CONFIG = 1; // 0 http/localhost, 1 https/ip
	private static final String DEFAULT_FILE_RILEVAZIONI = "./rilevazioni/rilevazioni.txt";
	private static final double MULTIPLIER;
	
	static {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if(d.getWidth()>1920) {
			MULTIPLIER = 1.5;
		}else {
			if(d.getWidth()==1920) {
				MULTIPLIER = 1.25;
			}else {
				MULTIPLIER = 1;
			}
		}
	}
	
	private static final int FRAME_WIDTH = (int) (700 * MULTIPLIER);
	private static final int FRAME_HEIGHT = (int) (700 * MULTIPLIER);
	private static final int MARGIN_NORTH_SOUTH_PANEL = (int) (10 * MULTIPLIER);
	private static final int MARGIN_WEST_EAST_PANEL = (int) (10 * MULTIPLIER);
	private static final int MARGIN_NORTH_SOUTH_TITLE = (int) (30 * MULTIPLIER);
	private static final int MARGIN_NORTH_SOUTH_ELEMENTS = (int) (20 * MULTIPLIER);
	private static final int MARGIN_WEST_EAST_ELEMENTS = (int) (20 * MULTIPLIER);
	private static final int LABEL_WIDTH = 100;
	private static final int LABEL_HEIGHT = 25;
	private static final int COMBOBOX_WIDTH = 145;
	private static final int COMBOBOX_HEIGHT = 25;
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 25;
	private static final int LOG_AREA_WIDTH = FRAME_WIDTH - (2*MARGIN_WEST_EAST_PANEL) - 15;
	private static final double LOG_AREA_MULTIPLIER;
	
	static {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if(d.getWidth()>1920) {
			LOG_AREA_MULTIPLIER = 1.5;
		}else {
			if(d.getWidth()==1920) {
				LOG_AREA_MULTIPLIER = 1.35;
			}else {
				LOG_AREA_MULTIPLIER = 1;
			}
		}
	}
	private static final int LOG_AREA_HEIGHT = (int) (350 * LOG_AREA_MULTIPLIER );
	private JPanel mainPanel;
	private SpringLayout layout;
	private JLabel titleLabel;
	private JLabel hostnameLabel;
	private String[] hostnameArray = { "localhost","www.itisvallauri.net" };
	private JLabel portLabel;
	private Integer[] portArray = { 80,443 };
	private JLabel protocolLabel;
	private String[] protocolArray = { "HTTP","HTTPS" };
	private String[] requestUrlArray = { "ProgettoMeteo","meteo" };
	private boolean[] filterByDataArray = { false, true };
	private JComboBox<String> hostnameComboBox;
	private JComboBox<Integer> portComboBox;
	private JComboBox<String> protocolComboBox;
	private JCheckBox filterByDataCheckBox;
	private JButton pushButton;
	private JButton exitButton;
	private JLabel requestUrlLabel;
	private JComboBox<String> requestUrlComboBox;
	private JLabel pathFileRilevazioniLabel;
	private JButton pathFileRilevazioniButton;
	private JFileChooser pathFileRilevazioniChooser;
	
	private JLabel intervalloTemporaleLabel;
	private Long[] intervalliTemporaliArray = { 1l, 5l, 10l, 15l, 30l, 60l };
	private JComboBox<Long> intervalloTemporaleComboBox;
	private JButton startRoutineButton;
	private JButton stopRoutineButton;

	
	private JTextArea textArea;
	private JScrollPane logArea;
	
	private String hostname = hostnameArray[DEFAULT_CONFIG];
	private int port = portArray[DEFAULT_CONFIG];
	private String requestUrl = requestUrlArray[DEFAULT_CONFIG];
	private String protocol = protocolArray[DEFAULT_CONFIG];
	private String file = DEFAULT_FILE_RILEVAZIONI;
	private boolean filterByData = filterByDataArray[DEFAULT_CONFIG];
	
	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> serviceHandler;
	
	
	public RilevazioniPusherGUI() {
		super("App Invio Dati Rilevazioni - Sviluppato dall'ing.Bonifazi Andrea");				
		setSize(FRAME_WIDTH,FRAME_HEIGHT);
		centerLocation();
		createComponents();
		layoutComponents();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		log("Creazione Rilevazioni GUI completata.");
		log("GUI Size - Width: " + getWidth() + "\tHeight: " + getHeight());
	}
	
	public void centerLocation() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)((d.getWidth()-getWidth())/2), (int)((d.getHeight()-getHeight())/2));
	}
	
	private void createComponents() {
		mainPanel = (JPanel) getContentPane();
		mainPanel.setBackground(Color.WHITE);
		titleLabel = new JLabel("Invio dati Rilevazioni");
		titleLabel.setFont(new Font("Serif",Font.ITALIC,32));
		hostnameLabel = new JLabel("Host: ");
		hostnameLabel.setPreferredSize(new Dimension(LABEL_WIDTH,LABEL_HEIGHT));
		hostnameComboBox = new JComboBox<>(hostnameArray);
		hostnameComboBox.setSelectedIndex(DEFAULT_CONFIG);
		hostnameComboBox.setPreferredSize(new Dimension(COMBOBOX_WIDTH,COMBOBOX_HEIGHT));
		hostnameComboBox.addActionListener(this);	
		portLabel = new JLabel("Port: ");
		portLabel.setPreferredSize(new Dimension(LABEL_WIDTH,LABEL_HEIGHT));
		portComboBox = new JComboBox<>(portArray);
		portComboBox.setSelectedIndex(DEFAULT_CONFIG);
		portComboBox.setPreferredSize(new Dimension(COMBOBOX_WIDTH,COMBOBOX_HEIGHT));
		portComboBox.addActionListener(this);	
		protocolLabel = new JLabel("Protocol: ");
		protocolLabel.setPreferredSize(new Dimension(LABEL_WIDTH,LABEL_HEIGHT));
		protocolComboBox = new JComboBox<>(protocolArray);
		protocolComboBox.setSelectedIndex(DEFAULT_CONFIG);
		protocolComboBox.setPreferredSize(new Dimension(COMBOBOX_WIDTH,COMBOBOX_HEIGHT));
		protocolComboBox.addActionListener(this);
		filterByDataCheckBox = new JCheckBox("Filter By Data");
		filterByDataCheckBox.setSelected(false);
		filterByDataCheckBox.addActionListener(this);
		pushButton = new JButton("Push data");
		pushButton.addActionListener(this);
		pushButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		exitButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
		
		requestUrlLabel = new JLabel("Request URL: ");
		requestUrlLabel.setPreferredSize(new Dimension(LABEL_WIDTH,LABEL_HEIGHT));
		requestUrlComboBox = new JComboBox<>(requestUrlArray);
		requestUrlComboBox.setSelectedIndex(DEFAULT_CONFIG);
		requestUrlComboBox.setToolTipText("Select 'ProgettoMeteo' per il servizio in localhost, 'meteo' per il servizio su Aruba");
		requestUrlComboBox.setPreferredSize(new Dimension(COMBOBOX_WIDTH,COMBOBOX_HEIGHT));
		requestUrlComboBox.addActionListener(this);
		
		pathFileRilevazioniLabel = new JLabel("File Rilevazioni: ");
		pathFileRilevazioniLabel.setPreferredSize(new Dimension(LABEL_WIDTH,LABEL_HEIGHT));
		pathFileRilevazioniButton = new JButton("Open");
		pathFileRilevazioniButton.addActionListener(this);
		pathFileRilevazioniButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
		pathFileRilevazioniChooser = new JFileChooser("./rilevazioni");		
		
		intervalloTemporaleLabel = new JLabel("Intervallo: ");
		intervalloTemporaleComboBox = new JComboBox<>(intervalliTemporaliArray);
		intervalloTemporaleComboBox.setToolTipText("Scegli l'intervallo temporale in minuti con cui ripetere l'invio dei dati delle rilavazioni al Server");
		startRoutineButton = new JButton("Start Routine");
		startRoutineButton.addActionListener(this);
		stopRoutineButton = new JButton("Stop Routine");
		stopRoutineButton.addActionListener(this);
		stopRoutineButton.setEnabled(false);
		
		textArea = new JTextArea();
		logArea = new JScrollPane(textArea);
		logArea.setPreferredSize(new Dimension(LOG_AREA_WIDTH, LOG_AREA_HEIGHT));
		textArea.setEnabled(false);		
		textArea.setDisabledTextColor(Color.BLACK);
		
		mainPanel.add(titleLabel);
		mainPanel.add(hostnameLabel);
		mainPanel.add(hostnameComboBox);
		mainPanel.add(portLabel);
		mainPanel.add(portComboBox);
		mainPanel.add(protocolLabel);
		mainPanel.add(protocolComboBox);
		mainPanel.add(filterByDataCheckBox);
		mainPanel.add(pushButton);
		mainPanel.add(exitButton);
		mainPanel.add(requestUrlLabel);
		mainPanel.add(requestUrlComboBox);
		mainPanel.add(pathFileRilevazioniLabel);
		mainPanel.add(pathFileRilevazioniButton);
		mainPanel.add(intervalloTemporaleLabel);
		mainPanel.add(intervalloTemporaleComboBox);
		mainPanel.add(startRoutineButton);
		mainPanel.add(stopRoutineButton);
		mainPanel.add(logArea);
		
		AppLogger.setLogArea(textArea);
		AppLogger.setDebugMode(DebugMode.CONSOLE_GUI_FILE);
	}
	
	private void layoutComponents() {
		layout = new SpringLayout();
		setLayout(layout);
		
		layout.putConstraint(SpringLayout.NORTH, titleLabel, MARGIN_NORTH_SOUTH_PANEL, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, titleLabel, (int)(100*LOG_AREA_MULTIPLIER), SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, hostnameLabel, MARGIN_NORTH_SOUTH_TITLE, SpringLayout.SOUTH, titleLabel);
		layout.putConstraint(SpringLayout.WEST, hostnameLabel, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, hostnameComboBox, MARGIN_NORTH_SOUTH_TITLE, SpringLayout.SOUTH, titleLabel);
		layout.putConstraint(SpringLayout.WEST, hostnameComboBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, hostnameLabel);
		layout.putConstraint(SpringLayout.NORTH, portLabel, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, hostnameLabel);
		layout.putConstraint(SpringLayout.WEST, portLabel, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, portComboBox, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, hostnameLabel);
		layout.putConstraint(SpringLayout.WEST, portComboBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, portLabel);
		layout.putConstraint(SpringLayout.NORTH, protocolLabel, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, portLabel);
		layout.putConstraint(SpringLayout.WEST, protocolLabel, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, protocolComboBox, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, portLabel);
		layout.putConstraint(SpringLayout.WEST, protocolComboBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, protocolLabel);
		layout.putConstraint(SpringLayout.NORTH, filterByDataCheckBox, MARGIN_NORTH_SOUTH_TITLE, SpringLayout.SOUTH, titleLabel);
		layout.putConstraint(SpringLayout.WEST, filterByDataCheckBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, hostnameComboBox);
		layout.putConstraint(SpringLayout.NORTH, pushButton, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, hostnameLabel);
		layout.putConstraint(SpringLayout.WEST, pushButton, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, portComboBox);
		layout.putConstraint(SpringLayout.NORTH, exitButton, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, pushButton);
		layout.putConstraint(SpringLayout.WEST, exitButton, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, protocolComboBox);
		layout.putConstraint(SpringLayout.NORTH, requestUrlLabel, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, protocolLabel);
		layout.putConstraint(SpringLayout.WEST, requestUrlLabel, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, requestUrlComboBox, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, protocolLabel);
		layout.putConstraint(SpringLayout.WEST, requestUrlComboBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, requestUrlLabel);
		layout.putConstraint(SpringLayout.NORTH, pathFileRilevazioniLabel, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, pathFileRilevazioniLabel, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, pathFileRilevazioniButton, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, pathFileRilevazioniButton, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, pathFileRilevazioniLabel);
		
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleLabel, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleLabel, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, pathFileRilevazioniButton);
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleComboBox, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleComboBox, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.NORTH, startRoutineButton, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, startRoutineButton, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, intervalloTemporaleComboBox);
		layout.putConstraint(SpringLayout.NORTH, stopRoutineButton, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, requestUrlLabel);
		layout.putConstraint(SpringLayout.WEST, stopRoutineButton, MARGIN_WEST_EAST_ELEMENTS, SpringLayout.EAST, startRoutineButton);
				
		
		layout.putConstraint(SpringLayout.NORTH, logArea, MARGIN_NORTH_SOUTH_ELEMENTS, SpringLayout.SOUTH, pathFileRilevazioniLabel);
		layout.putConstraint(SpringLayout.WEST, logArea, MARGIN_WEST_EAST_PANEL, SpringLayout.WEST, mainPanel);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==hostnameComboBox) {
			hostname = (String) hostnameComboBox.getSelectedItem();
			requestUrl = requestUrlArray[hostnameComboBox.getSelectedIndex()];
			requestUrlComboBox.setSelectedIndex(hostnameComboBox.getSelectedIndex());
		}
		if(e.getSource()==requestUrlComboBox) {
			requestUrl = (String) requestUrlComboBox.getSelectedItem();
			hostname = hostnameArray[requestUrlComboBox.getSelectedIndex()];
			hostnameComboBox.setSelectedIndex(requestUrlComboBox.getSelectedIndex());
		}
		if(e.getSource()==portComboBox) {
			port = (int) portComboBox.getSelectedItem();
			protocol = protocolArray[portComboBox.getSelectedIndex()];
			protocolComboBox.setSelectedIndex(portComboBox.getSelectedIndex());
		}
		if(e.getSource()==protocolComboBox) {
			protocol = (String) protocolComboBox.getSelectedItem();
			port = portArray[protocolComboBox.getSelectedIndex()];
			portComboBox.setSelectedIndex(protocolComboBox.getSelectedIndex());
		}
		if(e.getSource()==exitButton) {
			System.out.println("Richiesta di terminazione ricevuta, shutdown imminente...");
			System.exit(0);
		}		
		if(e.getSource()==pushButton) {
			if(pushButton.isEnabled()) {
				if(protocol.equalsIgnoreCase("HTTPS")) {
					new Thread(new RilevazioniController(hostname,port,true,requestUrl,file,filterByData)).start();
					pushButton.setEnabled(false);
				}else {
					if(protocol.equalsIgnoreCase("HTTP")) {
						new Thread(new RilevazioniController(hostname,port,false,requestUrl,file,filterByData)).start();
					}else {
						log("Protocollo specificato non riconosciuto.");
					}
				}				
			}
		}
		if(e.getSource()==filterByDataCheckBox) {
			filterByData = filterByDataCheckBox.isSelected();
			log("Filter by data: " + filterByData);
		}
		if(e.getSource()==pathFileRilevazioniButton) {
			int result = pathFileRilevazioniChooser.showOpenDialog(this);
			switch(result) {
				case JFileChooser.CANCEL_OPTION:{
					log("Scelta del file delle misurazioni, operazione annullata dall'utente");
					break;
				}
				case JFileChooser.APPROVE_OPTION:{
					file = pathFileRilevazioniChooser.getSelectedFile().toPath().toString();
					log("File rilevazioni corrente: " + file);
					break;
				}
				case JFileChooser.ERROR_OPTION:{
					log("Errore in fase di scelta del file delle misurazioni ");
					break;
				}
			}
		}
		
		if(e.getSource()==startRoutineButton) {
			executorService = Executors.newScheduledThreadPool(1);
			serviceHandler = executorService.scheduleAtFixedRate(()->{
				if(protocol.equalsIgnoreCase("HTTPS")) {
					new Thread(new RilevazioniController(hostname,port,true,requestUrl,file,filterByData)).start();					
				}else {
					if(protocol.equalsIgnoreCase("HTTP")) {
						new Thread(new RilevazioniController(hostname,port,false,requestUrl,file,filterByData)).start();
					}else {
						log("Invio programmato dei dati fallito. Il Protocollo specificato non riconosciuto.");
					}
				}	
			}, (long)0, (long)intervalloTemporaleComboBox.getSelectedItem(), TimeUnit.MINUTES);
			
			startRoutineButton.setEnabled(false);
			stopRoutineButton.setEnabled(true);
		}		
		if(e.getSource()==stopRoutineButton) {
			new Thread( () -> {
				serviceHandler.cancel(true);
			    executorService.shutdown();
			    stopRoutineButton.setEnabled(false);
				startRoutineButton.setEnabled(true);
				log("Task cancellato correttamente");
			}).start();			
		}
		
	}

}
