package gui;

import java.io.File;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
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

public class MainWindow implements Application {
	private Window window = null;
	
	// assistant
	private TextInput txtImgSrc = null;
	private TextInput txtXmpSrc = null;
	private TextInput txtOutFolder = null;
	private Button buttonGenerateTimelapse = null;
	private Button buttonBrowseXmpSrc = null;
	private Button buttonBrowseImgSrc = null;
	private Button buttonBrowseOut = null;
	
	// interpolation
	private Checkbox cbIsExportJpg = null;
	private Checkbox cbIsExportMovie = null;
	
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

		bxmlSerializer.bind(this, MainWindow.class);
        
		// assistant
		txtXmpSrc = (TextInput) bxmlSerializer.getNamespace().get("txtXmpSrc");
		txtImgSrc = (TextInput) bxmlSerializer.getNamespace().get("txtImgSrc");
		txtOutFolder = (TextInput) bxmlSerializer.getNamespace().get("txtOutFolder");
		buttonBrowseXmpSrc = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseXmpSrc");
		buttonBrowseImgSrc = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseImgSrc");
		buttonBrowseOut = (PushButton) bxmlSerializer.getNamespace().get("buttonBrowseOut");
		buttonGenerateTimelapse = (Button) bxmlSerializer.getNamespace().get("buttonGenerateTimelapse");
		
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
			    
				// launch Main
				
				Alert.alert(MessageType.INFO, "Timelapse generated: " + txtOutFolder.getText(), window);
				
			}
		});
        
		



				// interpolation
		cbIsExportJpg = (Checkbox) bxmlSerializer.getNamespace().get("isExportJpg");
		cbIsExportMovie = (Checkbox) bxmlSerializer.getNamespace().get("isExportMovie");
		
		cbIsExportJpg.getButtonStateListeners().add(new ButtonStateListener() {
			@Override
			public void stateChanged(Button button, Button.State previousState) {
				updateMovieCheckbox();
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
		
		// Output current parameters
		System.out.println(sliderDeflick.getValue());
		
		
		
		
		
		window.open(display);
	}
	

	@Override
	public void suspend() throws Exception {
	}

	private void updateMovieCheckbox() {
		// Movie export enabled only if JPG is selected
		cbIsExportMovie.setEnabled(cbIsExportJpg.isSelected());
		// Movie not selected if JPG is not
		if (!cbIsExportJpg.isSelected()){
			cbIsExportMovie.setSelected(false);
		}
	}
	
	private void updateLabelDeflick() {
		labelDeflick.setText(Integer.toString(sliderDeflick.getValue()));
	}

}

