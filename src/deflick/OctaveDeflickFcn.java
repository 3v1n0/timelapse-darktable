package deflick;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OctaveDeflickFcn {
	
	// parameters of octave scripts
	public int lpFiltMinNum;
	public int spikeFiltMinNum;
	public double deltaLumThdMax;
	public String outFolderDeflick;
	public String outLuminanceFile;
	public String masterFileName;
	public String deflickFcnName;
	public String imgSizeXY;
	
	public OctaveDeflickFcn(String outFolderDeflick,String outLuminanceFile) {
		super();
		// default parameters
		this.lpFiltMinNum = 50;
		this.spikeFiltMinNum = 12;
		this.deltaLumThdMax = 0.02;
		this.imgSizeXY = "640,320";
		// files config
		this.outFolderDeflick = outFolderDeflick;
		this.outLuminanceFile = outLuminanceFile;
		// write files
		this.masterFileName = "master.m";
		this.deflickFcnName = "deflick";
	}
	
	public void setLpFiltMinNum(int lpFiltMinNum) {
		this.lpFiltMinNum = lpFiltMinNum;
	}
	
	public void setSpikeFiltMinNum(int spikeFiltMinNum) {
		this.spikeFiltMinNum = spikeFiltMinNum;
	}
	
	public void setDeltaLumThdMax(double deltaLumThdMax) {
		this.deltaLumThdMax = deltaLumThdMax;
	}
	
	public void writeFiles() throws IOException {

		// regression on luminance points with octave script (write the "master" script)
		BufferedWriter outOctaveMaster = new BufferedWriter(new FileWriter(this.outFolderDeflick+"/"+this.masterFileName));
		outOctaveMaster.write("#!/usr/bin/octave -qf"+"\n");
		outOctaveMaster.write("addpath('"+outFolderDeflick+"');"+"\n");
		outOctaveMaster.write(this.deflickFcnName+"('"+outFolderDeflick+"/"+outLuminanceFile+"');"+"\n");
		outOctaveMaster.close();

		// write the octave function into a file in the output folder
		// copy/paste octave script and find/replace
		// first \n in \\n
		// then regexp : ^(.*)\n   by  "$1\\n"+\n
		// change hard-coded parameter in ..."+this.paramName+"...
		String deflickFcnCore = "function "+this.deflickFcnName+"(luminanceTableFic)"+
				"% compute deflickering parameters from luminance table\n"+
				"\n"+
				"% retrieve source luminance table\n"+
				"in_signal = load(luminanceTableFic);\n"+
				"in_signal = in_signal(:); % column\n"+
				"\n"+
				"% first : coarse filtering with window averaging\n"+
				"[avgSignal] = windowAverage(in_signal,24);\n"+
				"\n"+
				"% standard variation of luminance wrt average\n"+
				"stdLum = std(avgSignal-in_signal);\n"+
				"\n"+
				"% advanced filtering parameters\n"+
				"N_LpFilt    = min("+this.lpFiltMinNum+",floor(length(in_signal)/2));   % Window size for filtering\n"+
				"N_SpikeFilt = min("+this.spikeFiltMinNum+",floor(length(in_signal)/4+1)); % Size of filtered spike\n"+
				"dL_max      = max("+this.deltaLumThdMax+",1.5*stdLum);           % 'normal' deltaL max (above, it is considered as a spike)\n"+
				"isPlotFlag  = true;                                 % output a grapics with deflickering source/target curves\n"+
				"\n"+
				"% filtering (deflickering target)\n"+
				"[filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag);\n"+
				"\n"+
				"% print target values\n"+
				"[lumPath,lumFic,lumExt] = fileparts(luminanceTableFic);\n"+
				"printTable(fullfile(lumPath,[lumFic '_deflick' lumExt]),filt_signal);\n"+
				"\n"+
				"% print graphics\n"+
				"if isPlotFlag  == true\n"+
				"	print(fullfile(lumPath,[lumFic '_deflick.png']),'-S"+this.imgSizeXY+"');\n"+
				"end\n"+
				"\n"+
				"end\n"+
				"\n"+
				"function [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag)\n"+
				"% Filtering spikes and averaging input signal\n"+
				"%\n"+
				"% SYNTAX\n"+
				"% [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt [,isPlotFlag] )\n"+
				"%\n"+
				"% OPTIONS\n"+
				"% isPlotFlag : boolean\n"+
				"\n"+
				"if nargin<5\n"+
				"	isPlotFlag=0;\n"+
				"end\n"+
				"\n"+
				"Ntot=length(in_signal);\n"+
				"N_SpikeFilt=min(N_SpikeFilt,Ntot);\n"+
				"\n"+
				"% FILTERING SPIKES : dL > dL_max\n"+
				"% -------------------------------\n"+
				"in_time=1:Ntot;\n"+
				"notRejIdx=1:Ntot;\n"+
				"[avgSignal] = windowAverage(in_signal,24);\n"+
				"notRejIdx = find(abs(avgSignal-in_signal)<dL_max);\n"+
				"if length(notRejIdx)<2\n"+
				" % keep 1st and last index\n"+
				" notRejIdx = unique([1 notRejIdx(:)' length(notRejIdx)]);\n"+
				"end\n"+
				"% delete spikes and replace them by linear interpolated values\n"+
				"filtSpikes_signal=interp1(in_time(notRejIdx),in_signal(notRejIdx),in_time,'linear','extrap');\n"+
				"\n"+
				"% final window-averaging \n"+
				"filt_signal = windowAverage(in_signal,N_LpFilt);\n"+
				"\n"+
				"% PLOT\n"+
				"% ----\n"+
				"if isPlotFlag\n"+
				"    figure('Visible','off'),\n"+
				"    plot(in_time,in_signal,'bx-') % input signal\n"+
				"    hold on\n"+
				"    grid on\n"+
				"    plot(filtSpikes_signal,'g--') % spikes removed\n"+
				"    plot(in_time,filt_signal,'r-') % final filtered signal\n"+
				"    title('signal and filtered signal')\n"+
				"    xlabel('frame index')\n"+
				"    ylabel('luminance')\n"+
				"    legend('input','w/o spikes','filtered')\n"+
				"end\n"+
				"\n"+
				"end\n"+
				"\n"+
				"function out_avg = windowAverage(in_raw,winHalfSize)\n"+
				"\n"+
				"% compute the average of the signal on the window size\n"+
				"Ntot=length(in_raw);\n"+
				"% window and weights before/after current sample\n"+
				"weightGen=abs([1./[-winHalfSize:-1] 1 1./[1:winHalfSize]].^0.5);\n"+
				"winGen=(-winHalfSize):(winHalfSize);\n"+
				"% apply filter on a sliding window\n"+
				"out_avg=zeros(size(in_raw));\n"+
				"for iL=1:Ntot\n"+
				"	idx = intersect(find((winGen+iL)>1),find((winGen+iL)<Ntot));\n"+
				"	winIdx = iL+winGen(idx)-1;\n"+
				"	weight=weightGen(idx);\n"+
				"	weigthTot=sum(weight);\n"+
				"	% weighted averaging on current window samples\n"+
				"	% keyboard;\n"+
				"	out_avg(iL) = sum(in_raw(winIdx).*weight(:)/weigthTot);\n"+
				"end\n"+
				"\n"+
				"\n"+
				"function printTable(filename,values)\n"+
				"% print in a text file\n"+
				"\n"+
				"fid=fopen(filename,'w');\n"+
				"for iVal=1:length(values)\n"+
				"	fprintf(fid,'%.6f\\n',values(iVal));\n"+
				"end\n"+
				"fclose(fid);";
		
		BufferedWriter outDeflickFcn = new BufferedWriter(new FileWriter(outFolderDeflick+"/"+this.deflickFcnName+".m"));
		outDeflickFcn.write(deflickFcnCore);
		outDeflickFcn.close();
		
		System.out.println("Deflickering files written...");
				
	}
}
