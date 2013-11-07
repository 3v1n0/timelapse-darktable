function deflick(luminanceTableFic)
% compute deflickering parameters from luminance table

% retrieve source luminance table
in_signal = load(luminanceTableFic);

% filtering parameters
N_LpFilt     = 50;     % Window size for filtering
N_SpikeFilt = 12;     % Size of filtered spike
dL_max       = 0.05/2; % 'normal' deltaL max (above, it is considered as a spike)
isPlotFlag   = true;   % output a grapics with deflickering source/target curves

% filtering (deflickering target)
[filt_signal] = filtLuminance(in_signal, N_LpFilt, dL_max, N_SpikeFilt, isPlotFlag);

% print target values
[lumPath,lumFic,lumExt] = fileparts(luminanceTableFic);
printTable(fullfile(lumPath,[lumFic '_deflick' lumExt]),filt_signal);

% print graphics
print(fullfile(lumPath,[lumFic '_deflick.png']));

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
filtSpikes_signal=in_signal;
for iShift=1:N_SpikeFilt
    % diff N / N-iShift
    diffSignal=in_signal-in_signal([ones(1,iShift) 1:(end-iShift)]);
    % not rejected if never rejected:
    notRejIdx=intersect(notRejIdx,find(diffSignal<dL_max));
end
% delete spikes and replace them by linear interpolated values
filtSpikes_signal=interp1(in_time(notRejIdx),filtSpikes_signal(notRejIdx),in_time,'linear','extrap');


% FILTERING SIGNAL
% ----------------
% window and weights before/after current sample
weightGen=abs([1./[-N_LpFilt:-1] 1 1./[1:N_LpFilt]].^0.5);
winGen=(-N_LpFilt):(N_LpFilt);
% apply filter on a sliding window
filt_signal=zeros(1,Ntot);
for iL=1:Ntot
	idx = intersect(find((winGen+iL)>1),find((winGen+iL)<Ntot));
	winIdx = iL+winGen(idx);
	weight=weightGen(idx);
	weigthTot=sum(weight);
	% weighted averaging on current window samples
	filt_signal(iL) = sum(filtSpikes_signal(winIdx).*weight/weigthTot);
end

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

function printTable(filename,values)
% print in a text file

fid=fopen(filename,'w');
for iVal=1:length(values)
	fprintf(fid,'%.6f\n',values(iVal));
end
fclose(fid);
