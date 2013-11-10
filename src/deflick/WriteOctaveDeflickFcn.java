package deflick;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteOctaveDeflickFcn {

	public WriteOctaveDeflickFcn(String outFolderDeflick,String outLuminanceFile) throws IOException {

		// regression on luminance points with octave script (write the "master" script)
		BufferedWriter outOctaveMaster = new BufferedWriter(new FileWriter(outFolderDeflick+"/master.m"));
		outOctaveMaster.write("#!/usr/bin/octave -qf"+"\n");
		outOctaveMaster.write("addpath('"+outFolderDeflick+"');"+"\n");
		outOctaveMaster.write("deflick('"+outFolderDeflick+"/"+outLuminanceFile+"');"+"\n");
		outOctaveMaster.close();

		// write the octave function into a file in the output folder
		String deflickFcn = "function deflick(luminanceTableFic)\n" +
				"% compute deflickering parameters from luminance table\n" + 
				"\n" + 
				"% retrieve source luminance table\n" + 
				"in_signal = load(luminanceTableFic);\n" + 
				"\n" + 
				"% filtering parameters\n" + 
				"N_LpFilt    = min(50,floor(length(in_signal)/2));   % Window size for filtering\n" + 
				"N_SpikeFilt = min(12,floor(length(in_signal)/4+1)); % Size of filtered spike\n" + 
				"dL_max      = max(0.05/2,3*std(in_signal));         % 'normal' deltaL max (above, it is considered as a spike)\n" + 
				"isPlotFlag  = true;                                 % output a grapics with deflickering source/target curves\n" + 
				"\n" + 
				"% filtering (deflickering target)\n" + 
				"[filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag);\n" + 
				"\n" + 
				"% print target values\n" + 
				"[lumPath,lumFic,lumExt] = fileparts(luminanceTableFic);\n" + 
				"printTable(fullfile(lumPath,[lumFic '_deflick' lumExt]),filt_signal);\n" + 
				"\n" + 
				"% print graphics\n" + 
				"if isPlotFlag  == true\n" + 
				"	print(fullfile(lumPath,[lumFic '_deflick.png']));\n" + 
				"end\n" + 
				"\n" + 
				"end\n" + 
				"\n" + 
				"function [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag)\n" + 
				"% Filtering spikes and averaging input signal\n" + 
				"%\n" + 
				"% SYNTAX\n" + 
				"% [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt [,isPlotFlag] )\n" + 
				"%\n" + 
				"% OPTIONS\n" + 
				"% isPlotFlag : boolean\n" + 
				"\n" + 
				"if nargin<5\n" + 
				"	isPlotFlag=0;\n" + 
				"end\n" + 
				"\n" + 
				"Ntot=length(in_signal);\n" + 
				"N_SpikeFilt=min(N_SpikeFilt,Ntot);\n" + 
				"\n" + 
				"% FILTERING SPIKES : dL > dL_max\n" + 
				"% -------------------------------\n" + 
				"in_time=1:Ntot;\n" + 
				"notRejIdx=1:Ntot;\n" + 
				"filtSpikes_signal=in_signal;\n" + 
				"for iShift=1:N_SpikeFilt\n" + 
				"    % diff N / N-iShift\n" + 
				"    diffSignal=in_signal-in_signal([ones(1,iShift) 1:(end-iShift)]);\n" + 
				"    % not rejected if never rejected:\n" + 
				"    notRejIdx=intersect(notRejIdx,find(diffSignal<dL_max));\n" + 
				"end\n" + 
				"% delete spikes and replace them by linear interpolated values\n" + 
				"filtSpikes_signal=interp1(in_time(notRejIdx),filtSpikes_signal(notRejIdx),in_time,'linear','extrap');\n" + 
				"\n" + 
				"\n" + 
				"% FILTERING SIGNAL\n" + 
				"% ----------------\n" + 
				"% window and weights before/after current sample\n" + 
				"weightGen=abs([1./[-N_LpFilt:-1] 1 1./[1:N_LpFilt]].^0.5);\n" + 
				"winGen=(-N_LpFilt):(N_LpFilt);\n" + 
				"% apply filter on a sliding window\n" + 
				"filt_signal=zeros(1,Ntot);\n" + 
				"for iL=1:Ntot\n" + 
				"	idx = intersect(find((winGen+iL)>1),find((winGen+iL)<Ntot));\n" + 
				"	winIdx = iL+winGen(idx);\n" + 
				"	weight=weightGen(idx);\n" + 
				"	weigthTot=sum(weight);\n" + 
				"	% weighted averaging on current window samples\n" + 
				"	filt_signal(iL) = sum(filtSpikes_signal(winIdx).*weight/weigthTot);\n" + 
				"end\n" + 
				"\n" + 
				"% PLOT\n" + 
				"% ----\n" + 
				"if isPlotFlag\n" + 
				"    figure,\n" + 
				"    plot(in_time,in_signal,'bx-') % input signal\n" + 
				"    hold on\n" + 
				"    grid on\n" + 
				"    plot(filtSpikes_signal,'g--') % spikes removed\n" + 
				"    plot(in_time,filt_signal,'r-') % final filtered signal\n" + 
				"    title('signal and filtered signal')\n" + 
				"    xlabel('frame index')\n" + 
				"    ylabel('luminance')\n" + 
				"    legend('input','w/o spikes','filtered')\n" + 
				"end\n" + 
				"\n" + 
				"end\n" + 
				"\n" + 
				"function printTable(filename,values)\n" + 
				"% print in a text file\n" + 
				"\n" + 
				"fid=fopen(filename,'w');\n" + 
				"for iVal=1:length(values)\n" + 
				"	fprintf(fid,'%.6f\\n',values(iVal));\n" + 
				"end\n" + 
				"fclose(fid);";
		
		BufferedWriter outDeflickFcn = new BufferedWriter(new FileWriter(outFolderDeflick+"/deflick.m"));
		outDeflickFcn.write(deflickFcn);
		outDeflickFcn.close();
				
	}
}
