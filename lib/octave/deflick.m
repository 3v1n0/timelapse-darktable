% Note that this file is given for information (or debug purpose on octave)
% the source file used in timelapse-darktable is in deflick package
% class : WriteOctaveDeflickFcn 
% To modify the source : modify WriteOctaveDeflickFcn.java

function deflick(luminanceTableFic)
% compute deflickering parameters from luminance table

% retrieve source luminance table
in_signal = load(luminanceTableFic);
in_signal = in_signal(:); % column

% first : coarse filtering with window averaging
[avgSignal] = windowAverage(in_signal,24);

% standard variation of luminance wrt average
stdLum = std(avgSignal-in_signal);

% advanced filtering parameters
N_LpFilt    = min(50,floor(length(in_signal)/2));   % Window size for filtering
N_SpikeFilt = min(12,floor(length(in_signal)/4+1)); % Size of filtered spike
dL_max      = max(0.02,1.5*stdLum);           % 'normal' deltaL max (above, it is considered as a spike)
isPlotFlag  = true;                                 % output a grapics with deflickering source/target curves

% filtering (deflickering target)
[filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag);

% print target values
[lumPath,lumFic,lumExt] = fileparts(luminanceTableFic);
printTable(fullfile(lumPath,[lumFic '_deflick' lumExt]),filt_signal);

% print graphics
if isPlotFlag  == true
	print(fullfile(lumPath,[lumFic '_deflick.png']));
end

end

function [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag)
% Filtering spikes and averaging input signal
%
% SYNTAX
% [filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt [,isPlotFlag] )
%
% OPTIONS
% isPlotFlag : boolean

if nargin<5
	isPlotFlag=0;
end

Ntot=length(in_signal);
N_SpikeFilt=min(N_SpikeFilt,Ntot);

% FILTERING SPIKES : dL > dL_max
% -------------------------------
in_time=1:Ntot;
notRejIdx=1:Ntot;
[avgSignal] = windowAverage(in_signal,24);
notRejIdx = find(abs(avgSignal-in_signal)<dL_max);
if length(notRejIdx)<2
 % keep 1st and last index
 notRejIdx = unique([1 notRejIdx(:)' length(notRejIdx)]);
end
% delete spikes and replace them by linear interpolated values
filtSpikes_signal=interp1(in_time(notRejIdx),in_signal(notRejIdx),in_time,'linear','extrap');

% final window-averaging 
filt_signal = windowAverage(in_signal,N_LpFilt);

% PLOT
% ----
if isPlotFlag
    figure,
    plot(in_time,in_signal,'bx-') % input signal
    hold on
    grid on
    plot(filtSpikes_signal,'g--') % spikes removed
    plot(in_time,filt_signal,'r-') % final filtered signal
    title('signal and filtered signal')
    xlabel('frame index')
    ylabel('luminance')
    legend('input','w/o spikes','filtered')
end

end

function out_avg = windowAverage(in_raw,winHalfSize)

% compute the average of the signal on the window size
Ntot=length(in_raw);
% window and weights before/after current sample
weightGen=abs([1./[-winHalfSize:-1] 1 1./[1:winHalfSize]].^0.5);
winGen=(-winHalfSize):(winHalfSize);
% apply filter on a sliding window
out_avg=zeros(size(in_raw));
for iL=1:Ntot
	idx = intersect(find((winGen+iL)>1),find((winGen+iL)<Ntot));
	winIdx = iL+winGen(idx)-1;
	weight=weightGen(idx);
	weigthTot=sum(weight);
	% weighted averaging on current window samples
	% keyboard;
	out_avg(iL) = sum(in_raw(winIdx).*weight(:)/weigthTot);
end


function printTable(filename,values)
% print in a text file

fid=fopen(filename,'w');
for iVal=1:length(values)
	fprintf(fid,'%.6f\\n',values(iVal));
end
fclose(fid);
