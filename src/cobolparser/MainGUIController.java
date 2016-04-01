package cobolparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
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
	private Task processWorker;

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
		if (dir == null)
			return;
		lbDir.setText("Diretorio: " + dir.getAbsolutePath());

		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if ((!files[i].getName().contains(".xml")) && files[i].isFile()) {
				ProgramsTableLine pgmLine = new ProgramsTableLine();
				pgmLine.setArquivo(files[i].getAbsolutePath());
				olTabelaProgramas.add(pgmLine);
				tvTabProgs.setItems(olTabelaProgramas);
			}
		}

/*		Task<String> t = new Task<String>() {

			@Override
			protected String call() throws Exception {
				for (int i = 0; i < files.length; i++) {
					if (!files[i].getName().contains(".xml")) {
						ProgramsTableLine pgmLine = new ProgramsTableLine();
						pgmLine.setArquivo(files[i].getAbsolutePath());
						olTabelaProgramas.add(pgmLine);
						tvTabProgs.setItems(olTabelaProgramas);
					}
				}
				return "OK";
			}
		};*/

	}

	@FXML
	private void processarArquivos(Event event){
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle("Selecionar Opção de XML");
		a.setContentText("Favor Selecionar Uma das opções de Gerar XML.");

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
				processWorker = createWorker();
				new Thread(processWorker).start();
				taAreatexto.setText(folderProcessor.getProcessResult());
			}
		}
	}

	private Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
				pbProcessados.setProgress(0);
            	taAreatexto.setText("");
            	lbProcessados.setText("0/" + olTabelaProgramas.size());
            	Double di = new Double("0"), ds = new Double("0");
            	ds = ds.parseDouble("" + olTabelaProgramas.size());
				for (int i = 0; i < olTabelaProgramas.size(); i++) {
					taAreatexto.setText(taAreatexto.getText() + folderProcessor.processAndCreateFile(i));
					Thread.sleep(10);
					di  = di.parseDouble("" + i);
					di = di + 1;
					pbProcessados.setProgress(di/ds);
					lbProcessados.setText("" + i + "/" + olTabelaProgramas.size());
					//tvTabProgs.scrollTo(i);
					tvTabProgs.getSelectionModel().focus(i);
					tvTabProgs.refresh();
				}
				pbProcessados.setProgress(1);
				Thread.sleep(10);
                return true;
            }
        };
    }
}
