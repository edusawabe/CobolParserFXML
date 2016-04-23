package cobolparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

public class MainGUIController implements Initializable{
	@FXML
	private MenuItem miOpen;
	@FXML
	private MenuItem miClose;
	@FXML
	private Label lbDir;
	@FXML
	private TableView<ProgramsTableLine> tvTabProgs;
	@FXML
	private TableColumn<ProgramsTableLine, String> tcColunaArquivo;
	@FXML
	private TableColumn<ProgramsTableLine, String> tcColunaNome;
	@FXML
	private TableColumn<ProgramsTableLine, String> tcColunaStatus;
	@FXML
	private RadioButton rbGerarXMLSim;
	@FXML
	private RadioButton rbGerarXMLNao;
	@FXML
	private Button btProcessar;
	@FXML
	private Button btDetProg;
	@FXML
	private Label lbProcessados;
	@FXML
	private ProgressBar pbProcessados;
	@FXML
	private TextArea taAreatexto;

	private ObservableList<ProgramsTableLine> olTabelaProgramas = FXCollections.observableArrayList();
	private File dir;
	private FolderToXMLProcessor folderProcessor;
	private DBDriver dbDriver;
	private Timeline processWorker;
	private Timeline oPenProcess;
	private int fileInProcess;
	private int indFile;
	private File[] files;
	private Double di = new Double("0");
	private Double ds = new Double("0");
	private int totalFiles;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tcColunaArquivo.setCellValueFactory(new PropertyValueFactory<ProgramsTableLine, String>("arquivo"));
		tcColunaNome.setCellValueFactory(new PropertyValueFactory<ProgramsTableLine, String>("nomePrograma"));
		tcColunaStatus.setCellValueFactory(new PropertyValueFactory<ProgramsTableLine, String>("status"));
		tvTabProgs.getItems().setAll(olTabelaProgramas);
		dbDriver = new DBDriver();
		dbDriver.openConnection();
	}


	@FXML
	private void abrirPasta(Event event){
		olTabelaProgramas.clear();
		taAreatexto.setText("");
		DirectoryChooser dch = new DirectoryChooser();
		dch.setInitialDirectory(new File("C:\\E!SuperCopia"));
		dir = dch.showDialog(lbDir.getScene().getWindow());
		indFile = 0;

		if (dir == null)
			return;
		lbDir.setText("Diretorio: " + dir.getAbsolutePath());
		files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if ((!files[i].getName().contains(".xml")) && files[i].isFile() && (!files[i].getName().contains("RFINW")))
				totalFiles++;
		}
		totalFiles--;

		oPenProcess = new Timeline();
		oPenProcess.setCycleCount(Timeline.INDEFINITE);
		oPenProcess.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1), new EventHandler() {
			// KeyFrame event handler
			@Override
			public void handle(Event event) {
				try {
					doOpenDir();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}));
		oPenProcess.playFromStart();
	}

	@FXML
	private void processarArquivos(Event event){
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle("Selecionar Opção de XML");
		a.setContentText("Favor Selecionar Uma das opções de Gerar XML.");
		fileInProcess = 0;

		if(!rbGerarXMLNao.isSelected() && !rbGerarXMLSim.isSelected()){
			a.show();
			return;
		}

		if (dir != null) {
			if (dir.exists()) {
				folderProcessor = new FolderToXMLProcessor();
				folderProcessor.setFolder(dir);
				folderProcessor.setOlTabelaProgramas(olTabelaProgramas);
				if (rbGerarXMLSim.isSelected())
					folderProcessor.setSkipExistingFile(true);
				if (rbGerarXMLNao.isSelected())
					folderProcessor.setSkipExistingFile(false);
				folderProcessor.setDb2Driver(new DBDriver());

				processWorker = new Timeline();
				processWorker.setCycleCount(Timeline.INDEFINITE);
				processWorker.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1), new EventHandler() {
					// KeyFrame event handler
					@Override
					public void handle(Event event) {
						try {
							doProcess();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}));
				processWorker.playFromStart();

				taAreatexto.setText(folderProcessor.getProcessResult());
			}
		}
	}

	private void doProcess() throws Exception {
		if (fileInProcess >= olTabelaProgramas.size()) {
			processWorker.stop();
			return;
		} else {
			if ((!files[fileInProcess].getName().contains(".xml")) && files[fileInProcess].isFile()
					&& (!files[fileInProcess].getName().contains("RFINW"))) {
				taAreatexto.setText("");
				lbProcessados.setText("0/" + olTabelaProgramas.size());
				ds = ds.parseDouble("" + olTabelaProgramas.size());
				taAreatexto.setText(taAreatexto.getText() + folderProcessor.processAndCreateFile(fileInProcess));
				di = di.parseDouble("" + fileInProcess);
				di = di + 1;
				pbProcessados.setProgress(di / ds);
				lbProcessados.setText("" + fileInProcess + "/" + olTabelaProgramas.size());
				tvTabProgs.scrollTo(fileInProcess);
				tvTabProgs.getSelectionModel().focus(fileInProcess);
				tvTabProgs.refresh();
			}
			fileInProcess++;
		}
	}

	private void doOpenDir() throws Exception {
		if (indFile >= totalFiles) {
			oPenProcess.stop();
			return;
		} else {
			if ((!files[indFile].getName().contains(".xml")) && files[indFile].isFile()) {
				ProgramsTableLine pgmLine = new ProgramsTableLine();
				pgmLine.setArquivo(files[indFile].getAbsolutePath());
				olTabelaProgramas.add(pgmLine);
				tvTabProgs.setItems(olTabelaProgramas);

				ds = ds.parseDouble("" + totalFiles);
				di = di.parseDouble("" + indFile);
				di = di + 1;
				pbProcessados.setProgress(di / ds);
				lbProcessados.setText("Carregando Arquivos:" + indFile + "/" + totalFiles);
				tvTabProgs.scrollTo(indFile);
			}
			indFile++;
		}
	}
}
