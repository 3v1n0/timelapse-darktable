package gui;

import java.io.File;
import java.io.IOException;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonGroup;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.Slider;
import org.apache.pivot.wtk.SliderValueListener;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;

import com.martiansoftware.jsap.JSAPException;

import core.TLDTCore;

public class TLDTWindow implements Application {
	private Window window = null;

	// assistant
	private TextInput txtImgSrc = null;
	private TextInput txtXmpSrc = null;
	private TextInput txtOutFolder = null;
	private Button buttonGenerateTimelapse = null;
	private Button buttonBrowseXmpSrc = null;
	private Button buttonBrowseImgSrc = null;
	private Button buttonBrowseOut = null;

	// params
	private Checkbox cbIsDeflick = null;
	private Checkbox cbIsExportJpg = null;
	private Checkbox cbIsExportMovie = null;
	private ButtonGroup rbInterpType = null;
	private TextInput txtWidth = null;
	private TextInput txtHeigth = null;

	// deflick
	private Slider sliderDeflick = null;
	private Label labelDeflick = null;

	@Override
	public void resume() throws Exception {
	}

	@Override
	public boolean shutdown(boolean arg0) throws Exception {
		return false;
	}

	@Override
	public void startup(Display display, Map<String, String> properties)	throws Exception {
		BXMLSerializer bxmlSerializer = new BXMLSerializer();
		window = (Window) bxmlSerializer.readObject(getClass().getResource("timelapse-darktable.bxml"));

		bxmlSerializer.bind(this, TLDTWindow.class);

		// assistant
		txtXmpSrc = (TextInput) bxmlSerializer.getNamespace().get("txtXmpSrc");
		txtImgSrc = (TextInput) bxmlSerializer.getNamespace().get("txtImgSrc");
		txtOutFolder = (TextInput) bxmlSerializer.getNamespace().get("txtOutFolder");
		buttonBrowseXmpSrc = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseXmpSrc");
		buttonBrowseImgSrc = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseImgSrc");
		buttonBrowseOut = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseOut");
		buttonGenerateTimelapse = (Button) bxmlSerializer.getNamespace().get("buttonGenerateTimelapse");
		cbIsDeflick = (Checkbox) bxmlSerializer.getNamespace().get("isDeflick");
		cbIsExportJpg = (Checkbox) bxmlSerializer.getNamespace().get("isExportJpg");
		txtHeigth = (TextInput) bxmlSerializer.getNamespace().get("txtHeigth");
		txtWidth = (TextInput) bxmlSerializer.getNamespace().get("txtWidth");
		cbIsExportMovie = (Checkbox) bxmlSerializer.getNamespace().get("isExportMovie");
		rbInterpType = (ButtonGroup) bxmlSerializer.getNamespace().get("rbInterpType");


		buttonBrowseXmpSrc.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {

				final FileBrowserSheet fileBrowserSheet =
						new FileBrowserSheet(FileBrowserSheet.Mode.SAVE_TO);

				if(txtXmpSrc != null && txtXmpSrc.getText().length() > 0 &&
						txtXmpSrc.getText().indexOf("\\") >0  ){
					try{
						String text = txtXmpSrc.getText();
						int index = text.lastIndexOf("\\");
						fileBrowserSheet.setRootDirectory(
								new File(text.substring(0,index)));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				};
				fileBrowserSheet.open(window, new SheetCloseListener() {
					@Override
					public void sheetClosed(Sheet sheet) {
						if (sheet.getResult()) {
							Sequence<File> selectedFiles = fileBrowserSheet.getSelectedFiles();
							if(selectedFiles.getLength() > 0){
								String fileName = selectedFiles.get(0).getAbsoluteFile().toString();
								txtXmpSrc.setText(fileName);
							}
						} 
					}
				});
			}
		});	

		buttonBrowseImgSrc.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {

				final FileBrowserSheet fileBrowserSheet =
						new FileBrowserSheet(FileBrowserSheet.Mode.SAVE_TO);

				if(txtImgSrc != null && txtImgSrc.getText().length() > 0 &&
						txtImgSrc.getText().indexOf("\\") >0  ){
					try{
						String text = txtImgSrc.getText();
						int index = text.lastIndexOf("\\");
						fileBrowserSheet.setRootDirectory(
								new File(text.substring(0,index)));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				};
				fileBrowserSheet.open(window, new SheetCloseListener() {
					@Override
					public void sheetClosed(Sheet sheet) {
						if (sheet.getResult()) {
							Sequence<File> selectedFiles = fileBrowserSheet.getSelectedFiles();
							if(selectedFiles.getLength() > 0){
								String fileName = selectedFiles.get(0).getAbsoluteFile().toString();
								txtImgSrc.setText(fileName);
							}
						} 
					}
				});
			}
		});

		buttonBrowseOut.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				final FileBrowserSheet fileBrowserSheet =
						new FileBrowserSheet(FileBrowserSheet.Mode.SAVE_TO);

				if(txtOutFolder != null && txtOutFolder.getText().length() > 0 &&
						txtOutFolder.getText().indexOf("\\") >0  ){
					try{
						String text = txtOutFolder.getText();
						int index = text.lastIndexOf("\\");
						fileBrowserSheet.setRootDirectory(
								new File(text.substring(0,index)));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				};
				fileBrowserSheet.open(window, new SheetCloseListener() {
					@Override
					public void sheetClosed(Sheet sheet) {
						if (sheet.getResult()) {
							Sequence<File> selectedFiles = fileBrowserSheet.getSelectedFiles();
							if(selectedFiles.getLength() > 0){
								String fileName = selectedFiles.get(0).getAbsoluteFile().toString();
								txtOutFolder.setText(fileName);
							}
						} 
					}
				});
			}
		});


		buttonGenerateTimelapse.getButtonPressListeners().add(new ButtonPressListener() {

			@Override
			public void buttonPressed(Button arg0) {

				if(txtXmpSrc.getText() == null || txtXmpSrc.getText().length() == 0
						|| txtImgSrc.getText() == null || txtImgSrc.getText().length() == 0
						|| txtOutFolder.getText() == null || txtOutFolder.getText().length() == 0){
					Alert.alert(MessageType.ERROR, "Please insert 3 locations (2 inputs, 1 output).", window);
					return;
				}

				// launch MainScript
				launchTLDTCore();
				Alert.alert(MessageType.INFO, "Timelapse generated: " + txtOutFolder.getText(), window);

			}
		});

		cbIsExportJpg.getButtonStateListeners().add(new ButtonStateListener() {
			@Override
			public void stateChanged(Button button, Button.State previousState) {
				updateJpgExportRelated();
			}
		});		


		// deflick
		labelDeflick = (Label) bxmlSerializer.getNamespace().get("labelDeflick");
		sliderDeflick = (Slider) bxmlSerializer.getNamespace().get("sliderDeflick");

		sliderDeflick.getSliderValueListeners().add(new SliderValueListener() {
			@Override
			public void valueChanged(Slider slider, int previousValue) {
				updateLabelDeflick();
			}
		});		
		updateLabelDeflick();

		window.open(display);
	}


	@Override
	public void suspend() throws Exception {
	}

	private void updateJpgExportRelated() {
		// Movie export enabled only if JPG is selected
		cbIsExportMovie.setEnabled(cbIsExportJpg.isSelected());
		txtWidth.setEnabled(cbIsExportJpg.isSelected());
		txtHeigth.setEnabled(cbIsExportJpg.isSelected());
		// Movie reset if JPG is not selected
		if (!cbIsExportJpg.isSelected()){
			cbIsExportMovie.setSelected(false);
		}

	}

	private void updateLabelDeflick() {
		labelDeflick.setText(Integer.toString(sliderDeflick.getValue()));
	}

	private void launchTLDTCore(){
		// launch TLDTCore with CLI arguments
		try {
			new TLDTCore(genOptions());
		} catch (JSAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String[] genOptions() {
		int maxSize = 40;
		String[] options = new String[maxSize] ;
		int i=0;
		options[i] = "-x";i+=1;
		options[i] = txtXmpSrc.getText();i+=1;	
		options[i] = "-i";i+=1;
		options[i] = txtImgSrc.getText();i+=1;
		options[i] = "-o";i+=1;
		options[i] = txtOutFolder.getText();i+=1;
		options[i] = "-t";i+=1;
		options[i] = (String) rbInterpType.getSelection().getButtonData();i+=1;
		options[i] = "-w";i+=1;
		options[i] = txtWidth.getText();i+=1;
		options[i] = "-h";i+=1;
		options[i] = txtHeigth.getText();i+=1;
		if (cbIsDeflick.isSelected()){
			options[i] = "-d";i+=1;
		}
		if (cbIsExportJpg.isSelected()) {
			options[i] = "-j";i+=1;
		}
		if (cbIsExportMovie.isSelected()) {
			options[i] = "-m";i+=1;
		}
		// return only filled string array part
		String[] optionsNotNull = new String[i];
		for (int j = 0; j < optionsNotNull.length; j++) {
			optionsNotNull[j] = options[j];
		}
		return optionsNotNull;
	}

}

