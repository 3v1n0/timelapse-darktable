package gui;

import java.io.File;
import java.io.IOException;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Vote;
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
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.Window;

import com.martiansoftware.jsap.JSAPException;

import core.TLDTCore;

public class TLDTWindow implements Application {
	private Window window = null;

	// assistant
	private TextInput txtImgSrc = null;
	private TextInput txtXmpSrc = null;
	private TextInput txtOutFolder = null;
	private Button buttonBrowseXmpSrc = null;
	private Button buttonBrowseImgSrc = null;
	private Button buttonBrowseOut = null;
	private Button buttonGenerateTimelapse = null;

	// params
	private Checkbox cbIsDeflick = null;
	private Checkbox cbIsExportJpg = null;
	private Checkbox cbIsExportMovie = null;
	private TextInput txtWidth = null;
	private TextInput txtHeigth = null;

	// interpolation
	private ButtonGroup rbInterpType = null;
	private Button buttonInterpolateXmp = null;

	// deflick
	private Slider sliderDeflick = null;
	private Label labelDeflick = null;
	private Button buttonDeflickFilter = null;

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

		// id
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
		buttonInterpolateXmp = (Button) bxmlSerializer.getNamespace().get("buttonInterpolateXmp");
		labelDeflick = (Label) bxmlSerializer.getNamespace().get("labelDeflick");
		sliderDeflick = (Slider) bxmlSerializer.getNamespace().get("sliderDeflick");


		buttonBrowseXmpSrc.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				browseFile(txtXmpSrc,window);
			}
		});	

		buttonBrowseImgSrc.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				browseFile(txtImgSrc,window);
			}
		});

		buttonBrowseOut.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				browseFile(txtOutFolder,window);
			}
		});
		
		buttonGenerateTimelapse.getButtonPressListeners().add(new ButtonPressListener() {

			@Override
			public void buttonPressed(Button arg0) {

				// launch MainScript
				boolean isOk = checkInputs();
				if (isOk) {
					generateTimelapse();
					Alert.alert(MessageType.INFO, "Timelapse generated: " + txtOutFolder.getText(), window);
				}
			}
		});

		buttonInterpolateXmp.getButtonPressListeners().add(new ButtonPressListener() {

			@Override
			public void buttonPressed(Button arg0) {
				//
				//				if(txtXmpSrc.getText() == null || txtXmpSrc.getText().length() == 0
				//						|| txtImgSrc.getText() == null || txtImgSrc.getText().length() == 0
				//						|| txtOutFolder.getText() == null || txtOutFolder.getText().length() == 0){
				//					Alert.alert(MessageType.ERROR, "Please insert 3 locations (2 inputs, 1 output).", window);
				//					return;
				//				}

				// launch MainScript
				System.out.println("button pressed");
				interpolateXmp();
				Alert.alert(MessageType.INFO, 
						rbInterpType.getSelection().getButtonData()+" interpolatation done", window);

			}

		});

		cbIsExportJpg.getButtonStateListeners().add(new ButtonStateListener() {
			@Override
			public void stateChanged(Button button, Button.State previousState) {
				updateJpgExportRelated();
			}
		});		

		sliderDeflick.getSliderValueListeners().add(new SliderValueListener() {
			@Override
			public void valueChanged(Slider slider, int previousValue) {
				updateLabelDeflick();
			}
		});		
		updateLabelDeflick();

		window.open(display);
	}


	protected boolean checkInputs() {
		boolean isOk = false;
		if(txtXmpSrc.getText() == null || txtXmpSrc.getText().length() == 0
				|| txtImgSrc.getText() == null || txtImgSrc.getText().length() == 0
				|| txtOutFolder.getText() == null || txtOutFolder.getText().length() == 0){
			Alert.alert(MessageType.INFO, 
				"Complete all folder paths first", window);
		} else {
			isOk = true;
		}
		return isOk;		
	}

	protected void browseFile(final TextInput txt, Window win) {
		
		final FileBrowserSheet fileBrowserSheet =
				new FileBrowserSheet(FileBrowserSheet.Mode.SAVE_TO);

		if(txt != null && txt.getText().length() > 0 &&
				txt.getText().indexOf("\\") >0  ){
			try{
				String text = txt.getText();
				int index = text.lastIndexOf("\\");
				fileBrowserSheet.setRootDirectory(
						new File(text.substring(0,index)));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		};
		fileBrowserSheet.open(win, new SheetCloseListener() {
			@Override
			public void sheetClosed(Sheet sheet) {
				if (sheet.getResult()) {
					Sequence<File> selectedFiles = fileBrowserSheet.getSelectedFiles();
					if(selectedFiles.getLength() > 0){
						String fileName = selectedFiles.get(0).getAbsoluteFile().toString();
						txt.setText(fileName);
					}
				} 
			}
		});
		
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

	private void generateTimelapse(){
		// launch TLDTCore with CLI arguments
		try {
			TLDTCore core = new TLDTCore(genCliOptions());
			core.generateTimelapse();
		} catch (JSAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void interpolateXmp() {
		// launch TLDTCore and interpolate only
		try {
			TLDTCore core = new TLDTCore(genCliOptions());
			core.interpolateXmp();
		} catch (JSAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String[] genCliOptions() {
		int maxSize = 40;
		String[] cliOptions = new String[maxSize] ;
		int i=0;
		cliOptions[i] = "-x";i+=1;
		cliOptions[i] = txtXmpSrc.getText();i+=1;	
		cliOptions[i] = "-i";i+=1;
		cliOptions[i] = txtImgSrc.getText();i+=1;
		cliOptions[i] = "-o";i+=1;
		cliOptions[i] = txtOutFolder.getText();i+=1;
		cliOptions[i] = "-t";i+=1;
		cliOptions[i] = (String) rbInterpType.getSelection().getButtonData();i+=1;
		cliOptions[i] = "-w";i+=1;
		cliOptions[i] = txtWidth.getText();i+=1;
		cliOptions[i] = "-h";i+=1;
		cliOptions[i] = txtHeigth.getText();i+=1;
		cliOptions[i] = "-L";i+=1;
		cliOptions[i] = ""+sliderDeflick.getValue();i+=1;
		if (cbIsDeflick.isSelected()){
			cliOptions[i] = "-d";i+=1;
		}
		if (cbIsExportJpg.isSelected()) {
			cliOptions[i] = "-j";i+=1;
		}
		if (cbIsExportMovie.isSelected()) {
			cliOptions[i] = "-m";i+=1;
		}

		// return only filled string array part
		String[] cliOptionsNotNull = new String[i];
		for (int j = 0; j < cliOptionsNotNull.length; j++) {
			cliOptionsNotNull[j] = cliOptions[j];
		}
		return cliOptionsNotNull;
	}

}

